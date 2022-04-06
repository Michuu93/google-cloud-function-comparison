# Local test

## Install Ruby
```bash
sudo apt install ruby-full
```

## Download dependencies
```bash
bundle add functions_framework
bundle install
```

## Run and test function
```bash
bundle exec functions-framework-ruby --target ruby_heavy
curl localhost:8080 -d "Hello world" -H "content-type:text/plain"
```
