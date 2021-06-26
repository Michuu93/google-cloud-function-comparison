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
echo "gcp_project = \""$(gcloud config get-value project)"\"" >> terraform/terraform.tfvars
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
`europe-west1` is a region from variable `gcp-region`, `nodejs14` is function name (runtime)
```
curl "https://europe-west1-$(gcloud config get-value project).cloudfunctions.net/europe-west1_nodejs14" -H "Authorization: bearer $(gcloud auth print-identity-token)"
```

### Run load tests
In gatling folder run
```
mvn gatling:test -Dproject=$(gcloud config get-value project) -Dtoken=$(gcloud auth print-identity-token) -Dregion=europe-west1
```

### Delete resources
```
terraform destroy -auto-approve
```

## How to add a new function
1. The source code for the function is in the directory `functions/`.
2. Each function has a directory with a name that is the runtime environment. Example: `/functions/nodejs14` directory contains function codes for runtime nodejs14.
3. For each region in variable `function_regions`, a function for each runtime environment will be created. The name of the function will be `REGION_RUNTIME_ENVIRONMENT`, e.g. `europe-west1_nodejs14`.
4. Add function name/runtime and entry point to `variables.tf`. The `functions` variable contains a list of objects containing functions names/runtimes and entry points in the default section. You can overwrite the variable in the `terraform.tfvars` file.
5. If you want to change the function regions, add them in the `function_regions` list variable.
