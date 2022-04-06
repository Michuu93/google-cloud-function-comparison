require "functions_framework"

FunctionsFramework.http "ruby_heavy" do |request|
  input = request.body.read rescue {}
  input.chars.sort.join
end
