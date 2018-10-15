package com.chatbot;

import com.chatbot.resources.CommonResourceBundle;

import java.util.logging.Logger;

/**
 * Helper class for getting a configured <code>Logger</code>.
 */
public class CommonLogger {
    private static final Logger LOGGER = Logger.getLogger("com.chatbot", CommonResourceBundle.class.getName());

    private CommonLogger() {
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
