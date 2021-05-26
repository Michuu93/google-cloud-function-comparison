variable "gcp-project" {}
variable "gcp-region" {}
variable "function_name" {}
variable "runtime" {}
variable "source_dir" {}
variable "memory" {
  default = 128
}
variable "max-instances" {
  default = 1
}
variable "entry_point" {
  default = "helloWorld"
}
