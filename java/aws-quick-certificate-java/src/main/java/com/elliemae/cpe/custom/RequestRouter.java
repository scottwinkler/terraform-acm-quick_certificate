package com.elliemae.cpe.custom;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.elliemae.cpe.custom.customresource.CustomResource;
import com.elliemae.cpe.custom.exception.BadRequestException;
import com.elliemae.cpe.custom.exception.InternalErrorException;
import com.elliemae.cpe.custom.model.CustomResourceRequest;
import com.elliemae.cpe.custom.model.CustomResourceResponse;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class contains the main event handler for the Lambda function.
 */
public class RequestRouter {
    /**
     * The main Lambda function handler. Receives the request as an input stream, parses the json and looks for the
     * "action" property to decide where to route the request. The "body" property of the incoming request is passed
     * to the Action implementation as a request body.
     *
     * @param request  The InputStream for the incoming event. This should contain an "action" and "body" properties. The
     *                 action property should contain the namespaced name of the class that should handle the invocation.
     *                 The class should implement the Action interface. The body property should contain the full
     *                 request body for the action class.
     * @param response An OutputStream where the response returned by the action class is written
     * @param context  The Lambda Context object
     * @throws BadRequestException    This Exception is thrown whenever parameters are missing from the request or the action
     *                                class can't be found
     * @throws InternalErrorException This Exception is thrown when an internal error occurs, for example when the database
     *                                is not accessible
     */
    public static void lambdaHandler(InputStream request, OutputStream response, Context context) throws BadRequestException, InternalErrorException {
        LambdaLogger logger = context.getLogger();

        JsonParser parser = new JsonParser();
        JsonObject inputObj;
        try {
            inputObj = parser.parse(IOUtils.toString(request)).getAsJsonObject();
        } catch (IOException e) {
            logger.log("Error while reading request\n" + e.getMessage());
            throw new InternalErrorException(e.getMessage());
        }

        if (inputObj == null || inputObj.get("ResourceType") == null || inputObj.get("ResourceType").getAsString().trim().equals("")) {
            logger.log("Invalid inputObj, could not find resource type parameter");
            throw new BadRequestException("Could not find resource type value in request");
        }
        String packageName = CustomResource.class.getPackage().getName();
        String customResourceClass = packageName+"."+inputObj.get("ResourceType").getAsString();
        CustomResource customResource;

        try {
            customResource = CustomResource.class.cast(Class.forName(customResourceClass).newInstance());
        } catch (final InstantiationException e) {
            logger.log("Error while instantiating custom resource class\n" + e.getMessage());
            throw new InternalErrorException(e.getMessage());
        } catch (final IllegalAccessException e) {
            logger.log("Illegal access while instantiating custom resource class\n" + e.getMessage());
            throw new InternalErrorException(e.getMessage());
        } catch (final ClassNotFoundException e) {
            logger.log("Custom resource class could not be found\n" + e.getMessage());
            throw new InternalErrorException(e.getMessage());
        }

        if (customResource == null) {
            logger.log("Custom resource class is null");
            throw new BadRequestException("Invalid custom resource class");
        }

        String requestType = inputObj.get("RequestType").toString();
        if(requestType == null || requestType.trim().equals("")) {
            logger.log("Request type cannot be null or empty");
            throw new BadRequestException("Null or empty Request Type");
        }

        Gson gson = new Gson();
        CustomResourceRequest customResourceRequest = gson.fromJson(inputObj, CustomResourceRequest.class);
        System.out.println(gson.toJson(customResourceRequest));
        CustomResourceResponse customResourceResponse;
        switch(requestType){
            case "\"Create\"" :
                customResourceResponse = customResource.create(customResourceRequest,context);
                break;
            case "\"Update\"" :
                customResourceResponse = customResource.update(customResourceRequest,context);
                break;
            case "\"Delete\"" :
                customResourceResponse = customResource.delete(customResourceRequest,context);
                break;
            default:
                logger.log("Request Type is not one of: Create|Update|Delete");
                throw new BadRequestException("Invalid Request Type");
        }

        String output = gson.toJson(customResourceResponse);
        System.out.println(output);
        try {
            IOUtils.write(output, response);
        } catch (final IOException e) {
            logger.log("Error while writing response\n" + e.getMessage());
            throw new InternalErrorException(e.getMessage());
        }

    }
}

