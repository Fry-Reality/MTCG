package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String path;
    private String method;
    private Map<String, String> headers = new HashMap<>();
    private StringBuilder body = new StringBuilder();

    public HttpRequest(BufferedReader reader) throws IOException {
        String line;
        boolean isFirstLine = true;

        while (!(line = reader.readLine()).isEmpty()) {
            if (isFirstLine) {
                String[] requestLine = line.split(" ");
                method = requestLine[0];
                path = requestLine[1];
                isFirstLine = false;
            } else if (line.contains(": ")) {
                String[] header = line.split(": ");
                headers.put(header[0], header[1]);
            }
        }

        while (reader.ready()) {
            body.append((char) reader.read());
        }
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public String getBody() {
        return body.toString();
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }
}



