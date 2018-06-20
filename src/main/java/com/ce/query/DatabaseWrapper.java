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
    private Connection connection;

    private boolean isInActiveTransaction = false;
    private boolean previousAutoCommit = false;

    public DatabaseWrapper(DataSource ds) {
        if(ds == null) {
            throw new QueryException("data source is required");
        }
        this.dataSource = ds;
    }

    public synchronized <T> T execute(IDatabaseExecution<T> execution) {
        boolean canCloseConnection = false;

        try {
            canCloseConnection = openConnection();

            T result = execution.execute(connection);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new QueryException(e);
        } finally {
            // if can close, close the connection
            if (canCloseConnection) {
                closeConnection();
            }
        }
    }

    public synchronized <T> T transaction(IDatabaseExecution<T> execution) {
        boolean canCloseConnection = false;
        boolean canCommit = false;

        try {
            // open connection and transaction
            canCloseConnection = openConnection();
            canCommit = openTransaction(connection);

            T result = execution.execute(connection);

            // close transaction
            if (canCommit) {
                closeTransaction(connection);
            }

            return result;
        }
        // sql exception, throw QueryException
        catch (SQLException se) {
            se.printStackTrace();
            rollback();
            throw new QueryException(se);
        }
        // runtime exception, re-throw it
        catch (RuntimeException re) {
            re.printStackTrace();
            rollback();
            throw re;
        }
        // catch ANY exception, will rollback
        catch (Exception e) {
            e.printStackTrace();
            rollback();
            throw new QueryException(e);
        }
        // finally close the connection
        finally {
            if (canCloseConnection) {
                closeConnection();
            }
        }
    }

    public synchronized void execute(IDatabaseExecutionVoid execution) {
        this.execute(connection -> {
            execution.execute(connection);
            return null;
        });
    }

    public synchronized void transaction(IDatabaseExecutionVoid execution) {
        this.transaction(connection -> {
            execution.execute(connection);
            return null;
        });
    }


    /*
    openConnection method
     */
    private boolean openConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = dataSource.getConnection();
            return true;
        }

        return false;
    }

    /*
    openTransaction method

    if we already in a transaction, will return false, means we do NOT have right to commit the transaction.
    if we are not in a active transaction, we will mark the wrapper `in active transaction`, and return true,
    means we have right to commit the transaction
     */
    private boolean openTransaction(Connection connection) throws SQLException {
        if (isInActiveTransaction) {
            return false;
        }

        isInActiveTransaction = true;
        previousAutoCommit = connection.getAutoCommit();

        // turn off auto commit
        connection.setAutoCommit(false);

        return true;
    }

    private void closeTransaction(Connection connection) throws SQLException {
        if (!isInActiveTransaction) {
            return;
        }

        connection.commit();

        isInActiveTransaction = false;

        // reset auto commit
        connection.setAutoCommit(previousAutoCommit);
    }

    private boolean closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new ConnectionFailException(e);
        }

        // clear connection
        connection = null;
        return true;
    }

    private void rollback() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        isInActiveTransaction = false;
    }

}
