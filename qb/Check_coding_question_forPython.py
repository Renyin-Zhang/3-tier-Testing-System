from io import StringIO
import subprocess

import os
import sys
import unittest


'''Three functions dealing with the coding questions, compile the string submitted from web browser in the TM(if c or java,
# and execute each file, if success, check whether the answer is same as the result, if failure, print the error to from
# TM to the user.
'''

def write_string_to_file(file_name, content):
    try:
        with open(file_name, 'w') as file:
            file.write(content)
        print(f"Successfully wrote the string to the file '{file_name}'.")
    except IOError:
        print(f"An error occurred while writing to the file '{file_name}'.")



def run_java():
    try:
        result = subprocess.run(['javac', "Test.java"], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)

        if result.returncode == 0:
            print("Compilation successful")
            subprocess.run(["java", "Test"])
        else:
            print("Compilation error:")
            print(result.stderr)

    except FileNotFoundError:
        print("javac compiler not found. Please make sure it is installed and added to the system's PATH.")


def run_py():
    try:
        result = subprocess.run(['python3', "main.py"], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)

        if result.returncode == 0:
            print("Execution successful")
            print(result.stdout)

        else:
            print("Execution failed:")
            print(result.stderr)

    except FileNotFoundError:
        print("Python interpreter not found. Please make sure it is installed and added to the system's PATH.")


def execute_python_code(filename):
    try:
        with open(filename, 'r') as file:
            code = file.read()

        # Capture stdout by redirecting it to a variable
        stdout = sys.stdout
        sys.stdout = captured_output = StringIO()

        exec(code)

        # Restore the original stdout
        sys.stdout = stdout

        # Get the captured output
        output = captured_output.getvalue()

        # Process the output as needed
        print("Output:", output)

    except IOError:
        print(f"An error occurred while reading the file '{filename}'.")
    except Exception as e:
        print(f"An error occurred while executing the code in '{filename}': {e}")

    return output

# Example usage
python_answer_file = "main.py"
output = execute_python_code(python_answer_file)


# Test class for code testing
class MyCodeTests(unittest.TestCase):
    def test_c1(self):
        # Question C1: Write a Python program to find the largest number in a list nums = [1, 2, 3, 4, 5]
        code = "nums = [1, 2, 3, 4, 5]\n"
        code += "largest = max(nums)\n"
        code += "print(largest)"
        expected_output = "5"

        # Write the code to a file
        Check_Coding_Questions.write_string_to_file("main.py", code)

        # Execute the code and capture the output
        stdout = StringIO()
        stderr = StringIO()
        subprocess.run(['python3', 'main.py'], stdout=stdout, stderr=stderr, text=True)
        output = stdout.getvalue().strip()

        # Clean up the temporary file
        os.remove("main.py")

        # Assert the output
        self.assertEqual(output, expected_output)

    def test_c2(self):
        # Question C2: Write a Java program to find the smallest number in an array nums = [1, 2, 3, 4, 5]
        code = "class Main {\n"
        code += "  public static void main(String[] args) {\n"
        code += "    int[] nums = {1, 2, 3, 4, 5};\n"
        code += "    int smallest = nums[0];\n"
        code += "    for (int i = 1; i < nums.length; i++) {\n"
        code += "      if (nums[i] < smallest) {\n"
        code += "        smallest = nums[i];\n"
        code += "      }\n"
        code += "    }\n"
        code += "    System.out.println(smallest);\n"
        code += "  }\n"
        code += "}"
        expected_output = "1"

        # Write the code to a file
        Check_Coding_Questions.write_string_to_file("Test.java", code)

        # Compile and run the Java code
        Check_Coding_Questions.run_java()

        # Capture the output
        stdout = StringIO()
        stderr = StringIO()
        subprocess.run(['java', 'Test'], stdout=stdout, stderr=stderr, text=True)
        output = stdout.getvalue().strip()

        # Clean up the temporary file
        os.remove("Test.java")
        os.remove("Test.class")

        # Assert the output
        self.assertEqual(output, expected_output)

    def test_c3(self):
        # Question C3: Write a Python program to reverse a string Hello
        code = "string = 'Hello'\n"
        code += "reversed_string = string[::-1]\n"
        code += "print(reversed_string)"
        expected_output = "olleH"

        # Write the code to a file
        Check_Coding_Questions.write_string_to_file("main.py", code)

        # Execute the code and capture the output
        stdout = StringIO()
        stderr = StringIO()
        subprocess.run(['python3', 'main.py'], stdout=stdout, stderr=stderr, text=True)
        output = stdout.getvalue().strip()

        # Clean up the temporary file
        os.remove("main.py")

        # Assert the output
        self.assertEqual(output, expected_output)



if __name__ == '__main__':
    unittest.main()