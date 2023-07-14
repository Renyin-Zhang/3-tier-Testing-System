import subprocess

class PythonTest:
    def run_python_code(self, program_path, input_data):
        try:
            process = subprocess.run(["python3", "main.py"], input=input_data,
                                     capture_output=True, text=True)

            if process.returncode != 0 or process.stderr:
                print("Python code execution failed")
                return False, process.stderr

            return True, process.stdout.strip()
        except Exception as e:
            print("An error occurred:", str(e))
            return False, str(e)

    def find_largest_number_test(self):
        result, output = self.run_python_code("find_largest_number.py", "5\n3 7 2 1 4\n")
        expected_output = "Largest number: 7"

        if result and output == expected_output:
            print("Test passed: Correct largest number")
            return True
        else:
            print("Test failed: Incorrect largest number")
            return False

    def reverse_array_test(self):
        result, output = self.run_python_code("reverse_array.py", "1 2 3 4 5\n")
        expected_output = "Reversed array: 5 4 3 2 1"

        if result and output == expected_output:
            print("Test passed: Correct array reversal")
            return True
        else:
            print("Test failed: Incorrect array reversal")
            return False

    def calculate_sum_test(self):
        result, output = self.run_python_code("calculate_sum.py", "3 5\n")
        expected_output = "Sum: 8"

        if result and output == expected_output:
            print("Test passed: Correct sum calculation")
            return True
        else:
            print("Test failed: Incorrect sum calculation")
            return False

if __name__ == '__main__':
    tester = PythonTest()
    tester.find_largest_number_test()
    tester.reverse_array_test()
    tester.calculate_sum_test()
