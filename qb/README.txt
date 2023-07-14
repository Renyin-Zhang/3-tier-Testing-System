How to run QB application:

Instructions
"cd" into directory of "QB" where "Question_Bank.py" is located

Compile and run "Question_Bank.py" with:
"python3 Question_Bank.py <IP address> <port>"

Terminal console should print:
"Ready to send to TM:"


Note:

QBs can indeed receive the user's attempt for the coding question and can run the coding question, only java is able to run. We didn't attempt to pass to the correctness of the coding question back, so TM is unable to catch the correctness of that coding question and post to the current user.

the file Check_coding_question_forPython.py attempted to check user answer for python coding questions. However the test is not completed under the time, so may have errors when running, and this is only a sample of what we are trying to achieve.