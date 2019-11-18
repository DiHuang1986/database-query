package com.ce.query;

import com.ce.query.contract.IRow;
import com.ce.query.contract.IRowToEntityHandler;

class PeopleRowMapping implements IRowToEntityHandler<People> {
    @Override
    public People map(IRow row) {
        People p = new People();
        p.id = row.getAs("id", Integer.class);
        p.name = row.getAs("name", String.class);
        p.age = row.getAs("age", Integer.class);
        return p;
    }
}
