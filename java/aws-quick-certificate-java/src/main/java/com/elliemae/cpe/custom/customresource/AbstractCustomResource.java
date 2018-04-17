package com.elliemae.cpe.custom.customresource;


import com.elliemae.cpe.custom.model.CustomResourceRequest;
import com.elliemae.cpe.custom.model.CustomResourceResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Abstract class implementing the CustomResource interface. This class is used to declare utility method
 * shared with all CustomResource implementations
 */
public abstract class AbstractCustomResource implements CustomResource {
    /**
     * Returns an initialized Gson object with the default configuration
     *
     * @return An initialized Gson object
     */
    protected Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }
}
