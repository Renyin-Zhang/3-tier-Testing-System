import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

// Student Name,    Student Number
// Xiaojun Huang,   23011392
// Lingwan Peng,    23478401
// Renyin Zhang,    23719462
// Maxcin Lim,      23146164

/**
 * This class deals with the students.properties file.
 * Current functionality is to read data from the properties file and
 *  use them to create objects instances of students with corresponding information
 * Writing back to properties file is not yet completed
 */
public class UserInfo{
    public static final String FILE_PATH = "students.properties";

    public static HashMap<String, UserInfo> userMap = new HashMap<>();

//    public static void main(String[] args) {
//        UserInfo database = new UserInfo();
//        Properties properties = database.loadProperties();
//        database.processUsers(properties);
//        //database.printPropertise(properties);
//
//        //database.saveProperties(properties); //requires further work
//        // try not to run with this line if you're testing as it shuffles the order of items in properties file.
//        //if you have to, make sure you can restore it to original order for ease of reading
//        //if you don't mind then otherwise
//    }

    private int studentCount;

    int NumOfQuestion;  // A number to record the user progress of the test
    String Username;
    String Password;
    boolean submitted = false;
    String[] MCQs;  // Stores the questionID get from qb (eg M1)
    int[] MCAs; // Stores the attempts submitted by user, will be sent to QB
    String []Qnum;
    String CQnum;
    String CQlang;
    String CodingQ;
    String uCodingA;

    // Score for each question (0-3). once the current score is 0
    // Proceed to next question with progress saved.
    int[] scores;
    int total_score;
    String []optionA;
    String []optionB;
    String []optionC;
    String []optionD;
    int[] attempts; // Each question has 3 attempts, if the attempt consume user cannot change the answer anymore

    // New UserInfo()
    public UserInfo(){
        this.Username = "";
        this.Password = "";
        NumOfQuestion = 1;
        MCAs = new int[4];
        MCQs = new String[4];
        scores = new int[4];
        total_score = 0;
        attempts = new int[5];
        Arrays.fill(attempts, 3);
        Arrays.fill(MCAs, 0);
    }

