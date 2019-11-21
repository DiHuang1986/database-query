package com.ce.query.grammar;

import java.util.HashMap;
import java.util.Map;

public class GrammarFactory {

    public static final GrammarFactory INSTANCE = new GrammarFactory();
    public static final String DEFAULT = "default";

    private Map<String, IGrammar> map = new HashMap<>();

    GrammarFactory() {
        this.register(new OracleGrammar());
        this.register(new DefaultGrammar());
    }

    public IGrammar get(String key) {
        String lowerKey = key.toLowerCase();
        IGrammar grammar = map.get(lowerKey);
        if (grammar == null) {
            return map.get(DEFAULT.toLowerCase());
        }
        return grammar;
    }

    void register(IGrammar grammar) {
        map.put(grammar.getDatabaseProductName().toLowerCase(), grammar);
    }
}
