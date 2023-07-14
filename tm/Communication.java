import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

// Student Name,    Student Number
// Xiaojun Huang,   23011392
// Lingwan Peng,    23478401
// Renyin Zhang,    23719462
// Maxcin Lim,      23146164

/*
 * Handles the communication between TM (Test Manager) and QBs (Question Banks).
 * Communication channel via ports and messages.
 * Questions, answers and user input and requests are relayed through here.
 */
public class Communication {
    private static HashMap<String, UserInfo> userMap;

    public static void setUserMap(HashMap<String, UserInfo> userMap) {
        Communication.userMap = userMap;
    }

    // Sends information to QB with message
    public static String commQB(Socket socket, String message) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

        // Prepare outgoing messages
        writer.println(message);

        // Prepare incoming messages
        String QBmessage;
        while ((QBmessage = reader.readLine()) != null) {
            if (!QBmessage.isEmpty()) break;
        }
        return QBmessage;
    }

    public static boolean isSocketOpen(Socket socket) { // Check whether the socket is open and connected
        if (socket == null) {
            return false;
        }
        return socket.isConnected() && !socket.isClosed();
    }

    // Produces and assigns a question from a selected QB with relevant information 
    public static void randomQuestion(String username) throws IOException{
        UserInfo userInfo = userMap.get(username);

        String randomQs = "";
        String randomCQs = "";

        if (isSocketOpen(TestManager.getQbSocket1())) {
            randomQs = commQB(TestManager.getQbSocket1(), "randomMCQ");
            System.out.println("Receive randomMCQ from QB1");
        } else if (isSocketOpen(TestManager.getQbSocket2())) {
            randomQs = commQB(TestManager.getQbSocket2(), "randomMCQ");
            System.out.println("Receive randomMCQ from QB2");
        } else {
            throw new RuntimeException("No available QB Socket.");
        }

        if (isSocketOpen(TestManager.getQbSocket2())) {
            randomCQs = commQB(TestManager.getQbSocket2(), "randomCQ");
            System.out.println("Receive randomCQ from QB2");
        } else if (isSocketOpen(TestManager.getQbSocket1())) {
            randomCQs = commQB(TestManager.getQbSocket1(), "randomCQ");
            System.out.println("Receive randomCQ from QB1");
        } else {
            throw new RuntimeException("No available QB Socket.");
        }

        String []randomQ = randomQs.split(";");
        String []randomCQ = randomCQs.split(";");

        // questionID in QB
        userInfo.Qnum = new String[]{randomQ[0], randomQ[6], randomQ[12], randomQ[18]};

        // question text
        userInfo.MCQs = new String[]{randomQ[1], randomQ[7], randomQ[13], randomQ[19]};

        // option A for MQ
        userInfo.optionA = new String[]{randomQ[2], randomQ[8], randomQ[14], randomQ[20]};

        // option B for MQ
        userInfo.optionB = new String[]{randomQ[3], randomQ[9], randomQ[15], randomQ[21]}; 

        // option C for MQ
        userInfo.optionC = new String[]{randomQ[4], randomQ[10], randomQ[16], randomQ[22]}; 

        // option D for MQ
        userInfo.optionD = new String[]{randomQ[5], randomQ[11], randomQ[17], randomQ[23]};

        userInfo.CQnum = randomCQ[0]; // CQ ID in QB
        userInfo.CQlang = randomCQ[1]; // CQ language
        userInfo.CodingQ = randomCQ[2]; // CQ text
    }

}
