import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
// Student Name,    Student Number
// Xiaojun Huang,   23011392
// Lingwan Peng,    23478401
// Renyin Zhang,    23719462
// Maxcin Lim,      23146164
public class LoginHandler implements HttpHandler {
    private HashMap<String, UserInfo> userMap;
    public LoginHandler(HashMap<String, UserInfo> userMap) {
        this.userMap = userMap;
    }
    public void postLogin(HttpExchange exchange) throws IOException {
        // Generate the HTML form
        String htmlResponse = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <title> Login </title>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h2> Login</h2>\n" +
                "        <form method=\"post\" action=\"/\">\n" +
                "            <h5> This is a test which consists of 5 questions for you to answer with an unlimited time-frame. There are 4 multiple-choice questions, and 1 coding exercise which you will undertake in your own time. </h5>\n" +
                "           <h6> Get an answer right, and you will be awarded 3 marks on your first     correct attempt </h6>\n" +
                "           <h6> Every right answer after your first correct attempt will add 2 marks to your current score </h6>\n" +
                "           <h6> No marks will be awarded for any incorrect answer</h6>\n" +
                "            <p> Please enter a username and password to start this test. Use your registered details to continue at a later time. </p>\n" +
                "            <label> Username: </label>\n" +
                "            \n" +
                "            <input type=\"name\" name=\"username\" >\n" +
                "            \n" +
                "            <label> Password: </label>\n" +
                "            \n" +
                "            <input type=\"password\" name=\"password\">\n" +
                "            \n" +
                "            <input type=\"submit\" value=\"Log me in\"></input>\n" +
                "        </form>\n" +
                "    </body>\n" +
                "</html>";
        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, htmlResponse.getBytes(StandardCharsets.UTF_8).length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(htmlResponse.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }
    private void postQuestion(HttpExchange exchange) throws IOException {
        // Read the form data from the request body
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String[] params = requestBody.split("&");
        String username = "Guest";
        String password = "";
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && keyValue[0].equals("username")) {
                username = keyValue[1];
            }
            if (keyValue.length == 2 && keyValue[0].equals("password")){
                password = keyValue[1];
            }
        }
        System.out.println("User: " + username);
        System.out.println("Password: " + password);
        StringBuilder responseBuilder = new StringBuilder();
        // Verifies username and password
        boolean validLogin = false;
        if (userMap.containsKey(username)) {
            UserInfo user = userMap.get(username);
            System.out.println(user.Username);
            if (user.getPassword().equals(password)) {
                validLogin = true;
            } else{
                responseBuilder.append("Invalid password, go back to the previous page and try again.");
            }
        } else {
            responseBuilder.append("Invalid username, go back to the previous page and try again.");
        }
        if (validLogin) { //valid
            String redirectUrl;
            if (userMap.get(username).submitted) { // Check if submitted
                System.out.println(userMap.get(username).submitted);
                redirectUrl = "/result/username=" + username; // Redirect to result page
                exchange.getResponseHeaders().set("Location", redirectUrl);
                exchange.sendResponseHeaders(302, -1); // Redirect status code
                // Closes the response stream
                exchange.getResponseBody().close();
                return;
            } else {
                redirectUrl = "/question/username=" + username; // Redirect to question page
            }
            exchange.getResponseHeaders().set("Location", redirectUrl);
            exchange.sendResponseHeaders(302, -1); // Redirect status code
            // Closes the response stream
            exchange.getResponseBody().close();
        } else {
            String errorMessage = responseBuilder.toString();
            String htmlResponse = "<html>\n" +
                    "    <head>\n" +
                    "        <script>\n" +
                    "            window.onload = function() {\n" +
                    "                alert('" + errorMessage + "');\n" +
                    "                window.history.back();\n" +
                    "            }\n" +
                    "        </script>\n" +
                    "    </head>\n" +
                    "    <body></body>\n" +
                    "</html>";

            // Set the response headers
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, htmlResponse.length());
            // Send the HTML response to the client
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(htmlResponse.getBytes());
            outputStream.close();
        }
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            postLogin(exchange);
        } else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            postQuestion(exchange);
        } else {
            // Handle other request methods
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}