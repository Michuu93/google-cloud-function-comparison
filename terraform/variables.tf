variable "gcp_project" {}
variable "gcp_region" {
  default = "europe-west1"
}
variable "function_memory" {
  default = 128
}
variable "function_max_instances" {
  default = 1
}
variable "function_regions" {
  default = ["europe-west1", "europe-central2"]
}
variable "functions" {
  default = [
    {
      runtime     = "nodejs14"
      entry_point = "helloWorld"
    },
    {
      runtime     = "go113"
      entry_point = "HelloWorld"
    },
    {
      runtime     = "java11"
      entry_point = "com.example.Example"
    },
    {
      runtime     = "python39"
      entry_point = "hello_world"
    },
    {
      runtime     = "ruby27"
      entry_point = "hello_world"
    }
  ]
}
