package com.ce.query.grammar;

public abstract class AbstractGrammar implements IGrammar {

    @Override
    public void handleSkip(StringBuffer buffer, int skip) {
        buffer.append(String.format(" offset %s rows ", skip));
    }

    @Override
    public void handleTake(StringBuffer buffer, int take) {
        buffer.append(String.format(" fetch next %s rows only ", take));
    }
}
