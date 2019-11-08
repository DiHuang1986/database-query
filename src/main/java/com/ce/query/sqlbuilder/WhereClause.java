package com.ce.query.sqlbuilder;

public class WhereClause {
    public String where;
    public ConditionType type;

    public WhereClause(String where, ConditionType type) {
        this.where = where;
        this.type = type;
    }
}
