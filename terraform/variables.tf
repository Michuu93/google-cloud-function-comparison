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
  default = ["europe-west3", "us-central1", "asia-east2"]
}
variable "functions" {
  default = [
    {
      folder      = "nodejs"
      runtime     = "nodejs14"
      entry_point = "helloWorld"
    },
    {
      folder      = "go"
      runtime     = "go113"
      entry_point = "HelloWorld"
    },
    {
      folder      = "java11"
      runtime     = "java11"
      entry_point = "com.example.Example"
    },
    {
      folder      = "python"
      runtime     = "python39"
      entry_point = "hello_world"
    },
    {
      folder      = "ruby"
      runtime     = "ruby27"
      entry_point = "hello_world"
    },
    {
      folder      = "dotnet3"
      runtime     = "dotnet3"
      entry_point = "SimpleHttpFunction.Function"
    },
    {
      folder      = "php74"
      runtime     = "php74"
      entry_point = "hello_world"
    },
    {
      folder     = "nodejs_heavy"
      runtime     = "nodejs14"
      entry_point = "heavy"
    },
    {
      folder      = "go_heavy"
      runtime     = "go116"
      entry_point = "goHeavy"
    },
    {
      folder     = "java11_heavy"
      runtime     = "java11"
      entry_point = "com.example.Heavy"
    },
    {
      folder     = "python_heavy"
      runtime     = "python39"
      entry_point = "heavy"
    },
    {
      folder      = "ruby_heavy"
      runtime     = "ruby27"
      entry_point = "ruby_heavy"
    },
    {
      folder      = "dotnet3_heavy"
      runtime     = "dotnet3"
      entry_point = "dotnet3_heavy.Function"
    },
    {
      folder      = "php74_heavy"
      runtime     = "php74"
      entry_point = "php74_heavy"
    }
  ]
}
