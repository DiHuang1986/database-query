package com.ce.query.grammar;

public interface IGrammar {

    String getDatabaseProductName();

    void handleSkip(StringBuilder buffer, int skip);


    void handleTake(StringBuilder buffer, int take);
}
