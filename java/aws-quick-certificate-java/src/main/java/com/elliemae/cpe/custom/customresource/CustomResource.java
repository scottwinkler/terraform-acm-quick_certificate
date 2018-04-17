package com.elliemae.cpe.custom.customresource;

import com.amazonaws.services.lambda.runtime.Context;
import com.elliemae.cpe.custom.exception.BadRequestException;
import com.elliemae.cpe.custom.exception.InternalErrorException;
import com.elliemae.cpe.custom.model.CustomResourceRequest;
import com.elliemae.cpe.custom.model.CustomResourceResponse;

/**
 * CustomResource defines the methods called by the RequestRouter when it is invoked by a Lambda function. Implementing
 * classes should be able to receive a JsonObject containing the cloudformation custom resource request and return a
 * String that contains valid json.
 */
public interface CustomResource {
    /**
     * The create handler method for each requests. This method is called by the RequestRouter when invoked by a Lambda
     * function.
     *
     * @param request       Receives a CustomResourceRequest containing the body content
     * @param lambdaContext The Lambda context passed by the AWS Lambda environment
     * @return A CustomResourceResponse to be returned to the calling client CloudFormation script
     * @throws BadRequestException    This exception should be thrown whenever request parameters are not valid or improperly
     *                                formatted
     * @throws InternalErrorException This exception should be thrown if an error that is independent from user input happens
     */
    CustomResourceResponse create(CustomResourceRequest request, Context lambdaContext) throws BadRequestException, InternalErrorException;

    /**
     * The update handler method for each requests. This method is called by the RequestRouter when invoked by a Lambda
     * function.
     *
     * @param request       Receives a CustomResourceRequest containing the body content
     * @param lambdaContext The Lambda context passed by the AWS Lambda environment
     * @return A CustomResourceResponse to be returned to the calling client CloudFormation script
     * @throws BadRequestException    This exception should be thrown whenever request parameters are not valid or improperly
     *                                formatted
     * @throws InternalErrorException This exception should be thrown if an error that is independent from user input happens
     */
    CustomResourceResponse update(CustomResourceRequest request,  Context lambdaContext) throws BadRequestException, InternalErrorException;


    /**
     * The delete handler method for each requests. This method is called by the RequestRouter when invoked by a Lambda
     * function.
     *
     * @param request       Receives a CustomResourceRequest containing the body content
     * @param lambdaContext The Lambda context passed by the AWS Lambda environment
     * @return A CustomResourceResponse to be returned to the calling client CloudFormation script
     * @throws BadRequestException    This exception should be thrown whenever request parameters are not valid or improperly
     *                                formatted
     * @throws InternalErrorException This exception should be thrown if an error that is independent from user input happens
     */
    CustomResourceResponse delete(CustomResourceRequest request,  Context lambdaContext) throws BadRequestException, InternalErrorException;
}
