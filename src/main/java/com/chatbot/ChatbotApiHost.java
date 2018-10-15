package com.chatbot;

import com.chatbot.api.ChatbotResource;
import com.chatbot.engine.QueryServerContext;
import com.chatbot.engine.QueryServerFactory;
import com.chatbot.resources.CommonMessageKeys;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.inject.Singleton;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatbotApiHost {
    private static final Logger LOGGER = CommonLogger.getLogger();

    private final String name = "Chatbot API";
    private Server jettyServer;

    private QueryServerContext context = QueryServerContext.getInstance();

    public static void main(String[] args) throws Exception {
        new ChatbotApiHost().start();
    }

    private HttpServerConfig getHttpServerConfig() {
        return new HttpServerConfig(8089, 25, 8);
    }

    protected void start() throws Exception {
        System.setProperty("http.proxyHost", "www-proxy.us.oracle.com");
        System.setProperty("http.proxyPort", "80");

        System.setProperty("https.proxyHost", "www-proxy.us.oracle.com");
        System.setProperty("https.proxyPort", "80");

        HttpServerConfig config = getHttpServerConfig();

        Handler apiHandler = new HttpServletContextHandlerBuilder()
                .setContextPath("/chatbot-api/v1")
                .setHealthCheck(true)
                .setPackages(false, ChatbotResource.class.getPackage().getName())
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bindFactory(new QueryServerFactory(context)).to(QueryServerContext.class).in(Singleton.class);
                    }
                })
                .register(MultiPartFeature.class)
                .register(RolesAllowedDynamicFeature.class)
                .setTitle(getName())
                .build();

        jettyServer = new HttpServerBuilder()
                .addHandler(apiHandler)
                .setConfig(config)
                .build();

        jettyServer.start();

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, CommonMessageKeys.CHATBOT_API_STARTED, jettyServer.getURI());
        }
    }

    protected String getName() {
        return name;
    }
}
