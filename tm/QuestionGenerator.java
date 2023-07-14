import java.util.HashMap;
import java.io.IOException;

// Student Name,    Student Number
// Xiaojun Huang,   23011392
// Lingwan Peng,    23478401
// Renyin Zhang,    23719462
// Maxcin Lim,      23146164

/*
 * Generates all HTML pages for user.
 * Uses StringBuilder for appending HTML pages in different circumstances.
 */
public class QuestionGenerator {

    private static HashMap<String, UserInfo> userMap;

    public static void setUserMap(HashMap<String, UserInfo> userMap) {
        QuestionGenerator.userMap = userMap;
    }

    // Generates coding exercise question page 
    public static String generateCoding(String username) {
        UserInfo userInfo = userMap.get(username);
        int attempts = userInfo.attempts[4]; // Coding question is the 5th question.
        String language = userInfo.CQlang; // Get which language the user needs to use
        String initial = "";

        // Coding exercise language is Java
        if (language.equals("java")) {
            // Example Java coding question with initialized textarea value
            initial = "class Test {\n" +
                    "    public static void main(String[] args) {\n" +
                    "        // Your code here\n" +
                    "    }\n" +
                    "}";
            userInfo.uCodingA = initial;
        }
        // Correct answer page
        String attemptUseOutPage = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <title> Question 5" + "</title>\n" +
                "        <script>\n" +
                "            function setButtonClicked(button) {\n" +
                "                document.getElementsByName(\"buttonClicked\")[0].value = button;\n" +
                "            }\n" +
                "            function submitForm() {\n" +
                "                document.getElementsByName(\"buttonClicked\")[0].value = \"Submit\";\n" +
                "                document.getElementById(\"codingForm\").submit();\n" +
                "            }\n" +
                "        </script>\n" +
                "        <style>\n" +
                "            h4{\n" +
                "                color: red;\n" +
                "            }\n" +
                "        </style>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h2> Welcome," +"</h2>\n" +
                "        <h3> Test </h3>\n" +
                "\n" +
                "        <h3>" + "Number of Question: 5 (Coding)" + "</h3>\n" +
                "\n" +
                "        <h4>" + "Sorry, you have no attempt for this question, the sample solution is" + "" + "</h3>\n" +//TODO: add the correct answer
                "\n" +
                "        <form action=\"/question/username=" + username + "\" method=\"post\">\n" +
                "            <input type=\"submit\"  value=\"Previous\" name=\"buttonClicked\" onclick=\"setButtonClicked('prev')\">\n" +
                "\n" +
                "        </form>\n" +
                "        <form action=\"/result/username=" + username + "\" method=\"post\">\n" +
                "            <input type=\"submit\"  value=\"Submit Test\" )\">\n" +
                "        </form>\n";
        StringBuilder codingPageHTMLStringBuilder = new StringBuilder();
        if (userInfo.attempts[4]==0){
            return attemptUseOutPage;
        }
        // TODO: Coding question from userinfo here
        // Coding question
        codingPageHTMLStringBuilder.append("<!DOCTYPE html>\n" + 
        "<html>\n" + 
        "    <head>\n" + 
        "        <meta charset=\"UTF-8\">\n" + 
        "        <title> Question 5</title>\n" + 
        "        <script>\n" + 
        "            function setButtonClicked(buttonName) {\n" + 
        "                var textareaContent = document.getElementsByName(\"codingAnswer\")[0].value;\n" + 
        "                document.getElementById(\"textareaContent\").value = textareaContent;\n" + "                document.getElementById(\"buttonClicked\").value = buttonName;\n" + "            }\n" + 
        "        </script>\n" + 
        "    </head>\n" + 
        "    <body>\n" + 
        "        <h3> Test </h3>\n" + 
        "        <h3> Question 5 (")
                .append(language).append(" Coding Question) </h3>\n")
                .append("        <h4>")
                .append(userInfo.CodingQ).append("</h4>\n")
                .append("        <h4> You have ")
                .append(attempts)
                .append("/3 attempts </h4>\n")
                .append("        <form action=\"/question/username=")
                .append(username)
                .append("\" method=\"post\">\n")
                .append("            <label name=\"codingA\">\n")
                .append("                <textarea name=\"codingAnswer\" rows=\"25\" cols=\"150\">")
                .append(initial)
                .append("</textarea> \n")
                .append("            </label>\n")
                .append("            <input type=\"submit\" value=\"check\" name=\"buttonClicked\" onclick=\"setButtonClicked('check')\">\n")
                .append("        </form>\n")
                .append("        <form action=\"/question/username=")
                .append(username)
                .append("\" method=\"post\">\n")
                .append("            <input type=\"submit\" value=\"Previous\" name=\"buttonClicked\" onclick=\"setButtonClicked('prev')\">\n")
                .append("        </form>\n")
                .append("        <form action=\"/result/username=")
                .append(username).append("\" method=\"post\">\n")
                .append("            <input type=\"hidden\" name=\"textareaContent\" id=\"textareaContent\" />\n")
                .append("            <input type=\"submit\" value=\"Submit Test\" name=\"buttonClicked\" onclick=\"setButtonClicked('submit test')\">\n")
                .append("        </form>\n")
                .append("    </body>\n")
                .append("</html>");

        return codingPageHTMLStringBuilder.toString();
    }

