package com.ce.query.grammar;

public interface IGrammar {

    String getDatabaseProductName();

    void handleSkip(StringBuffer buffer, int skip);


    void handleTake(StringBuffer buffer, int take);
}
