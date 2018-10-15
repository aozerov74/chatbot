package com.chatbot.engine;

import org.glassfish.hk2.api.Factory;

public class QueryServerFactory implements Factory<QueryServerContext> {
    private QueryServerContext context;
    public QueryServerFactory(QueryServerContext context) {
        this.context = context;
    }
    @Override
    public QueryServerContext provide() {
        return context;
    }
    @Override
    public void dispose(QueryServerContext t) {
    }
}