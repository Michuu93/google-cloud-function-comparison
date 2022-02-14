# Google Coud Functions Comparison

## Requirements
- Terraform
- Scala 2.13.3
- Java 11
- Maven

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

### Before running the tests, check the availability of the functions
In `/tests` folder run
```
mvn scala:run -DaddArgs="$(gcloud config get-value project)" -Dlauncher=availability
```

### Run load tests
In `/tests` folder run
```
mvn gatling:test -Dproject=$(gcloud config get-value project)
```
or
```
mvn gatling:test -Dproject=$(gcloud config get-value project) -Dusers=1 -Dduration=60
```
where `users` is number of concurrent users and `duration` is test duration in seconds (default 20 users and 120 seconds).

### Run cold start tests
Make sure that no active instance of any function exists before running the cold starts test.  
In `/tests` folder run
```
mvn scala:run -DaddArgs="$(gcloud config get-value project)" -Dlauncher=coldstarts
```
or
```
mvn scala:run -DaddArgs="$(gcloud config get-value project)|20" -Dlauncher=coldstarts
```
where `20` is number of requests per function (default 10).
The first response time is compared to the mean of the remaining response times (for 10 requests, the average is taken from 10-1=9 requests).

### Delete resources
```
terraform destroy -auto-approve
```

## How to add a new function
1. The source code for the function is in the directory `functions/`.
2. For each region in variable `function_regions`, a function for each runtime environment will be created. The name of the function will be `RUNTIME_REGION_FOLDER`, e.g. `nodejs14_europe-west1_nodejs`.
3. Add function folder, runtime and entry point to `variables.tf`. The `functions` variable contains a list of objects defining function location. You can overwrite the variable in the `terraform.tfvars` file.
4. If you want to change the function regions, add them in the `function_regions` list variable.
