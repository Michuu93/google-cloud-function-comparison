variable "gcp_project" {}
variable "gcp_region" {
  //  Internal error (HTTP 500): Failed to initialize region (action ID: 7d75de3170745c65)
  //  default = "europe-central2"
  default = "europe-west1"
}
variable "function_memory" {
  default = 128
}
variable "function_max_instances" {
  default = 1
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
