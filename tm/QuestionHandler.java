import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

// Student Name,    Student Number
// Xiaojun Huang,   23011392
// Lingwan Peng,    23478401
// Renyin Zhang,    23719462
// Maxcin Lim,      23146164


public class QuestionHandler implements HttpHandler{
    private static HashMap<String, UserInfo> userMap;

    public static void setUserMap(HashMap<String, UserInfo> userMap) {
        QuestionHandler.userMap = userMap;
    }

    public static String extractUsernameFromPath(String path) {//Method to get the username from the url
        // The path format is "/question/username={{username}}
        String[] parts = path.split("=");
        if (parts.length == 2) {
            return parts[1];
        }
        return null;
    }



    // Check and run user's coding question's answer by comparing expected output
    private boolean checkCodingAnswer(HttpExchange exchange , UserInfo userinfo, String codingAnswer) throws IOException {
        // Retrieve the coding answer from the request body

        System.out.println("User's coding answer: " + codingAnswer);

        String message = String.format("coding:%s,%s,%s", userinfo.CQnum, userinfo.attempts[4], codingAnswer);
        userinfo.scores[4] = Integer.parseInt(Communication.commQB(TestManager.getQbSocket2(),message));

        return false;
    }



    private Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
        Map<String, String> formData = new HashMap<>();
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    formData.put(key, value);
//                    System.out.println(key+":"+value);
                }
            }
        }

        return formData;
    }

    private String getContentFromTextarea(Map<String, String> formData) {
        return formData.get("codingAnswer");
    }

    private void QBNotReadyPage(boolean isQBConnected, HttpExchange exchange) throws IOException {
        // QB is not connected, display the unavailable page
        String response = "<html>\n" +
                "    <head>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <title>Page Unavailable</title>\n" +
                "        <style>\n" +
                "            p{\n" +
                "                color: red;\n" +
                "            }\n" +
                "        </style>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>Page Unavailable</h1>\n" +
                "        <p>The page is currently unavailable. Please try again later.</p>\n" +
                "    </body>\n" +
                "</html>";

        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }


    private void GETHandler(HttpExchange exchange, String username, UserInfo userInfo) throws IOException {
        // display the initial question
        int num;
        if (username != null && userMap.containsKey(username)){
            UserInfo userinfo = userMap.get(username);
            num = userinfo.getNumOfQuestion();
            if (userinfo.CQlang == null) {
                Communication.randomQuestion(username);
            }

            String initialQuestionResponse = num<5?QuestionGenerator.generateMCQ(username):
                    QuestionGenerator.generateCoding(username);
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, initialQuestionResponse.getBytes(StandardCharsets.UTF_8).length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(initialQuestionResponse.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();
            String redirectUrl = "/question" + "/username="+username;
            exchange.getResponseHeaders().set("Location", redirectUrl);
            exchange.sendResponseHeaders(302, -1); // Redirect status code
            // Close the response stream
            exchange.getResponseBody().close();
        }
    }

    private void POSTHandler(HttpExchange exchange, int num,
                             String username, UserInfo userInfo) throws IOException {
        //Get the data from request
        int answer;
        String codingAnswer;
        Map<String, String> formData = parseFormData(exchange);
        codingAnswer = getContentFromTextarea(formData);
//            System.out.println(userInfo.CodingA);//null
        if (formData.get("option")==null){//User didn't choose an option
            answer = 0;
        }else{
            answer = Integer.parseInt(formData.get("option"));
            if (answer != 0) {
                String message = String.format("multiQ:%s,%d,%s",userInfo.Qnum[num-1], answer, userInfo.attempts[num-1]);
                String score = "";
                if (Communication.isSocketOpen(TestManager.getQbSocket1())) {
                    score = Communication.commQB(TestManager.getQbSocket1(),message);
                } else if (Communication.isSocketOpen(TestManager.getQbSocket2())) {
                    score = Communication.commQB(TestManager.getQbSocket2(),message);
                }

                userMap.get(username).scores[num-1] = userMap.get(username).scores[num-1] != 0 ?
                        userMap.get(username).scores[num-1] :Integer.parseInt(score);

            }

        }

        if (num<5){
            if (answer!= userInfo.MCAs[num - 1]){//User changed his/her answer
                if (answer != 0){
                    userInfo.MCAs[num - 1] = answer;
                    userInfo.attempts[num-1]--;
                    if (userInfo.attempts[num-1]<0){
                        userInfo.attempts[num-1] = 0;
                    }
                }
            }
        }
        num++;
        if (num>6){
            num=6;
        }
        userInfo.setNumOfQuestion(num);
        userMap.put(username,userInfo);

        String buttonClicked = formData.get("buttonClicked");
        if (buttonClicked != null && buttonClicked.equals("check")) {
            if (!codingAnswer.equals(userInfo.uCodingA)){
                checkCodingAnswer(exchange ,userInfo,codingAnswer);//TODO:check the answer of coding question
                System.out.println("New: "+codingAnswer);
                System.out.println("Old: "+userInfo.uCodingA);
                userInfo.uCodingA = codingAnswer;
                userInfo.attempts[4]--;
                if (userInfo.attempts[4]<0){
                    userInfo.attempts[4] = 0;
                }
            }
            userMap.put(username, userInfo);
        }
        if ((buttonClicked != null && buttonClicked.equals("prev")) || Objects.equals(buttonClicked, "Previous")) {
            // handle "Previous" button click
            num -= 2;
            userInfo.setNumOfQuestion(num);
            userMap.put(username, userInfo);
            if (num < 1) {
                num = 1;
                userInfo.setNumOfQuestion(num);
                userMap.put(username, userInfo);
            }
        }

        System.out.println(num);
        String nextQuestionResponse = num < 5 ? QuestionGenerator.generateMCQ(username) : QuestionGenerator.generateCoding(username);


        // Redirect the user to the next question or the coding question
        // Set the response headers and send the response
        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, nextQuestionResponse.getBytes(StandardCharsets.UTF_8).length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(nextQuestionResponse.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();

        // Redirect the user to the new URL if "Next" button is clicked
        if (buttonClicked != null && buttonClicked.equals("next")) {
            String redirectUrl = "/question/username=" + username;
            exchange.getResponseHeaders().set("Location", redirectUrl);
            exchange.sendResponseHeaders(302, -1); // Redirect status code
        }

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String username = extractUsernameFromPath(exchange.getRequestURI().getPath());
        UserInfo userInfo = userMap.get(username);
        int num = userInfo.getNumOfQuestion();
        boolean isQBConnected = TestManager.getQbSocket1() != null || TestManager.getQbSocket2() != null;
        if (!isQBConnected){
            QBNotReadyPage(isQBConnected, exchange);
            return;
        }
        if (exchange.getRequestMethod().equalsIgnoreCase("POST")){
            POSTHandler(exchange,num,username,userInfo);
        } else {
            GETHandler(exchange,username,userInfo);
        }
    }

}