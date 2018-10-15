package com.chatbot.engine;

public final class QueryUtils {
    public static final String isWebDomainInQuery(String query){
		String[] tokensInQuery = query.split(" ");
		for(String t: tokensInQuery){
			if (t.endsWith(".com") || t.endsWith(".biz") || t.endsWith(".edu"))
				return t;
		}
		return null;
	}
}
