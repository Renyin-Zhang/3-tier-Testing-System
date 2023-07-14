import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// Student Name,    Student Number
// Xiaojun Huang,   23011392
// Lingwan Peng,    23478401
// Renyin Zhang,    23719462
// Maxcin Lim,      23146164

/*
 * The final page of the test.
 * Ends all current user's activities by displaying accumulated score.
 */
public class LastPageHandler implements HttpHandler {
    //private int score = 0;
    private HashMap<String, UserInfo> userMap;

    public LastPageHandler(HashMap<String, UserInfo> userMap) {
        this.userMap = userMap;
    }
    public String lastPage(String username) {

        String empty = "";
        UserInfo user = userMap.get(username);

        // Once user attempts all 5 questions, generates score page 
        if (user.getNumOfQuestion() == 5) {
            // Get current user's total score 
            int score = user.scoreCaluclator();

            // Show final total score; end of test

            // Use StringBuilder to generate HTML
            StringBuilder score_page = new StringBuilder();
            score_page.append("<!DOCTYPE html>\n")
                .append("<html>\n")
                .append("    <head>\n")
                .append("        <title> Results </title>\n")
                .append("    </head>\n")
                .append("   <body>\n")
                .append("        <h4> Your Score:" + score +"</h4>\n")
                .append("        <h5> End of Test </h5>\n")
                .append("   </body>\n")
                .append("</html>\n");
            return score_page.toString();
        }
        else {
            return empty;
        }
    }

    public static Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
        Map<String, String> formData = new HashMap<>();
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder requestBodyBuilder = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            requestBodyBuilder.append(line);
        }

        String requestBody = requestBodyBuilder.toString();
        String[] pairs = requestBody.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                formData.put(key, value);
            }
        }

        return formData;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String username = QuestionHandler.extractUsernameFromPath(exchange.getRequestURI().getPath());
        Map<String, String> formData = parseFormData(exchange);
        String textareaContent = formData.get("textareaContent");//Get the content of the last question(coding question)
        //TODO: mark coding question
        System.out.println(textareaContent);
//        if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            UserInfo userInfo = userMap.get(username);
            userInfo.submitted = true;
            userMap.put(username,userInfo);
            String lastPageResponse = lastPage(username);

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, lastPageResponse.getBytes(StandardCharsets.UTF_8).length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(lastPageResponse.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();
//        }
    }
}