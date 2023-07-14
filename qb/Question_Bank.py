import os
import socket
import json
import random
import Check_Coding_Questions
import sys

# Student Name,    Student Number
# Xiaojun Huang,   23011392
# Lingwan Peng,    23478401
# Renyin Zhang,    23719462
# Maxcin Lim,      23146164

def get_user_input():
    if len(sys.argv) < 3:
        print("Usage: python3 test.py <IP address> <port>")
        sys.exit(1)

    ip_address = sys.argv[1]
    port = int(sys.argv[2])

    return ip_address, port

# Get own computer's ip address (for own test)
def get_ip_address():
    # Create a temporary socket
    temp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

    try:
        # Connect to a remote server (doesn't actually send any data)
        temp_socket.connect(("8.8.8.8", 80))

        # Get the socket's own address, which includes the IP address
        ip_address = temp_socket.getsockname()[0]
    finally:
        # Close the temporary socket
        temp_socket.close()

    return ip_address

server_address = get_ip_address() # Specify TM's address
server_port = 5997  # Specify TM's port
server_address, server_port = get_user_input()

def start_client():
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.connect((server_address, server_port))
    print("Connected to server:", server_address, ":", server_port)

    # Start a loop to continuously receive responses
    get_from_TM(client_socket)


def get_from_TM(sock):
    while True:
        response = sock.recv(1024)
        if response.decode() != "":
            print("\nTM requests:", response.decode())
            string = response.decode().rstrip("\n").split(":", 1)
            print(string)

            if(string[0] == "multiQ"): # marking MQ
                send_to_TM(sock, multiQ(string[1]))

            if(string[0] == "check"): # check answer of specific question
                send_to_TM(sock, checkAnswer(string[1]))

            if(string[0] == "randomMCQ"): # select random MCQ questions
                send_to_TM(sock, randomMCQ())

            if(string[0] == "randomCQ"): # select random CQ questions
                send_to_TM(sock, randomCQ())

            if(string[0] == "coding"): # marking CQ
                send_to_TM(sock, checkCQ(string[1]))

def send_to_TM(sock, message):
    print("Ready to send to TM: " + message)
    message = message + '\n'
    sock.send(message.encode())

def checkAnswer(message):
    # Load the JSON file
    with open('multipleChoice.json') as f:
        data = json.load(f)

    for question in data['questions']:
        if(message == question['question_number']):
            # Return user's score
            return(str(question['correct']))

def randomCQ():
    with open('codingQuestions.json') as f:
        data = json.load(f)

    # Select 1 random question
    question = random.sample(data['questions'], 1)
    randomCQuestion = ""

    randomCQuestion = f"{question[0]['question_number']};{question[0]['language']};{question[0]['question']}"

    return randomCQuestion

def randomMCQ():
    # Load the JSON file
    with open('multipleChoice.json') as f:
        data = json.load(f)

    # Select 4 random questions
    questions = random.sample(data['questions'], 4)
    randomQuestion = ""

    for question in questions:
      randomQuestion = randomQuestion + f"{question['question_number']};{question['question']};"
      for answer in question['answers']:
        randomQuestion = randomQuestion + answer + ';'

    return randomQuestion

def checkCQ(answer):
    # Load the JSON file
    with open('codingQuestions.json') as f:
        data = json.load(f)

    answers = answer.split(",") # answers['CQnum', 'attempt', 'answer']
    print(answers[2])
    """
    Searching question in question bank, execute user's answer according to
    its language. run_java() and run_py() would return true or false indicating
    whether user's answer is acceptable.
    """
    for question in data['questions']:
        if(answers[0] == question['question_number']):
            if(question['language'] == "java"):
                file = ""
                if (answers[0] == "C2"):
                    file = add_method_to_java_class(userAnswer=answers[2],number=answers[0])
                    Check_Coding_Questions.write_string_to_file("Test.java",file)
                    correct = Check_Coding_Questions.check_find_smallest_number()
                    print(correct)
                score = int(answers[1])
                os.remove("Test.java")
                break
            elif(question['language'] == "python" ):
                Check_Coding_Questions.write_string_to_file("main.py",answers[2])
                Check_Coding_Questions.run_py()
                score = int(answers[1])
                os.remove("main.py")
                break
            else:
                score = 0
                break

    # Return user's score
    return (str(score))

def multiQ(answer):
    # Load the JSON file
    with open('multipleChoice.json') as f:
        data = json.load(f)

    answers = answer.split(",") # answers['Qnum', 'answer', 'attempt']

    for question in data['questions']:
        if(answers[0] == question['question_number']):
            if(int(answers[1]) == question['correct']):
                score = int(answers[2])
                break
            else:
                score = 0
                break

    # Return user's score
    return(str(score))

def add_method_to_java_class(userAnswer,number):
    if(number=="C2"):
        java_code = """
        class Test {

            public static void main(String[] args) {
                int[] arr = {3, 7, 2, 1, 4};
                int smallestNumber = findSmallestNumber(arr);
                System.out.println("Smallest number: " + smallestNumber);
            }
        }
        """
    if(number == "C4"):
        java_code = """class Test {
    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5};
        int[] reversedArr = reverseArray(arr);
        StringBuilder sb = new StringBuilder("Reversed array: ");
        for (int i = 0; i < reversedArr.length; i++) {
            sb.append(reversedArr[i]);
            if (i < reversedArr.length - 1) {
                sb.append(" ");
            }
        }
        String expectedOutput = sb.toString();
        System.out.println(expectedOutput);
    }
}
"""
    class_start = java_code.index("class")
    class_end = java_code.index("{", class_start)
    method_declaration = userAnswer

    modified_java_code = java_code[:class_end] + method_declaration + java_code[class_end:]
    return modified_java_code







if __name__ == '__main__':
    start_client()