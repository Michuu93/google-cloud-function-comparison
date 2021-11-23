provider "google-beta" {
  project = var.gcp_project
  region  = var.gcp_region
}

# Enable Cloud Functions API
resource "google_project_service" "cf" {
  project = var.gcp_project
  service = "cloudfunctions.googleapis.com"
}

# Enable Cloud Build API
resource "google_project_service" "cb" {
  project = var.gcp_project
  service = "cloudbuild.googleapis.com"
}

resource "google_storage_bucket" "bucket" {
  project       = var.gcp_project
  name          = "${var.gcp_project}-functions"
  location      = var.gcp_region
  force_destroy = true
}

locals {
  function_list = flatten([
    for function in var.functions : [
      for region in var.function_regions : {
        runtime     = function.runtime
        entry_point = function.entry_point
        folder      = function.folder
        name        = "${function.runtime}_${region}_${function.folder}"
        region      = region
      }
    ]
  ])
}

module "function" {
  for_each = {
    for function in local.function_list : function.name => function
  }
  source                 = "./modules/function"
  gcp_project            = var.gcp_project
  function_region        = each.value.region
  function_name          = each.value.name
  runtime                = each.value.runtime
  entry_point            = each.value.entry_point
  folder                 = each.value.folder
  function_max_instances = var.function_max_instances
  function_memory        = var.function_memory
  source_root_dir        = abspath("../functions")
  bucket_name            = google_storage_bucket.bucket.name
  depends_on             = [google_project_service.cf, google_project_service.cb, google_storage_bucket.bucket]
}
