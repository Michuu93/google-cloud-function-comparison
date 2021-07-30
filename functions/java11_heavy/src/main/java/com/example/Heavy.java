package com.example;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import java.io.BufferedWriter;
import java.util.Arrays;

public class Heavy implements HttpFunction {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String message = request.getReader().readLine();
        char[] sortedMessage = message.toCharArray();
        Arrays.sort(sortedMessage);
        BufferedWriter writer = response.getWriter();
        writer.write(new String(sortedMessage));
    }
}
