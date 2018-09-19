package com.ce.query;

import static org.assertj.core.api.Assertions.*;

import com.ce.query.contract.IRow;
import com.ce.query.contract.IRowToEntityHandler;
import org.junit.*;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QueryTest {

    private static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:queryDb;DB_CLOSE_DELAY=-1");
        return conn;
    }

    private static void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @BeforeClass
    public static void beforeClass () throws SQLException {
        Connection conn = getConnection();
        String initSql = "create table people (id integer, name varchar, age integer);" +
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
        closeConnection(conn);
    }

    Connection connection;

    @Before
    public void setup() throws SQLException {
        connection = getConnection();
    }

    @After
    public void after() throws SQLException {
        closeConnection(connection);
    }

    @Test
    public void table() throws SQLException {
        List<Row> rows = Query.connect(connection)
                .table("people")
                .get();
        assertThat(rows.size()).isEqualTo(4);

        rows = Query.connect(connection)
                .table("student")
                .get();
        assertThat(rows.size()).isEqualTo(3);
    }

    @Test
    public void select() {
        List<Row> rows = Query.connect(connection)
                .table("people")
                .select("name")
                .get();
        assertThat(rows.get(0).size()).isEqualTo(1);

        rows = Query.connect(connection)
                .table("people")
                .select("name, age")
                .get();
        assertThat(rows.get(0).size()).isEqualTo(2);

        rows = Query.connect(connection)
                .table("people")
                .select("id, name, age")
                .get();
        assertThat(rows.get(0).size()).isEqualTo(3);
    }

    @Test
    public void join() {
        List<Row> rows = Query.connect(connection)
                .table("student")
                .join("exam_score", "student.id = exam_score.student_id")
                .get();

        assertThat(rows.size()).isEqualTo(4);
        assertThat(rows.get(0).size()).isGreaterThan(3);
        assertThat(rows.get(0).get("score")).isEqualTo(10);
    }

    @Test
    public void leftJoin() {
        List<Row> rows = Query.connect(connection)
                .table("student")
                .leftJoin("exam_score", "student.id = exam_score.student_id")
                .get();

        assertThat(rows.size()).isEqualTo(5);
        assertThat(rows.get(0).size()).isGreaterThan(3);
    }

    @Test
    public void rightJoin() {
        List<Row> rows = Query.connect(connection)
                .table("student")
                .rightJoin("exam_score", "student.id = exam_score.student_id")
                .get();

        assertThat(rows.size()).isEqualTo(4);
        assertThat(rows.get(0).size()).isGreaterThan(3);
    }

    @Test
    public void statement() {
        List<Row> rows = Query.connect(connection)
                .statement("select * from people")
                .get();
        assertThat(rows.size()).isEqualTo(4);
    }

    @Test
    public void whereRaw() {
        List<Row> rows = Query.connect(connection)
                .table("people")
                .whereRaw("name = 'TEST 1'")
                .get();
        assertThat(rows.size()).isEqualTo(1);
        assertThat(rows.get(0).get("id")).isEqualTo(1);
    }

    @Test
    public void where() {
        List<Row> rows = Query.connect(connection)
                .table("people")
                .where("name", "TEST 1")
                .get();
        assertThat(rows.size()).isEqualTo(1);
        assertThat(rows.get(0).get("id")).isEqualTo(1);
    }

    @Test
    public void whereIn() {
        List<Row> rows = Query.connect(connection)
                .table("people")
                .whereIn("name", new String[] { "TEST 1", "TEST 2" })
                .get();
        assertThat(rows.size()).isEqualTo(2);
        assertThat(rows.get(0).get("id")).isEqualTo(1);

        List<Row> nextRows = Query.connect(connection)
                .table("people")
                .whereIn("name", new String[0])
                .get();
        assertThat(nextRows.size()).isEqualTo(0);
    }

    @Test
    public void whereLike() {
        List<Row> rows = Query.connect(connection)
                .table("people")
                .whereLike("name", "EST 1")
                .get();
        assertThat(rows.size()).isEqualTo(1);
        assertThat(rows.get(0).get("id")).isEqualTo(1);
    }

    @Test
    public void paginate() {
        List<Row> rows = Query.connect(connection)
                .table("people")
                .paginate(2, 2)
                .get();
        assertThat(rows.size()).isEqualTo(2);
        assertThat(rows.get(0).get("id")).isEqualTo(3);
    }

    @Test
    public void orderBy() {
        List<Row> rows = Query.connect(connection)
                .table("people")
                .orderBy("name", "desc")
                .get();
        assertThat(rows.size()).isEqualTo(4);
        assertThat(rows.get(0).get("id")).isEqualTo(4);
    }

    @Test
    public void groupBy() {
        List<Row> rows = Query.connect(connection)
                .table("exam_score")
                .select("student_id, sum(score) sum")
                .groupBy("student_id")
                .get();
        assertThat(rows.size()).isEqualTo(2);
        assertThat(rows.get(1).getAsInteger("sum")).isEqualTo(90);
    }

    @Test
    public void param() {
        List<Row> rows = Query.connect(connection)
                .table("people")
                .whereRaw("age > :age")
                .param("age", 1)
                .get();
        assertThat(rows.size()).isEqualTo(3);
        assertThat(rows.get(0).get("age")).isEqualTo(2);
    }

    @Test
    public void params() {
        Map<String, Object> map = new HashMap();
        map.put("age", 1);
        List<Row> rows = Query.connect(connection)
                .table("people")
                .whereRaw("age > :age")
                .params(map)
                .get();
        assertThat(rows.size()).isEqualTo(3);
        assertThat(rows.get(0).get("age")).isEqualTo(2);
    }

    @Test
    public void get() {
        List<People> list = Query.connect(connection)
                .table("people")
                .get(new PeopleRowMapping());

        assertThat(list.size()).isEqualTo(4);
        assertThat(list.get(0).name).isEqualToIgnoringCase("TEST 1");
    }

    @Test
    public void all() {

        List<Row> rows = Query.connect(connection)
                .table("people")
                .all();

        assertThat(rows.size()).isEqualTo(4);
    }

    @Test
    public void first() {
        Row row = Query.connect(connection)
                .table("people")
                .first();

        assertThat(row).isNotNull();
        assertThat(row.getAsInteger("id")).isEqualTo(1);
    }

    @Test
    public void first1() {
        People people = Query.connect(connection)
                .table("people")
                .first(new PeopleRowMapping());

        assertThat(people).isNotNull();
        assertThat(people.id).isEqualTo(1);
    }

    @Test
    public void count() {
        int count = Query.connect(connection)
                .table("people")
                .count();
        assertThat(count).isEqualTo(4);
    }

    @Test
    public void count1() {
        int count = Query.connect(connection)
                .table("exam_score")
                .count("distinct student_id");
        assertThat(count).isEqualTo(2);
    }
}

