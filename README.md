# terraform-acm-quick_certificate
## Introduction
This is a terraform module for quickly creating acm certificates. Please note that it does not ensure that the certificate is deleted before returning, which is why it is faster than the current way of deleting certificates with terraform.

This module depends on a provider that I developed for creating custom lambda backed resources. The reason for this is that the pull request to the terraform aws provider has yet to be approved cause fuck those guys are lazy.

## Prerequisites
You will need to following terraform plugins installed to use this module. Simply copy the provider into your ~/.terraform.d/plugins directory and run "terraform init"

* terraform-provider-customresource

## Use
Refer to the example in the test folder for how to use this terraform module. 

**Inputs**

domain_name - (Required) The domain name of the certificate to create
project_name - (Optional) Your unique project name
region - (Optional) the aws region to deploy the certificate to. Defaults to current region
subject_alternative_names - (Optional) a list of alternative names to create

**Outputs**

arn - The arn of the certificate that was created

```
module "certificate" {
    source = "git::http://githubdev.dco.elmae/CloudPlatform/aws-quick-certificate.git"
    domain_name = "${var.domain_name}"
    project_name = "${var.project_name}"
    region = "${var.region}"
    subject_alternative_names = "${var.subject_alternative_names}
}
```

project_name is optional, but I recommend setting them for your project to prevent people from accidently deleting your resources through the console. You can access the arn of the certificate with the terraform output variable: `${module.certificate.arn}`
