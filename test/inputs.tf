
#aws region to deploy to
variable "region" {
  type    = "string"
  default    = "us-west-2"
}

# your domain name
variable domain_name {
  type = "string"
  default = "example.publiccloud.rd.elliemae.io"
  
}

variable project_name {
  type = "string"
  default = "my_project"
}