    // Properties of a user
    public Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(FILE_PATH)) {
            properties.load(inputStream);
            inputStream.close();
            studentCount = properties.size() / 14; // Assuming each student has 12 properties
            System.out.println("properties loaded.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    // Parse user information 
    public HashMap<String, UserInfo> processUsers(Properties properties) {
        for (int i = 0; i < studentCount; i++) {
            String prefix = "students." + i + ".";
            // access and store other properties
            Username = properties.getProperty(prefix + "user_name");
            Password = properties.getProperty(prefix + "password");
            attempts = new int[4];
            for (int j = 0; j < 4; j++) {
                String attemptKey = prefix + "attempt" + (j + 1);
                String attemptValue = properties.getProperty(attemptKey);
                if (attemptValue != null) {
                    attempts[j] = Integer.parseInt(attemptValue);
                } else {
                    System.out.println("Attempt value is missing or null");
                }
            }
            Qnum = new String[4];
            for (int j=0; j<4;j++) {
                Qnum[j] = properties.getProperty(prefix + "Q" + (j+1));
            }
            CodingQ = properties.getProperty(prefix + "Q5");

            // Scores for each questions [0,0,0,0,0]
            String[] scoresString = properties.getProperty(prefix + "score").split(",");
            // int[] scores = new int[scoresString.length];
            for (int j = 0; j<scoresString.length-1; j++) {
                this.scores[j] = Integer.parseInt(scoresString[i]);
            }

            // Q1-Q4 are multiple choices
            // Use an array to store their question numbers
            // String[] mcqs = new String[4];
            for (int j=0; j<4; j++) {
                this.MCQs[j] = properties.getProperty(prefix + "Q" + (j+1));
            }

            // Multiple choice attempted answers will be type int
            for (int j=1; j<4; j++){
                String attemptKey = prefix + "attempt" + j;
                String attemptValue = properties.getProperty(attemptKey);
                if (attemptValue != null) {
                    MCAs[j] = Integer.parseInt(attemptValue);
                }
                else {
                    System.out.println("Attempt value is missing or null");
                }
            }

            int numQuestion = scores.length; // Number of scores recorded is the number of questions attempted
            int total_score = Integer.parseInt(properties.getProperty(prefix + "total_score"));

            System.out.println(Username);
            // Create a User object or store the user details into the list of users
            UserInfo user = new UserInfo();
            user.setUsername(Username);
            user.setPassword(Password);
            //user.setNumOfQuestion(Qnum); whooo??
            user.setScores(scores);
            user.setMcqs(MCQs);
            userMap.put(Username, user);
        }

        for (Map.Entry<String, UserInfo> entry : userMap.entrySet()) {
            String key = entry.getKey();
            UserInfo userInfo = entry.getValue();
            System.out.println("Key: " + key);
            System.out.println("Value: ");
            System.out.println("Username: " + userInfo.getUsername());
            System.out.println("Password: " + userInfo.getPassword());
            System.out.println("Attempts: " + Arrays.toString(userInfo.getAttempts()));
            System.out.println("Scores: " + Arrays.toString(userInfo.getScores()));
            System.out.println("MCQs: " + Arrays.toString(userInfo.getScores()));
            System.out.println("MCAs: " + Arrays.toString(userInfo.getScores()));
            System.out.println("------------------------");
        }
        return userMap;

    }

    // Assign all user information to (their) Properties
    public void saveProperties(HashMap<String, UserInfo> userMap) {
        Properties properties = new Properties();

        // Iterate over the userMap and store UserInfo data in properties
        int i = 0;
        for (UserInfo user : userMap.values()) {
            String prefix = "students." + i + ".";
            properties.setProperty(prefix + "user_name", user.getUsername());
            properties.setProperty(prefix + "password", user.getPassword());

            properties.setProperty(prefix + "num_of_question", String.valueOf(user.getNumOfQuestion()));
            properties.setProperty(prefix + "coding_question", user.getCodingQ());
            properties.setProperty(prefix + "total_score", String.valueOf(user.getTotalScore()));

            // Set MCQ properties
            String[] mcqs = user.getMcqs();
            for (int j = 0; j < mcqs.length; j++) {
                properties.setProperty(prefix + "mcq" + (j + 1), mcqs[j]);
            }

            // Set MCA properties
            int[] mcas = user.getMcas();
            for (int j = 0; j < mcas.length; j++) {
                properties.setProperty(prefix + "mca" + (j + 1), String.valueOf(mcas[j]));
            }
            i++;
        }

        try (OutputStream outputStream = new FileOutputStream(FILE_PATH)) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Print the properties file for testing
    public void printProperties(Properties properties) {
        Set<String> keys = properties.stringPropertyNames();
        for (String key : keys) {
            String value = properties.getProperty(key);
            System.out.println(key + "=" + value);
        }
        System.out.println("Properties printed.");
    }

    // User's current attemped question
    public int getNumOfQuestion(){
        return NumOfQuestion;
    }

    public void setNumOfQuestion(int NumOfQuestion){
        this.NumOfQuestion = NumOfQuestion;
    }

    // Returns username
    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    // Returns password
    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    // Returns an array of user attempts
    public int[] getAttempts() {return attempts;}

    public void setAttempts(int[] attempts) {this.attempts = attempts;}
    public String[] getMcqs() {
        return MCQs;
    }

    public void setMcqs(String[] mcqs) {
        this.MCQs = mcqs;
    }

    public int[] getMcas() {
        return MCAs;
    }

    public void setMcas(int[] mcas) {
        this.MCAs = mcas;
    }

    // Get question from QB
    public String getCodingQ() {
        return CodingQ;
    }

    public void setCodingQ(String codingQ) {
        this.CodingQ = codingQ;
    }

    public int[] getScores() {
        return scores;
    }

    public void setScores(int[] scores) {
        this.scores = scores;
    }

    // User's total score for test
    public int getTotalScore() {
        return total_score;
    }

    public void setTotalScore(int totalScore) {
        this.total_score = totalScore;
    }

    // To be returned at last page 
    public int scoreCaluclator(){//Calculate the total score of user.
        int sum = 0;
        for (int score :
                this.scores) {
            sum += score;
        }
        return sum;
    }

}

