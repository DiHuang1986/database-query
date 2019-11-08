package com.ce.query.sqlbuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SqlBuilder {

    private List<String> columnList = new ArrayList<>();
    private List<String> fromList = new ArrayList<>();
    private List<JoinClause> joinClauseList = new ArrayList<>();
    private List<WhereClause> whereClauseList = new ArrayList<>();
    private List<GroupClause> groupClauseList = new ArrayList<>();
    private List<HavingClause> havingClauseList = new ArrayList<>();
    private List<OrderByClause> orderByClauseList = new ArrayList<>();
    private Map<String, Object> params = new ConcurrentHashMap<>();
    private int limit;
    private int offset;
    private int paramCount;

    public SqlBuilder select(String... columns) {
        if (columns == null || columns.length == 0) {
            columns = new String[]{"*"};
        }
        this.columnList = Arrays.asList(columns);
        return this;
    }

    public SqlBuilder from(String... tables) {
        this.fromList.addAll(Arrays.asList(tables));
        return this;
    }

    public SqlBuilder join(String table, String condition) {
        this.joinClauseList.add(new JoinClause(table, condition, JoinType.JOIN));
        return this;
    }

    public SqlBuilder leftJoin(String table, String condition) {
        this.joinClauseList.add(new JoinClause(table, condition, JoinType.LEFT_JOIN));
        return this;
    }

    public SqlBuilder rightJoin(String table, String condition) {
        this.joinClauseList.add(new JoinClause(table, condition, JoinType.RIGHT_JOIN));
        return this;
    }

    public SqlBuilder where(String where) {
        this.whereClauseList.add(new WhereClause(where, ConditionType.AND));
        return this;
    }

    public SqlBuilder orWhere(String where) {
        this.whereClauseList.add(new WhereClause(where, ConditionType.OR));
        return this;
    }

    private SqlBuilder where(String key, Object value, ConditionType type) {
        String id = generateParamId("where");
        this.whereClauseList.add(new WhereClause(String.format(
                "%s = :%s", key, id
        ), type));
        this.param(id, value);
        return this;
    }

    public SqlBuilder where(String key, Object value) {
        return this.where(key, value, ConditionType.AND);
    }

    public SqlBuilder orWhere(String key, Object value) {
        return this.where(key, value, ConditionType.OR);
    }

    public SqlBuilder whereIn(String key, List<Object> values) {
        List<String> ids = new ArrayList<>();
        values.stream().forEach(o -> {
            String id = generateParamId(key);
            ids.add(":" + id);
            this.param(id, o);
        });
        return this.where(String.format("%s in (%s)", key, String.join(", ", ids)));
    }

    public SqlBuilder whereExists(String nestSql) {
        return this.where(String.format("exist (%s)", nestSql));
    }

    public SqlBuilder whereNull(String key) {
        return this.where(String.format("%s is null", key));
    }

    public SqlBuilder whereNotNull(String key) {
        return this.where(String.format("%s is not null", key));
    }

    public SqlBuilder groupBy(String... groups) {
        List<GroupClause> groupClauses = Arrays.asList(groups).stream().map(g -> {
            return new GroupClause(g);
        }).collect(Collectors.toList());
        this.groupClauseList.addAll(groupClauses);
        return this;
    }

    public SqlBuilder param(String key, Object value) {
        this.params.put(key, value);
        return this;
    }

    public SqlBuilder params(Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }

    private String generateParamId(String prefix) {
        if (prefix == null || prefix.length() == 0) {
            prefix = "param";
        }
        return prefix + "_" + paramCount++;
    }
}

