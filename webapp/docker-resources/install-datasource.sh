#!/usr/bin/env bash

RED=$'\e[31m'
GREEN=$'\e[32m'
RESET=$'\e[0m'
OJDBC_DRIVER_VERSION=11.2.0.4
JBOSS_CLI=$JBOSS_HOME/bin/jboss-cli.sh

function info() {
    printf "%s==> %s%s\n" "${GREEN}" "$*" "${RESET}"
}

function error() {
    printf "%s==> ERROR: %s%s\n" "${RED}" "$*" "${RESET}"
}

function safe_run() {
    "$@"
    local exit_code=$?
    if [ "${exit_code}" -ne 0 ]; then
        error "The command '$*' exited with non-zero exit code ${exit_code}."
        exit "${exit_code}"
    fi
}

function check_server_state() {
    info "Checking server state"
    ${JBOSS_CLI} -c ":read-attribute(name=server-state)"
}

info "Starting JBoss"
${JBOSS_HOME}/bin/standalone.sh &

info "Waiting for the server to boot"
until check_server_state 2> /dev/null | grep -q running; do
    check_server_state 2> /dev/null;
    sleep 1;
done
info "JBoss has started up"

info "Downloading OJDBC driver"
OJDBC_DRIVER="/tmp/ojdbc6-${OJDBC_DRIVER_VERSION}.jar"
safe_run curl --location \
     --output "${OJDBC_DRIVER}" \
     --url "https://repo1.maven.org/maven2/com/oracle/database/jdbc/ojdbc6/${OJDBC_DRIVER_VERSION}/ojdbc6-${OJDBC_DRIVER_VERSION}.jar"

info "Adding OJDBC module"
MODULE_NAME="com.oracle.ojdbc"
safe_run ${JBOSS_CLI} --connect \
             --command="module add --name=${MODULE_NAME} --resources=${OJDBC_DRIVER} --dependencies=javax.api,javax.transaction.api"

info "Adding OJDBC driver"
safe_run ${JBOSS_CLI} --connect \
             --command="/subsystem=datasources/jdbc-driver=ojdbc:add(driver-name=ojdbc,driver-module-name=${MODULE_NAME},driver-class-name=oracle.jdbc.OracleDriver)"

info "Creating a new datasource"
safe_run ${JBOSS_CLI} --connect --command="data-source add
        --name=VisitsDS
        --jndi-name=java:/jdbc/visits
        --user-name=visitsusr
        --password=visitspwd
        --driver-name=ojdbc
        --connection-url=jdbc:oracle:thin:@//oracle-db:1521/orclpdb1
        --max-pool-size=25
        --blocking-timeout-wait-millis=5000
        --enabled=true
        --jta=true
        --use-ccm=false
        --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.oracle.OracleValidConnectionChecker
        --background-validation=true
        --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.oracle.OracleExceptionSorter"

info "Shutting down JBoss and Cleaning up"
safe_run ${JBOSS_CLI} --connect --command=":shutdown"

rm -f /tmp/*.jar