package com.elliemae.cpe.custom.customresource;

import com.amazonaws.services.certificatemanager.AWSCertificateManager;
import com.amazonaws.services.certificatemanager.AWSCertificateManagerClientBuilder;
import com.amazonaws.services.certificatemanager.model.*;
import com.amazonaws.services.lambda.model.ResourceNotFoundException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.elliemae.cpe.custom.exception.BadRequestException;
import com.elliemae.cpe.custom.exception.InternalErrorException;
import com.elliemae.cpe.custom.model.CustomResourceRequest;
import com.elliemae.cpe.custom.model.CustomResourceResponse;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Custom Resource that creates/updates/deletes for an Example
 */
public class QuickCertificateCustomResource extends AbstractCustomResource {
    private LambdaLogger logger;

    public CustomResourceResponse create(CustomResourceRequest request, Context lambdaContext) throws BadRequestException, InternalErrorException {
        logger = lambdaContext.getLogger();

        AWSCertificateManager acm ;

        CustomResourceResponse response = new CustomResourceResponse(request);
        System.out.println("Creating...");
        try {
            //cleanup old certs
            String domainName =request.getResourceProperties().get("DomainName");
            String region = request.getResourceProperties().get("Region");
            acm = getAWSCertificateManager(region);
            deleteCertificates(domainName,region);

            //construct new cert with parameters
            Map<String,String> properties = request.getResourceProperties();
            ArrayList<String> subjectAlternativeNames = new ArrayList<String>();
            if(properties.containsKey("SubjectAlternativeNames")&& properties.get("SubjectAlternativeNames").trim().length()>0) {
                subjectAlternativeNames = new ArrayList<String> (Arrays.asList(properties.get("SubjectAlternativeNames").split(",")));
            }

            String validationDomain = "elliemae.io";
            DomainValidationOption domainValidationOption = new DomainValidationOption()
                    .withValidationDomain(validationDomain)
                    .withDomainName(domainName);


            RequestCertificateRequest requestCertificateRequest = new RequestCertificateRequest()
                    .withDomainName(domainName)
                    .withDomainValidationOptions(domainValidationOption);


            if ( subjectAlternativeNames.size() >0 ){
                System.out.println("adding subject alt names: ");
                for(String subjectAlternativeName : subjectAlternativeNames){
                    System.out.println("name: |"+ subjectAlternativeName +"|");
                }
                subjectAlternativeNames.forEach(System.out::println);
                requestCertificateRequest = requestCertificateRequest.withSubjectAlternativeNames(subjectAlternativeNames);
            }

            String certificateArn = acm.requestCertificate(requestCertificateRequest).getCertificateArn();

            String certificateStatus = null;
            do {
                //wait for certificate to be issued to prevent subtle bugs
                DescribeCertificateRequest describeCertificateRequest = new DescribeCertificateRequest()
                        .withCertificateArn(certificateArn);
                DescribeCertificateResult describeCertificateResult = acm.describeCertificate(describeCertificateRequest);
                certificateStatus = describeCertificateResult.getCertificate().getStatus();
                //wait to prevent throttling
                TimeUnit.SECONDS.sleep(1);
            } while (!certificateStatus.equals("ISSUED"));

            Map<String,String> data = new HashMap<>();
            data.put("certificateArn",certificateArn);
            response.setData(data);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus("FAILED");
            response.setReason(e.getMessage());
        }
        return response;
    }

    public CustomResourceResponse update(CustomResourceRequest request, Context lambdaContext) throws BadRequestException, InternalErrorException {
        logger = lambdaContext.getLogger();

        System.out.println("Updating...");
        //delete old cert if able
        String oldDomainName =request.getOldResourceProperties().get("DomainName");
        String oldRegion = request.getOldResourceProperties().get("Region");
        deleteCertificates(oldDomainName,oldRegion);

        CustomResourceResponse response = new CustomResourceResponse(request);
        try {
            response = delete(request, lambdaContext);
            if(!response.getStatus().equals("FAILED")){
                response = create(request, lambdaContext);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus("FAILED");
            response.setReason(e.getMessage());
        }
        return response;
    }

    public CustomResourceResponse delete(CustomResourceRequest request, Context lambdaContext) throws BadRequestException, InternalErrorException {
        logger = lambdaContext.getLogger();


        String domainName =request.getResourceProperties().get("DomainName");
        String region = request.getResourceProperties().get("Region");
        AWSCertificateManager acm = getAWSCertificateManager(region);

        System.out.println("Deleting...");
        CustomResourceResponse response = new CustomResourceResponse(request);
        deleteCertificates(domainName,region);

        return response;
    }

    private AWSCertificateManager getAWSCertificateManager(String region){
        if (region == null || region.trim().equals("")) {
           return AWSCertificateManagerClientBuilder.defaultClient();
        }
        return AWSCertificateManagerClientBuilder.standard().withRegion(region).build();
    }

    private void deleteCertificates(String domainName,String region){
        AWSCertificateManager acm = getAWSCertificateManager(region);
        ListCertificatesRequest listCertificatesRequest = new ListCertificatesRequest();
        ListCertificatesResult listCertificatesResult = acm.listCertificates(listCertificatesRequest);
        List<CertificateSummary> certificateSummaries = listCertificatesResult.getCertificateSummaryList()
                .stream()
                .filter((item) -> item.getDomainName().equals(domainName))
                .collect(Collectors.toList());

        for (CertificateSummary certificateSummary : certificateSummaries) {
            //delete all certificate summaries sharing the domain name, not ideal but good enough. Avoids having to store certificate arn somewhere
            String certificateArn = certificateSummary.getCertificateArn();
            System.out.println("certificate arn: " + certificateArn);

            DeleteCertificateRequest deleteCertificateRequest = new DeleteCertificateRequest()
                    .withCertificateArn(certificateArn);

            //try to delete
            try {
                System.out.println("attempting to delete certificate");
                acm.deleteCertificate(deleteCertificateRequest);
                System.out.println("deleted succesfully!");

            } catch (ResourceInUseException e) {
                System.out.println("certificate in use, skipping");

            } catch (ResourceNotFoundException e) {
                //log error, then rethrow. Should not have this unless someone deleted the certificate through some other means
                e.printStackTrace();
            }
        }
}
}
