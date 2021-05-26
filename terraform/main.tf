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

module "nodejs14" {
  for_each      = var.function_runtimes
  source        = "./modules/function"
  gcp-project   = var.gcp-project
  gcp-region    = var.gcp-region
  function_name = each.key
  runtime       = "nodejs14"
  source_dir    = abspath("../functions/nodejs14")
}
