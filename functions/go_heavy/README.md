# Local test

## Install Dotnet3.1
```bash
sudo tar -C /usr/local -zxf go1.18.linux-amd64.tar.gz
export PATH=$PATH:/usr/local/go/bin
```

## Download dependencies
```bash
go mod tidy
```

## Run and test function
```
export FUNCTION_TARGET=HelloWorld
go run cmd/main.go
curl localhost:8080 -d "Hello world" -H "content-type:text/plain"
```
