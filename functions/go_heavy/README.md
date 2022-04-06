# Local test
```
go mod tidy
export FUNCTION_TARGET=HelloWorld
go run cmd/main.go
curl localhost:8080 -d "Hello world" -H "content-type:text/plain"
```
