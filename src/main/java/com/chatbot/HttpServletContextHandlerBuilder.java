package com.chatbot;

import com.chatbot.utils.JsonUtils;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.*;
import java.util.logging.Logger;

/**
 * Builder for configuring a ServletContextHandler.
 */
public class HttpServletContextHandlerBuilder {
    private static final Logger LOGGER = CommonLogger.getLogger();

    private ResourceConfig resourceConfig = new ResourceConfig();

    private String title;
    private String version = "1.0";
    private Set<String> schemes = new HashSet<>(Arrays.asList("https"));

    // default context path is root context
    private String contextPath = "/";

    // default is session-less
    private int servletOptions = ServletContextHandler.NO_SESSIONS;

    private List<ServletFilterConfig> filterConfigs = new ArrayList<ServletFilterConfig>();

    private boolean logEntity = true;

    private boolean healthCheck = false;

    public HttpServletContextHandlerBuilder setTitle(String title) {
        assert title != null;

        this.title = title;
        return this;
    }

    public HttpServletContextHandlerBuilder setVersion(String version) {
        assert version != null;

        this.version = version;
        return this;
    }

    public HttpServletContextHandlerBuilder addScheme(String scheme) {
        assert scheme != null;

        this.schemes.add(scheme);
        return this;
    }

    public HttpServletContextHandlerBuilder setSchemes(String... schemes) {
        this.schemes = new HashSet<>(Arrays.asList(schemes));
        return this;
    }

    public HttpServletContextHandlerBuilder setServletOptions(int servletOptions) {
        this.servletOptions = servletOptions;
        return this;
    }

    public HttpServletContextHandlerBuilder register(Object component) {
        assert component != null;

        resourceConfig.register(component);
        return this;
    }

    public HttpServletContextHandlerBuilder register(List<Object> components) {
        assert components != null;

        for (Object component : components)
            register(component);

        return this;
    }

    public HttpServletContextHandlerBuilder register(Class componentClass) {
        assert componentClass != null;

        resourceConfig.register(componentClass);

        return this;
    }

    public HttpServletContextHandlerBuilder setPackages(String... packageNames) {
        resourceConfig.packages(packageNames);

        return this;
    }

    public HttpServletContextHandlerBuilder setPackages(boolean recursive, String... packageNames) {
        resourceConfig.packages(recursive, packageNames);

        return this;
    }

    public HttpServletContextHandlerBuilder setContextPath(String contextPath) {
        assert contextPath != null;

        this.contextPath = contextPath;
        return this;
    }

    public HttpServletContextHandlerBuilder applyServletFilter(ServletFilterConfig config){
        assert config != null;

        filterConfigs.add(config);
        return this;
    }

    public HttpServletContextHandlerBuilder setLogEntity(boolean logEntity) {
        this.logEntity = logEntity;
        return this;
    }

    public HttpServletContextHandlerBuilder setHealthCheck(boolean healthCheck) {
        this.healthCheck = healthCheck;
        return this;
    }

    public ServletContextHandler build() {
        JacksonJaxbJsonProvider jacksonProvider = new JacksonJaxbJsonProvider();
        jacksonProvider.setMapper(JsonUtils.getMapper());

        // we always add these filters to setup the diagnostic context
        // Using MAX_VALUE for LoggerFilter priority to ensure it is logged last, after processing headers, etc
        resourceConfig = resourceConfig
                .register(jacksonProvider)
                .register(JacksonFeature.class)
                .property(ServerProperties.WADL_FEATURE_DISABLE, Boolean.TRUE.toString());

        ServletContainer servletContainer = new ServletContainer(resourceConfig);

        ServletContextHandler context = new ServletContextHandler(servletOptions);
        context.setContextPath(contextPath);

        ServletHolder holder = new ServletHolder(servletContainer);
        context.addServlet(holder, "/*");

        if(filterConfigs != null && !filterConfigs.isEmpty()) {
            for(ServletFilterConfig filterConfig : filterConfigs)
                context.getServletHandler().addFilter(filterConfig.getFilterHolder(), filterConfig.getFilterMapping());
        }

        return context;
    }
}
