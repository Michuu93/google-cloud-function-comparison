provider "google-beta" {
  project = var.gcp-project
  region  = var.gcp-region
  zone    = var.gcp-zone
}

# Enable Cloud Functions API
resource "google_project_service" "cf" {
  project = var.gcp-project
  service = "cloudfunctions.googleapis.com"
}

# Enable Cloud Build API
resource "google_project_service" "cb" {
  project = var.gcp-project
  service = "cloudbuild.googleapis.com"
}

resource "google_storage_bucket" "bucket" {
  project       = var.gcp-project
  name          = "${var.gcp-project}-functions"
  location      = var.gcp-region
  force_destroy = true
}

module "function" {
  for_each        = { for function in var.functions : function.runtime => function }
  source          = "./modules/function"
  gcp-project     = var.gcp-project
  gcp-region      = var.gcp-region
  function_name   = each.value.runtime
  runtime         = each.value.runtime
  entry_point     = each.value.entry_point
  source_root_dir = abspath("../functions")
  bucket_name     = google_storage_bucket.bucket.name
  depends_on      = [google_project_service.cf, google_project_service.cb, google_storage_bucket.bucket]
}
