variable "gcp-project" {}
variable "gcp-region" {
  //  Internal error (HTTP 500): Failed to initialize region (action ID: 7d75de3170745c65)
  //  default = "europe-central2"
  default = "europe-west1"
}
variable "gcp-zone" {
  //  default = "europe-central2-a"
  default = "europe-west1-c"
}
variable "function_runtimes" {
  type    = set(string)
  default = ["nodejs14"]
}
