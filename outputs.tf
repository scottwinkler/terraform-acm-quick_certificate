output "arn" {
    description = "the certificate arn"
    value = "${customresource_lambda_invoke.quick_certificate.data["certificateArn"]}"
}