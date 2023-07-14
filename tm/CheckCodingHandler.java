import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;

// Student Name,    Student Number
// Xiaojun Huang,   23011392
// Lingwan Peng,    23478401
// Renyin Zhang,    23719462
// Maxcin Lim,      23146164

/*
 * Runs user's coding answer and returns output.
 * Checks output against expected output.
 * Subsequent page ends with the total score;
 * and the coding question is thus the final page before user submits test.
 */
public class CheckCodingHandler implements HttpHandler {
    private HashMap<String, UserInfo> userMap;

    public CheckCodingHandler(HashMap<String, UserInfo> userMap) {
        this.userMap = userMap;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("POST")) {
            String buttonClicked = null;
            String codingAnswer = null;

            // Reads user's code from textArea
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String body = br.readLine();
            while (body != null && !body.isEmpty()) {
                String[] keyValue = body.split("=");
                if (keyValue.length == 2) {
                    String paramName = keyValue[0];
                    String paramValue = keyValue[1];
                    if (paramName.equals("buttonClicked")) {
                        buttonClicked = paramValue;
                    } else if (paramName.equals("CodingAnswer")) {
                        codingAnswer = paramValue;
                    }
                }
                body = br.readLine();
            }

            if ("check".equals(buttonClicked)) {
                // Process the coding answer
                // Return the "received" message or perform other operations

                String response = "received";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(response.getBytes());
                outputStream.close();
            } else if ("prev".equals(buttonClicked)) {
                // Handles previous button click
            } else {
                // Handles other button clicks (e.g., Submit Test)
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}
