# your domain name
variable domain_name {
  type = "string"
  description = "The domain name of the certificate to create project_name"
}

variable project_name {
  type = "string"
  description = ""
  default = "aws-quick-certificate"
}

variable environment {
  type = "string"
  description = "The deploy environment (e.g. dev, int, prod, qa, etc.)."
  default = "dev"
}

variable region {
  type = "string"
  description = "the aws region to deploy the certificate to. Defaults to current region"
  default = ""
}

variable subject_alternative_names {
  type = "list"
  description = "a list of alternative names to create"
  default = []
}