package com.chatbot;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for configuring a Jetty Server.
 *
 * NOTE: this will always add a <code>DiagnosticServerFilter</code> to propagate the tenant id and execution context.
 */
public class HttpServerBuilder {
    // default is random port
    private int port = 0;

    // default is whatever Jetty chooses
    private int maxThreads = -1;
    private int minThreads = -1;

    private int requestHeaderSize = 16384;

    // default is 1 acceptor per CPU
    private int acceptors = Runtime.getRuntime().availableProcessors();

    // default is whatever jetty chooses
    private int selectors = -1;

    private List<Handler> handlers = new ArrayList<>();

    public HttpServerBuilder addHandler(Handler handler) {
        this.handlers.add(handler);
        return this;
    }

    public HttpServerBuilder setConfig(HttpServerConfig config) {
        assert config != null;

        this.port = config.getPort();
        this.maxThreads = config.getMaxThreads();
        this.minThreads = config.getMinThreads();

        return this;
    }

    public HttpServerBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public HttpServerBuilder setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        return this;
    }

    public HttpServerBuilder setMinThreads(int minThreads) {
        this.minThreads = minThreads;
        return this;
    }

    public HttpServerBuilder setRequestHeaderSize(int requestHeaderSize) {
        this.requestHeaderSize = requestHeaderSize;
        return this;
    }

    public HttpServerBuilder setAcceptors(int acceptors) {
        this.acceptors = acceptors;
        return this;
    }

    public HttpServerBuilder setSelectors(int selectors) {
        this.selectors = selectors;
        return this;
    }

    public Server build() {
        QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads);

        Server server = new Server(threadPool);

        ContextHandlerCollection handlerCollection = new ContextHandlerCollection();
        handlerCollection.setHandlers(handlers.toArray(new Handler[0]));
        server.setHandler(handlerCollection);

        // configure the HTTP connection to NOT send the server version for security reasons
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSendServerVersion(false);
        httpConfiguration.setRequestHeaderSize(requestHeaderSize);

        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration);

        ServerConnector serverConnector = new ServerConnector(server, acceptors, selectors, httpConnectionFactory);
        serverConnector.setPort(port);

        server.addConnector(serverConnector);

        return server;
    }
}

