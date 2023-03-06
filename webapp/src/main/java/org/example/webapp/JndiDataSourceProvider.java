package org.example.webapp;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JndiDataSourceProvider implements DataSourceProvider {

    private static final String JNDI_NAME = "java:/jdbc/visits";

    @Override
    public DataSource getDataSource() {
        Context ctx = null;
        try {
            ctx = new InitialContext();

            return (DataSource) ctx.lookup(JNDI_NAME);

        } catch (NamingException e) {
            throw new DataSourceProviderException(e);
        } finally {
            if (ctx != null) {
                safeCloseContext(ctx);
            }
        }
    }

    private void safeCloseContext(Context ctx) {
        try {
            ctx.close();
        } catch (NamingException e) {
            System.err.println("Could not close context...\n");
            e.printStackTrace(System.err);
        }
    }

}
