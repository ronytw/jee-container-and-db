package org.example.webapp;

import javax.sql.DataSource;

public interface DataSourceProvider {
    DataSource getDataSource();

    class DataSourceProviderException extends RuntimeException {
        public DataSourceProviderException(Throwable cause) {
            super(cause);
        }
    }
}
