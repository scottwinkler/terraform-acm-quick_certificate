module "certificate" {
  source                    = "github.com/scottwinkler/terraform-acm-quick_certificate"
  project_name              = "${var.project_name}"
  domain_name               = "${var.domain_name}"
  subject_alternative_names = ["sub1.example.publiccloud.rd.elliemae.io", "sub2.example.publiccloud.rd.elliemae.io"]
}
