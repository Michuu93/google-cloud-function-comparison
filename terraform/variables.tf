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
  default = ["europe-west3", "europe-central2", "us-central1"]
}
variable "functions" {
  default = [
    {
      folder     = "nodejs14"
      runtime     = "nodejs14"
      entry_point = "helloWorld"
    },
    {
      folder     = "go113"
      runtime     = "go113"
      entry_point = "HelloWorld"
    },
    {
      folder     = "java11"
      runtime     = "java11"
      entry_point = "com.example.Example"
    },
    {
      folder     = "python39"
      runtime     = "python39"
      entry_point = "hello_world"
    },
    {
      folder     = "ruby27"
      runtime     = "ruby27"
      entry_point = "hello_world"
    },

    {
      folder     = "java11_heavy"
      runtime     = "java11"
      entry_point = "com.example.Heavy"
    },
    {
      folder     = "nodejs14_heavy"
      runtime     = "nodejs14"
      entry_point = "heavy"
    },
    {
      folder     = "python39_heavy"
      runtime     = "python39"
      entry_point = "heavy"
    }
  ]
}
