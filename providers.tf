provider "customresource" {
  region = "${data.aws_region.current.name}"
}
