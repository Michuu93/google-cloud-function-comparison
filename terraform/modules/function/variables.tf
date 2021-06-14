variable "gcp-project" {}
variable "gcp-region" {}
variable "function_name" {}
variable "runtime" {}
variable "entry_point" {}
variable "source_root_dir" {}
variable "bucket_name" {}
variable "memory" {
  default = 128
}
variable "max-instances" {
  default = 1
}
