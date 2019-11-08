package com.ce.query;

import com.ce.query.contract.IDatabaseExecution;
import com.ce.query.contract.IDatabaseExecutionVoid;
import com.ce.query.exception.ConnectionFailException;
import com.ce.query.exception.QueryException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by dhuang on 2/23/2018.
 */
public class DatabaseWrapper {

    private DataSource dataSource;

    // all method are synchronized, however, different thread access different method still share same connection.
    // which might be closed by other thread
    private ThreadLocal<Connection> threadLocalConnection = new ThreadLocal<>();

    private ThreadLocal<Boolean> threadLocalIsInActiveTransaction = new ThreadLocal<>();
    private ThreadLocal<Boolean> threadLocalPreviousAutoCommit = new ThreadLocal<>();

    public DatabaseWrapper(DataSource ds) {
        if (ds == null) {
            throw new QueryException("data source is required");
        }
        this.dataSource = ds;
    }

    public <T> T execute(IDatabaseExecution<T> execution) {
        boolean isLocalOpenedConnection = false;

        try {
            isLocalOpenedConnection = openConnection();

            T result = execution.execute(threadLocalConnection.get());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new QueryException(e);
        } finally {
            // if can close, close the connection
            if (isLocalOpenedConnection) {
                closeConnection();
            }
        }
    }

    public <T> T transaction(IDatabaseExecution<T> execution) {
        boolean isLocalOpenedConnection = false;
        boolean canCommit;

        try {
            // open connection and transaction
            isLocalOpenedConnection = openConnection();
            canCommit = openTransaction(threadLocalConnection.get());

            T result = execution.execute(threadLocalConnection.get());

            // close transaction
            if (canCommit) {
                closeTransaction(threadLocalConnection.get());
            }

            return result;
        }
        // catch ANY exception, will rollback
        catch (Exception e) {
            e.printStackTrace();
            rollback(threadLocalConnection.get());
            throw new QueryException(e);
        }
        // finally close the connection
        finally {
            if (isLocalOpenedConnection) {
                closeConnection();
            }
        }
    }

    public void execute(IDatabaseExecutionVoid execution) {
        this.execute(connection -> {
            execution.execute(connection);
            return null;
        });
    }

    public void transaction(IDatabaseExecutionVoid execution) {
        this.transaction(connection -> {
            execution.execute(connection);
            return null;
        });
    }


    /**
     * open connection, if newly opened connection, return true <br>
     * otherwise, return false
     *
     * @return
     * @throws SQLException
     */
    private boolean openConnection() throws SQLException {
        if (threadLocalConnection.get() == null
                || threadLocalConnection.get().isClosed()
        ) {
            threadLocalConnection.set(dataSource.getConnection());
            return true;
        }

        return false;
    }

    /**
     * if is new opened transaction, return true <br/>
     * otherwise, return false <br/>
     * <br/>
     *
     * @param connection
     * @return
     * @throws SQLException
     */
    private boolean openTransaction(Connection connection) throws SQLException {
        if (Boolean.TRUE.equals(threadLocalIsInActiveTransaction.get())) {
            return false;
        }

        threadLocalIsInActiveTransaction.set(Boolean.TRUE);
        threadLocalPreviousAutoCommit.set(connection.getAutoCommit());

        // turn off auto commit
        connection.setAutoCommit(false);

        return true;
    }

    private void closeTransaction(Connection connection) throws SQLException {
        if (!Boolean.TRUE.equals(threadLocalIsInActiveTransaction.get())) {
            return;
        }

        connection.commit();

        threadLocalIsInActiveTransaction.set(Boolean.FALSE);

        // reset auto commit
        connection.setAutoCommit(threadLocalPreviousAutoCommit.get());
    }

    private boolean closeConnection() {
        try {
            if (threadLocalConnection.get() != null) {
                threadLocalConnection.get().close();
            }
        } catch (SQLException e) {
            throw new ConnectionFailException(e);
        }

        // clear connection
        threadLocalConnection.set(null);
        return true;
    }

    private void rollback(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        threadLocalIsInActiveTransaction.set(Boolean.FALSE);
    }

}
