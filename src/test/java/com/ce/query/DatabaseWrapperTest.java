package com.ce.query;

import com.ce.query.exception.QueryException;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class DatabaseWrapperTest {
    static DataSource dataSource;
    static DatabaseWrapper databaseWrapper;

    {
        try {
            Class.forName("org.h2.Driver");
            dataSource = new JdbcDataSource();
            ((JdbcDataSource) dataSource).setUrl("jdbc:h2:mem:queryDb;DB_CLOSE_DELAY=-1");
            databaseWrapper = new DatabaseWrapper(dataSource);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void before() throws SQLException {
        Connection conn = dataSource.getConnection();
        String initSql = "drop table if exists people;" +
                "drop table if exists student;" +
                "drop table if exists exam_score;" +
                "create table people (id integer, name varchar, age integer);" +
                "insert into people values (1, 'TEST 1', 1);" +
                "insert into people values (2, 'TEST 2', 2);" +
                "insert into people values (3, 'TEST 3', 3);" +
                "insert into people values (4, 'TEST 4', 4);" +
                "create table student (id integer, name varchar, age integer);" +
                "insert into student values (1, 'student 1', 1);" +
                "insert into student values (2, 'student 2', 2);" +
                "insert into student values (3, 'student 3', 3);" +
                "create table exam_score (id integer, student_id integer, score integer);" +
                "insert into exam_score values(1, 1, 10);" +
                "insert into exam_score values(2, 2, 20);" +
                "insert into exam_score values(3, 2, 30);" +
                "insert into exam_score values(4, 2, 40);";
        Query.connect(conn)
                .statement(initSql)
                .execute();
        conn.close();
    }

    @After
    public void after() throws SQLException {
        Connection conn = dataSource.getConnection();
        String initSql = "drop table if exists people;" +
                "drop table if exists student;" +
                "drop table if exists exam_score;";
        Query.connect(conn)
                .statement(initSql)
                .execute();
        conn.close();
    }

    @Test
    public void execute() throws SQLException {
        String s = databaseWrapper.execute(connection -> {

            List<Row> rows = Query.connect(connection)
                    .table("people")
                    .all();
            assertThat(rows.size()).isEqualTo(4);
            Query.connect(connection)
                    .statement("insert into people values (10, 'TEST 10', 10)")
                    .execute();
            int count = Query.connect(connection)
                    .table("people")
                    .count();
            assertThat(count).isEqualTo(5);

            Query.connect(connection)
                    .statement("delete from people where id = :id")
                    .param("id", 10)
                    .execute();
            count = Query.connect(connection)
                    .table("people")
                    .count();
            assertThat(count).isEqualTo(4);
            return "sts";
        });
    }

    @Test
    public void executeWithException() throws SQLException {

        assertThatExceptionOfType(QueryException.class)
                .isThrownBy(() -> {
                    databaseWrapper.execute(connection -> {
                        Query.connect(connection)
                                .statement("insert into people values (10, 'TEST 10', 10)")
                                .execute();
                        if (true) throw new RuntimeException();
                    });
                });

        // people 10 will be saved
        Row row = Query.connect(dataSource.getConnection())
                .table("people")
                .where("id", 10)
                .first();
        assertThat(row).isNotNull();
    }

    @Test
    public void transaction() throws SQLException {
        try {
            databaseWrapper.transaction(connection -> {
                Query.connect(connection)
                        .statement("insert into people values (10, 'TEST 10', 10)")
                        .execute();
                Query.connect(connection)
                        .statement("update people set name = 'TEST 1 NEW NAME' where id = 1")
                        .execute();
                if (true) throw new RuntimeException();
            });
        } catch (Exception e) {
            assertThat(e).isOfAnyClassIn(QueryException.class);
        }

        // people 10 will be removed because transaction is suspended
        Row row = Query.connect(dataSource.getConnection())
                .table("people")
                .where("id", 10)
                .first();
        assertThat(row).isNull();
        // modification of people 1 will be restored
        row = Query.connect(dataSource.getConnection())
                .table("people")
                .where("id", 1)
                .first();
        assertThat(row.get("name")).isNotEqualTo("TEST 1 NEW NAME");
    }

    @Test
    public void givenMultiThread_whenCloseEachOwnConnection_thenNoException() throws ExecutionException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(100);
        AtomicInteger count = new AtomicInteger(100);
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            final int index = i;
            executorService.execute(() -> {
                databaseWrapper.transaction(connection -> {
                    try {
                        System.out.println("t[" + index + "]: start");
                        Thread.sleep(index * 10);
                        if (connection.isClosed()) {
                            throw new RuntimeException("connection closed");
                        }
                        System.out.println("t[" + index + "]: end");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                    return 1;
                });

                count.decrementAndGet();
            });
        }

        latch.await();

        echo("duration is " + (System.currentTimeMillis() - start));
        assertThat(count.get()).isEqualTo(1);
    }

    public void echo(Object o) {
        System.out.println(o);
    }
}