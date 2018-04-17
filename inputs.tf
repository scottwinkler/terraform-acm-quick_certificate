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

variable subject_alternative_names {
  type = "list"
  description = "a list of alternative names to create"
  default = []
}