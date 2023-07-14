import com.sun.net.httpserver.HttpServer;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Properties;

// Student Name,    Student Number
// Xiaojun Huang,   23011392
// Lingwan Peng,    23478401
// Renyin Zhang,    23719462
// Maxcin Lim,      23146164


/*
 * Test Manager (TM) is the server that communicates with QBs (Question Banks) through a given port.
 * Displays content to web browser through another port for users.
 * Implements numerous handlers to 'handle' user input and requests.
 */
public class TestManager {

    private static Socket qbSocket1;
    private static Socket qbSocket2;

    public static Socket getQbSocket1() {
        return qbSocket1;
    }

    public static Socket getQbSocket2() {
        return qbSocket2;
    }

    private static HashMap<String, UserInfo> userMap = new HashMap<>();

    public static void main(String[] argv) throws Exception{ //http://172.20.10.2:5997/
        // port1 for Test Manager
        int port1 = 2197;

        // port2&3 for Question Banks
        int port2 = 5997;
        int port3 = 5998;
        if(argv.length == 3){
            port1 = Integer.parseInt(argv[0]);
            port2 = Integer.parseInt(argv[1]);
            port3 = Integer.parseInt(argv[2]);
        }

        // Methods for creating an object for each student
        UserInfo database = new UserInfo();
        Properties properties = database.loadProperties();
        userMap = database.processUsers(properties);
        //database.printProperties(properties);

        QuestionHandler.setUserMap(userMap);
        QuestionGenerator.setUserMap(userMap);
        Communication.setUserMap(userMap);
        // Create an HTTP server on port1 for TM
        HttpServer server = HttpServer.create(new InetSocketAddress(port1), 0);

        // Create handlers to serve the HTML pages for each user
        server.createContext("/", new LoginHandler(userMap));
        server.createContext("/question", new QuestionHandler());
        server.createContext("/result", new LastPageHandler(userMap));

        // Start the server on port1 for TM
        server.start();
        System.out.println("Server Start and Listening to port (for browser): " + port1);

        try {
            // A server on port2 & port3 for QBs
            ServerSocket serverSocket1 = new ServerSocket(port2);
            ServerSocket serverSocket2 = new ServerSocket(port3);
            System.out.println("Server Start and Listening to port (for QB): " + port2 + " and " +port3);

            while (true) {
                // Accepts connection once QB clients are ready
                qbSocket1 = serverSocket1.accept();
                System.out.println("Connected to QB1: " + qbSocket1.getInetAddress().getHostAddress());
                qbSocket2 = serverSocket2.accept();
                System.out.println("Connected to QB2: " + qbSocket2.getInetAddress().getHostAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}