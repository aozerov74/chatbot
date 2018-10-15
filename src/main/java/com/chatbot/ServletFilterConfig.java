package com.chatbot;


import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;

/**
 * Created by jchakrav on 2/9/17.
 */
public class ServletFilterConfig {

    protected FilterMapping filterMapping = null;
    protected FilterHolder filterHolder = null;

    public ServletFilterConfig(FilterMapping filterMapping, FilterHolder filterHolder) {
        this.filterMapping = filterMapping;
        this.filterHolder = filterHolder;
    }

    public FilterMapping getFilterMapping() {
        return filterMapping;
    }

    public void setFilterMapping(FilterMapping filterMapping) {
        this.filterMapping = filterMapping;
    }

    public FilterHolder getFilterHolder() {
        return filterHolder;
    }

    public void setFilterHolder(FilterHolder filterHolder) {
        this.filterHolder = filterHolder;
    }
}


