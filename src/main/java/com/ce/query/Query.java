package com.ce.query;

import com.ce.query.contract.IRowToEntityHandler;
import com.ce.query.exception.QueryException;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Query {

    private final String WHERE_LIKE_SUFFIX = "__whereLike";
    private String table;
    private String select;
    private List<String> whereRawList = new ArrayList<String>();
    private List<String> whereList = new ArrayList<String>();
    private Map<String, Object[]> whereInList = new HashMap<String, Object[]>();
    private List<String> whereLikeList = new ArrayList<String>();
    private int page = 0;
    private int perPage = 0;
    private String orderBy;
    private String order;
    private String groupBy;
    private String statement;
    private List<String> joins = new ArrayList<String>();
    private Map<String, Object> params = new HashMap<String, Object>();
    private Connection connection;

    public Query(Connection connection) {
        this.connection = connection;
    }

    public static Query connect(Connection connection) {
        return new Query(connection);
    }

    /**
     * set table of Query object
     * @param table
     * @return
     */
    public Query table(String table) {
        this.table = table;
        return this;
    }

    /**
     * set select query of Query object
     * @param select
     * @return
     */
    public Query select(String select) {
        this.select = select;
        return this;
    }

    /**
     * join other table by join criteria
     * @param otherTable
     * @param joinCriteria
     * @return
     */
    public Query join(String otherTable, String joinCriteria) {
        String join = String.format(" join %s on %s ", otherTable, joinCriteria);
        joins.add(join);
        return this;
    }

    /**
     * left join table by join criteria
     * @param otherTable
     * @param joinCriteria
     * @return
     */
    public Query leftJoin(String otherTable, String joinCriteria) {
        String join = String.format(" left join %s on %s ", otherTable, joinCriteria);
        joins.add(join);
        return this;
    }

    /**
     * right join table by join criteria
     * @param otherTable
     * @param joinCriteria
     * @return
     */
    public Query rightJoin(String otherTable, String joinCriteria) {
        String join = String.format(" right join %s on %s ", otherTable, joinCriteria);
        joins.add(join);
        return this;
    }

    /**
     * use statement to execute complex sql query. <br>
     * Be aware, using statement will only respect <code>params</code>
     * <code>
     *     Query.connect(connection)
     *         .statement("select * from people where name = :name")
     *         .param("name", "test")
     *         .where("age", 5) // will be ignored
     *         .all();
     * </code>
     * @param statement
     * @return
     */
    public Query statement(String statement) {
        this.statement = statement;
        return this;
    }

    /**
     * add where criteria, but raw
     * <code>
     *     query.whereRaw("people.age > 20");
     * </code>
     * @param where
     * @return
     */
    public Query whereRaw(String where) {
        this.whereRawList.add(where);
        return this;
    }

    /**
     * add where criteria by only accept `=` operation;
     * <code>
     *     Query.connect(connection)
     *         .table("people")
     *         .where("name", "test")
     *         .all();
     * </code>
     * @param token
     * @param value
     * @return
     */
    public Query where(String token, Object value) {
        this.whereList.add(token);
        this.param(token, value);
        return this;
    }

    /**
     * add where criteria for in list
     * <code>
     *     query.whereIn("name", new String[] {"test 1", "test 2"});
     * </code>
     * @param token
     * @param list
     * @return
     */
    public Query whereIn(String token, Object[] list) {
        if (token == null || "".equals(token.trim()))
            return this;

        this.whereInList.put(token, list);
        return this;
    }

    /**
     * add where like criteria, used to search some field
     * <code>
     *     query.whereLike("name", "David");
     * </code>
     * @param token
     * @param value
     * @return
     */
    public Query whereLike(String token, String value) {
        if (token == null || token.trim().length() == 0)
            return this;

        if (value != null && value.trim().length() > 0) {
            this.whereLikeList.add(token);
            this.param(token + WHERE_LIKE_SUFFIX, "%" + value.toLowerCase() + "%");
        }
        return this;
    }

    /**
     * add paginate
     * @param page
     * @param perPage
     * @return
     */
    public Query paginate(int page, int perPage) {
        this.page = page;
        this.perPage = perPage;
        return this;
    }

    /**
     * add order by
     * @param orderBy
     * @param order
     * @return
     */
    public Query orderBy(String orderBy, String order) {
        this.orderBy = orderBy;
        this.order = order;
        return this;
    }

    /**
     * add group by
     * @param groupBy
     * @return
     */
    public Query groupBy(String groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    /**
     * take a param with a key
     * @param key
     * @param value
     * @return
     */
    public Query param(String key, Object value) {
        this.params.put(key, value);
        return this;
    }

    /**
     * take a map as params, each of them will be applied to Query object as param
     * @param map
     * @return
     */
    public Query params(Map<String, Object> map) {
        this.params.putAll(map);
        return this;
    }

    private void _buildJoin(StringBuffer buffer) {
        // join
        for (int i = 0; i < joins.size(); i++) {
            buffer.append(joins.get(i));
        }
    }

    private void _appendWhere(StringBuffer buffer) {
        if (buffer.toString().contains(" where ")) {
            buffer.append(" and ");
        } else {
            buffer.append(" where ");
        }
    }

    private void _buildWhereRaw(StringBuffer buffer) {
        // where raw
        for (int i = 0; i < whereRawList.size(); i++) {
            String where = whereRawList.get(i);

            _appendWhere(buffer);

            buffer.append(where);
        }
    }

    private void _buildWhere(StringBuffer buffer) {
        // where
        for (int i = 0; i < whereList.size(); i++) {
            String token = whereList.get(i);
            _appendWhere(buffer);
            buffer.append(String.format(" %s = %s ", token, ":" + token));
        }
    }

    void _buildWhereIn(StringBuffer buffer) {
        // where in
        for (Entry<String, Object[]> entry : whereInList.entrySet()) {

            String key = entry.getKey();
            Object[] value = entry.getValue();

            _appendWhere(buffer);

            if (value != null && value.length > 0) {
                String[] names = SQLHelper.generateArrayOfNamedParameters(key, value.length);

                buffer.append(String.format(" %s in ( %s )", key, SQLHelper.generateArrayOfNamedParameterString(key, value.length)));

                for (int i = 0; i < value.length; i++) {
                    param(names[i], value[i]);
                }
            } else {
                buffer.append(" false ");
            }
        }
    }

    private StringBuffer _buildSqlBase() {
        if (table == null)
            throw new QueryException("table is required");

        StringBuffer buffer = new StringBuffer();

        _buildTable(buffer);

        _buildJoin(buffer);

        _buildWhereRaw(buffer);

        _buildWhere(buffer);

        _buildWhereIn(buffer);

        _buildWhereLike(buffer);

        _buildGroupBy(buffer);

        _buildOrderBy(buffer);

        return buffer;
    }

    private void _buildTable(StringBuffer buffer) {
        buffer.append(String.format(" from %s ", table));
    }

    private void _buildOrderBy(StringBuffer buffer) {
        if (orderBy != null) {
            if (!"desc".equals(order)) {
                order = "asc";
            }
            buffer.append(String.format(" order by %s %s ", orderBy, order));
        }
    }

    private void _buildGroupBy(StringBuffer buffer) {
        if (groupBy != null) {
            buffer.append(String.format(" group by %s ", groupBy));
        }
    }

    private void _buildWhereLike(StringBuffer buffer) {
        // where like
        for (int i = 0; i < whereLikeList.size(); i++) {
            String token = whereLikeList.get(i);
            _appendWhere(buffer);
            buffer.append(String.format(" lower( %s ) like %s ", token, ":" + token + WHERE_LIKE_SUFFIX));
        }
    }

    //============= actions

    /**
     * get list of selection, but with pagination predefined if not given
     *
     * @return
     */
    public List<Row> get() {
        String sql = null;

        if (statement != null) {
            sql = statement;
        } else {
            StringBuffer buffer = _buildSqlBase();

            _buildSelect(buffer);

            _buildPagination(buffer);

            sql = buffer.toString();
        }

        return executeQuery(sql);
    }

    private void _buildSelect(StringBuffer buffer) {
        // add select
        if (select == null || "".equals(select.trim()))
            select = "*";
        buffer.insert(0, String.format("select %s ", select));
    }

    private void _buildPagination(StringBuffer buffer) {
        // add pagination
        if (page > 0 && perPage > 0) {
            buffer.append(String.format(" offset %s rows fetch next %s rows only ", (page - 1) * perPage, perPage));
        }
    }

    public <T> List<T> get(IRowToEntityHandler<T> handler) {
        return processRow(get(), handler);
    }

    /**
     * get all query result, will ignore the pagination
     * @return
     */
    public List<Row> all() {
        String sql = null;

        if (statement != null) {
            sql = statement;
        } else {
            StringBuffer buffer = _buildSqlBase();

            _buildSelect(buffer);

            sql = buffer.toString();
        }

        return executeQuery(sql);
    }

    /**
     * get all query result and process with given handler
     * @param handler
     * @param <T>
     * @return
     */
    public <T> List<T> all(IRowToEntityHandler<T> handler) {
        return processRow(all(), handler);
    }

    /**
     * execute query, return boolean as execution result
     * @return
     */
    public boolean execute() {
        String sql = null;

        if (statement != null) {
            sql = statement;
        } else {
            StringBuffer buffer = _buildSqlBase();

            _buildSelect(buffer);
            sql = buffer.toString();
        }

        return execute(sql);
    }

    /**
     * Execute the given SQL statement, and will apply params before executing.
     * <code>
     *     Query q = new Query();
     *     q.param("name", "some");
     *     q.execute("update people set gender = 'male' where name = :name");
     * </code>
     * @param sql
     * @return
     */
    public boolean execute(String sql) {

        NamedParameterStatement statement = null;

        try {
            statement = new NamedParameterStatement(connection, sql);
            _applyParams(statement);
            return statement.execute();
        } catch (SQLException e) {
            throw new QueryException("SQL Exception", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Execute the given SQL statement, and will apply params before executing.
     * <code>
     *     Query q = new Query();
     *     q.param("name", "some");
     *     int result = q.executeUpdate("update people set gender = 'male' where name = :name");
     * </code>
     * @param sql
     * @return
     */
    public int executeUpdate(String sql) {

        NamedParameterStatement statement = null;

        try {
            statement = new NamedParameterStatement(connection, sql);
            _applyParams(statement);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new QueryException("SQL Exception", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Execute the given SQL statement query, and will apply params before executing.
     * <code>
     *     List<Row> rows = Query.connect(conenction)
     *         .param("name", "test")
     *         .executeQuery("select * from people where name = :name");
     * </code>
     * @param sql
     * @return
     */
    public List<Row> executeQuery(String sql) {

        NamedParameterStatement statement = null;
        ResultSet rs = null;

        try {
            statement = new NamedParameterStatement(connection, sql);

            // apply params to statement
            _applyParams(statement);

            // query and extract result set
            rs = statement.executeQuery();
            return _extractResult(rs);
        } catch (SQLException e) {
            throw new QueryException("SQL Exception", e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new QueryException("I/O Exception", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * get the first one selection we found
     *
     * @return
     */
    public Row first() {
        List<Row> result = all();

        if (result.size() > 0)
            return result.get(0);

        return null;
    }

    /**
     * get first query result and process it with given handler
     * @param mapper
     * @param <T>
     * @return
     */
    public <T> T first(IRowToEntityHandler<T> mapper) {
        return mapper.map(this.first());
    }

    /**
     * count selections based on given params
     *
     * @return
     */
    public int count() {
        return count(null);
    }

    public int count(String countStr) {
        if (countStr == null || "".equals(countStr.trim())) countStr = "*";

        StringBuffer buffer = _buildSqlBase();

        buffer.insert(0, String.format("select count(%s) count ", countStr));

        String sql = buffer.toString();

        List<Row> result = executeQuery(sql);

        Object countObject = result.get(0).get("count");

        if (countObject instanceof Long) {
            return ((Long) countObject).intValue();
        }

        if (countObject instanceof BigDecimal) {
            return ((BigDecimal) countObject).intValue();
        }

        return Integer.parseInt(countStr.toString());
    }

    private void _applyParams(NamedParameterStatement statement) throws SQLException {
        // apply params
        for (Entry<String, Object> entry : params.entrySet()) {

            String key = entry.getKey();
            Object value = entry.getValue();

            if (key == null) continue;

            statement.setObject(key, value);
        }
    }

    private List<Row> _extractResult(ResultSet rs) throws SQLException, IOException {
        List<Row> result = new ArrayList<Row>();

        // get result
        while (rs.next()) {

            Row item = new Row();
            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();

            for (int i = 1; i <= count; i++) {

                String key = meta.getColumnLabel(i).toLowerCase();
                Object value = rs.getObject(i);

                /*
                sql query allows multi columns with same name, we can add table name before the column name
                to make it more clear, but still can not prevent name duplication, and will cause more format
                trouble.
                example: select id, username as id, created_at as id from account;
                this query has 3 id are listed, and none of them are same, even with table name, we can not
                identify one from another.
                also, in ResultSet class, these columns are identified by index, while retrieve data by name will only
                return the first found one.
                So, we keep the same pattern here. Always use the first found value while has duplicated key.
                also, developer should be informed by this information.
                 */
                // if key is added, ignore this value
                if (item.containsKey(key)) continue;

                item.put(key, value);
            }

            result.add(item);
        }

        return result;
    }

    private <T> List<T> processRow(List<Row> rows, IRowToEntityHandler<T> handler) {

        List<T> result = new ArrayList<T>();

        for (int i = 0; i < rows.size(); i++) {
            result.add(handler.map(rows.get(i)));
        }

        return result;
    }

}
