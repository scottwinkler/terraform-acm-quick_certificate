
module "lambda_role" {
    source = "github.com/scottwinkler/terraform-iam-lambda_role"
    aws_permissions = ["acm:DeleteCertificate","acm:DescribeCertificate","acm:ListCertificates","acm:RequestCertificate"]
    project_name = "${var.project_name}"
}

resource "random_pet" "random" {}

resource "aws_lambda_function" "custom_resources_lambda" {
  filename = "${path.cwd}/../java/target/aws-quick-certificate-java-1.0-SNAPSHOT.jar"
  description = "Quick Certificate. Creates and deletes acm certificates for: ${var.project_name}"
  function_name = "aws-quick-certificate-${var.project_name}-${random_pet.random.id}"
  handler = "com.elliemae.cpe.custom.RequestRouter::lambdaHandler"
  role = "${module.lambda_role.arn}"
  memory_size = 256
  runtime = "java8" 
  timeout = 240
}

resource "customresource_lambda_invoke" "quick_certificate" {
  depends_on = ["module.lambda_role"]
  service_token = "${aws_lambda_function.custom_resources_lambda.arn}"
  resource_type = "QuickCertificateCustomResource"
  resource_properties = {
    DomainName = "${var.domain_name}"
    SubjectAlternativeNames = "${join(",", var.subject_alternative_names)}"
    Region = "${var.region}"
  }
}