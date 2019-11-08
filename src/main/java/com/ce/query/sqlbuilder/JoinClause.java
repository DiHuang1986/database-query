package com.ce.query.sqlbuilder;

class JoinClause {
    public String table;
    public String condition;
    public JoinType type;

    public JoinClause(String table, String condition, JoinType type) {
        this.table = table;
        this.condition = condition;
        this.type = type;
    }
}
