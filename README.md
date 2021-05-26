# Google Coud Functions comparison

## How to run

### Authenticate with GCP
```
gcloud auth application-default login
```

### Set default project
```
gcloud config set project PROJECT_ID
```

### Create variables file
```
echo "gcp-project = \""$(gcloud config get-value project)"\"" >> terraform/terraform.tfvars
```

### Go to terraform/
```
cd terraform
```

### Initialize Terraform and review resources plan
```
terraform init && terraform plan
```

### Create resources
```
terraform apply -auto-approve
```

### Test function
`europe-west1` is a region from variable `gcp-region`
```
curl "https://europe-west1-$(gcloud config get-value project).cloudfunctions.net/nodejs14" -H "Authorization: bearer $(gcloud auth print-identity-token)"
```

### Delete resources
```
terraform destroy -auto-approve
```

## How to add a new function
1. The source code for the function is in the directory `functions/`.
2. Each function has a directory with a name that is the name of the function and the runtime environment. Example: `/functions/nodejs14` directory contains function codes named `nodejs14` and in the same runtime.
4. Add function name/runtime to `variables.tf`. The `function_runtimes` variable contains a set of functions names/runtimes in the default section. You can overwrite the variable in the `terraform.tfvars` file.
