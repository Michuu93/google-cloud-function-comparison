locals {
  timestamp = formatdate("YYMMDDhhmmss", timestamp())
}

data "archive_file" "source" {
  type        = "zip"
  source_dir  = "${var.source_root_dir}/${var.runtime}"
  output_path = "/tmp/${var.function_name}-${local.timestamp}.zip"
}

resource "google_storage_bucket_object" "zip" {
  name   = "${var.function_name}-${local.timestamp}.zip#${data.archive_file.source.output_md5}"
  bucket = var.bucket_name
  source = data.archive_file.source.output_path
}

resource "google_cloudfunctions_function" "function" {
  project               = var.gcp_project
  name                  = var.function_name
  runtime               = var.runtime
  available_memory_mb   = var.function_memory
  region                = var.function_region
  source_archive_bucket = var.bucket_name
  source_archive_object = google_storage_bucket_object.zip.name
  max_instances         = var.function_max_instances
  entry_point           = var.entry_point
  trigger_http          = true
}
