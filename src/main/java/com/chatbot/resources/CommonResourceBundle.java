package com.chatbot.resources;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;

public class CommonResourceBundle extends ListResourceBundle implements CommonMessageKeys {
    public Object[][] getContents() {
        return contents;
    }

    static final Object[][] contents = {
            {CHATBOT_API_STARTED, "Chatbot API started on {0}"},
            //annotation for ODL
            {"oracle.core.ojdl.logging.MessageIdKeyResourceBundle", ""}
    };

    public static ResourceBundle getBundle() {
        return ResourceBundle.getBundle(CommonResourceBundle.class.getName(), Locale.ROOT);
    }
}
