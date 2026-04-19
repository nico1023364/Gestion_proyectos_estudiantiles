package com.compensar.gestion.config;

import org.glassfish.jersey.server.ResourceConfig;

public class RestApplication extends ResourceConfig {

    public RestApplication() {
        packages("com.compensar.gestion.resource");
        register(org.glassfish.jersey.jsonb.JsonBindingFeature.class);
    }
}
