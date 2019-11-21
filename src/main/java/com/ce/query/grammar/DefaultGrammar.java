package com.ce.query.grammar;

public class DefaultGrammar extends AbstractGrammar {


    @Override
    public String getDatabaseProductName() {
        return GrammarFactory.DEFAULT;
    }
}
