package com.chatbot;

import java.util.Objects;

/**
 * Represents the configuration for a HTTP server.
 */
public class HttpServerConfig {
    private final int port;
    private final int maxThreads;
    private final int minThreads;

    public HttpServerConfig(int port, int maxThreads, int minThreads) {
        this.port = port;
        this.maxThreads = maxThreads;
        this.minThreads = minThreads;
    }

    public int getPort() {
        return port;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getMinThreads() {
        return minThreads;
    }


    @Override
    public int hashCode() {
        return Objects.hash(port, maxThreads, minThreads);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof HttpServerConfig))
            return false;

        HttpServerConfig that = (HttpServerConfig) obj;
        return Objects.equals(port, that.port) &&
                Objects.equals(maxThreads, that.maxThreads) &&
                Objects.equals(minThreads, that.minThreads);
    }
}
