# Local test

## Install Dotnet3.1
```bash
wget https://packages.microsoft.com/config/ubuntu/20.04/packages-microsoft-prod.deb -O packages-microsoft-prod.deb\
  sudo dpkg -i packages-microsoft-prod.deb\
  rm packages-microsoft-prod.deb
  
sudo apt-get update; \
  sudo apt-get install -y apt-transport-https && \
  sudo apt-get update && \
  sudo apt-get install -y dotnet-sdk-3.1
  
```

## Run and test function
```bash
FUNCTION_TARGET=''
dotnet run
curl localhost:8080 -d "Hello world" -H "content-type:text/plain"
```