    // Generates multiple-choice question page
    public static String generateMCQ(String username) throws IOException {
        UserInfo userInfo = userMap.get(username);
        int numOfMCQ = userInfo.getNumOfQuestion(); // User's current attempted question
        int attempts = userInfo.attempts[numOfMCQ - 1];
        int selectedOption = userInfo.MCAs[numOfMCQ - 1]; // Collect user input
        int score = userInfo.scores[numOfMCQ - 1];
        boolean answered = (score != 0);

        StringBuilder response = new StringBuilder("<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <title> Question " + numOfMCQ + "</title>\n" +
                "        <script>\n" +
                "            function setButtonClicked(button) {\n" +
                "                document.getElementsByName(\"buttonClicked\")[0].value = button;\n" +
                "            }\n" +
                "        </script>\n" +
                "        <style>\n"  +
                "            .warning{ color: red }"+
                "        </style>\n"+
                "    </head>\n" +
                "    <body>\n" +
                "        <h2> Welcome, " + username + "</h2>\n" +
                "        <h3> Test </h3>\n" +
                "\n" +
                "        <h3>" + "Number of Question: " + numOfMCQ + "</h3>\n");



        if (attempts == 0) {
            String message = String.format("check:%s", userInfo.Qnum[numOfMCQ-1]);
            String correctAnswer = "";

            if (Communication.isSocketOpen(TestManager.getQbSocket1())) {
                correctAnswer = Communication.commQB(TestManager.getQbSocket1(),message);
            } else if (Communication.isSocketOpen(TestManager.getQbSocket2())) {
                correctAnswer = Communication.commQB(TestManager.getQbSocket2(),message);
            }

            answered = true;
            response.append("<h3> The Question: ")
            .append(userInfo.MCQs[numOfMCQ - 1])
            .append("</h3>\n")
            .append("<h4>Attempts Left: ")
            .append(attempts)
            .append("/3</h4>\n")
            .append("<h4>Scores: ")
            .append(score)
            .append("</h4>\n")
            .append("<form action=\"/question/username=")
            .append(username)
            .append("\" method=\"post\">\n")
            .append(generateOptionHTML(userInfo.optionA[numOfMCQ - 1], selectedOption, answered, 1))
            .append(generateOptionHTML(userInfo.optionB[numOfMCQ - 1], selectedOption, answered, 2))
            .append(generateOptionHTML(userInfo.optionC[numOfMCQ - 1], selectedOption, answered, 3))
            .append(generateOptionHTML(userInfo.optionD[numOfMCQ - 1], selectedOption, answered, 4))
            .append(numOfMCQ > 1 ? "<input type=\"submit\" value=\"Previous\" onclick=\"setButtonClicked('prev')\">\n" : "")
            .append("<input type=\"submit\" value=\"Next Question\" onclick=\"setButtonClicked('next')\">\n")
            .append("<input type=\"hidden\" name=\"buttonClicked\" value=\"\">\n")
             .append("</form>\n")
             .append("<br>")
             .append("<br>");

            response.append("<h4 class=\"warning\">Sorry, you have no attempt for this question, the correct answer is: " + correctAnswer + "</h4>\n");
        }
        else {
            response.append("<h3> The Question: ")
            .append(userInfo.MCQs[numOfMCQ - 1])
            .append("</h3>\n")
            .append("<h4>Attempts Left: ")
            .append(attempts)
            .append("/3</h4>\n")
            .append("<h4>Scores: ")
            .append(score)
            .append("</h4>\n")
            .append("<form action=\"/question/username=")
            .append(username)
            .append("\" method=\"post\">\n")
            .append(generateOptionHTML(userInfo.optionA[numOfMCQ - 1], selectedOption, answered, 1))
            .append(generateOptionHTML(userInfo.optionB[numOfMCQ - 1], selectedOption, answered, 2))
            .append(generateOptionHTML(userInfo.optionC[numOfMCQ - 1], selectedOption, answered, 3))
            .append(generateOptionHTML(userInfo.optionD[numOfMCQ - 1], selectedOption, answered, 4))
            .append(numOfMCQ > 1 ? "<input type=\"submit\" value=\"Previous\" onclick=\"setButtonClicked('prev')\">\n" : "")
            .append("<input type=\"submit\" value=\"Next Question\" onclick=\"setButtonClicked('next')\">\n")
            .append("<input type=\"hidden\" name=\"buttonClicked\" value=\"\">\n")
            .append("</form>\n")
            .append("<br>")
            .append("<br");
        }
        response.append("    </body>\n").append("</html>");

        return response.toString();
    }

    // Generates user's input for multiple-choice questions by selecting radio button option
    private static String generateOptionHTML(String option, int selectedOption, boolean answered, int optionNumber) {
        String disabled = answered ? " disabled" : "";
        String checked = selectedOption == optionNumber ? " checked" : "";

        return "<label for=\"option" + optionNumber + "\">\n" +
                "    <input type=\"radio\" name=\"option\" value=\"" + optionNumber + "\"" + checked + disabled + "> " + option + "</input>\n" +
                "</label><br>\n";
    }

}
