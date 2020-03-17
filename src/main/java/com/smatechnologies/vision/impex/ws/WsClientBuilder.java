package com.smatechnologies.vision.impex.ws;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;

import javax.ws.rs.client.Client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.smatechnologies.opcon.restapiclient.DefaultClientBuilder;
import com.smatechnologies.opcon.restapiclient.jackson.DefaultObjectMapperProvider;

public class WsClientBuilder {
	
	   private Consumer<DefaultClientBuilder> defaultClientBuilderConsumer;
	    private boolean debug;
	    private Boolean failOnUnknownProperties;

	    private WsClientBuilder() {
	    }

	    public static WsClientBuilder get() {
	        return new WsClientBuilder();
	    }

	    public WsClientBuilder configureDefaultClientBuilder(Consumer<DefaultClientBuilder> defaultClientBuilderConsumer) {
	        this.defaultClientBuilderConsumer = defaultClientBuilderConsumer;

	        return this;
	    }

	    public WsClientBuilder setDebug(boolean debug) {
	        this.debug = debug;

	        return this;
	    }

	    public WsClientBuilder setFailOnUnknownProperties(boolean failOnUnknownProperties) {
	        this.failOnUnknownProperties = failOnUnknownProperties;

	        return this;
	    }

	    public Client build() throws KeyManagementException, NoSuchAlgorithmException {
	        DefaultClientBuilder defaultClientBuilder = DefaultClientBuilder.get();

	        if (defaultClientBuilderConsumer != null) {
	            defaultClientBuilderConsumer.accept(defaultClientBuilder);
	        }

	        DefaultObjectMapperProvider defaultObjectMapperProvider = new DefaultObjectMapperProvider();
	        defaultClientBuilder.setObjectMapperProvider(defaultObjectMapperProvider);

	        if (failOnUnknownProperties != null) {
	            defaultObjectMapperProvider.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties);
	        }

	        Client client = defaultClientBuilder.build();

	        if (debug) {
	            client.register(new WsLogger(defaultObjectMapperProvider));
	        }

	        return client;
	    }


}
