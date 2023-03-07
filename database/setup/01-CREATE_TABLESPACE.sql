ALTER SESSION SET CONTAINER = ORCLPDB1;

CREATE TABLESPACE VISITS_MAIN
    DATAFILE 'VISITS.dbf' SIZE 100M REUSE AUTOEXTEND ON NEXT 10M
    EXTENT MANAGEMENT LOCAL AUTOALLOCATE
    LOGGING
    ONLINE
    SEGMENT SPACE MANAGEMENT AUTO
;

CREATE TEMPORARY TABLESPACE VISITS_TEMP
    TEMPFILE 'VISITS_TEMP.dbf' SIZE 100M REUSE AUTOEXTEND ON NEXT 10M
;