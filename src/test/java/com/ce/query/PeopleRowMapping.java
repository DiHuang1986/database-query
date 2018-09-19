package com.ce.query;

import com.ce.query.contract.IRow;
import com.ce.query.contract.IRowToEntityHandler;

class PeopleRowMapping implements IRowToEntityHandler<People> {
    @Override
    public People map(IRow row) {
        People p = new People();
        p.id = row.getAsInteger("id");
        p.name = row.getAsString("name");
        p.age = row.getAsInteger("age");
        return p;
    }
}
