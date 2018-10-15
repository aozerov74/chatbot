package com.chatbot.engine;

import opennlp.tools.chatbot.wrapper.BotResponse;

public class ChatbotResponse {
    private BotResponse botResponse;
    private String url;
    private int queryType;

    public int getQueryType() {
        return queryType;
    }

    public void setQueryType(int queryType) {
        this.queryType = queryType;
    }

    public BotResponse getBotResponse() {
        return botResponse;
    }

    public void setBotResponse(BotResponse botResponse) {
        this.botResponse = botResponse;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
