package com.chatbot.engine;

import javax.inject.Singleton;

@Singleton
public class QueryServerContext {
    private final static QueryServerContext INSTANCE = new QueryServerContext();
    private QueryServer server = new QueryServer();

    private QueryServerContext() {
    }

    public static QueryServerContext getInstance() {
        return INSTANCE;
    }
    public QueryServer getQueryServer() {
        return server;
    }
}
