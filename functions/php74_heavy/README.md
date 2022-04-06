# Local test

## Install PHP
```bash
sudo apt install composer
```

## Download dependencies
```bash
composer require google/cloud-functions-framework
```

## Run and test function
```bash
composer start
curl localhost:8080 -d "Hello world" -H "content-type:text/plain"
```
