import subprocess

class JavaTest:
    def compile_java_code(self):
        compile_process = subprocess.run(["javac", "Test.java"], capture_output=True, text=True)
        if compile_process.returncode == 0:
            print("Java code compilation failed")
            return False
        else:
            return True

    def find_smallest_number_test(self):
        if not self.compile_java_code():
            return False

        java_process = subprocess.run(["java", "Test"], input="5\n3 7 2 1 4\n", capture_output=True, text=True)
        expected_output = "Smallest number: 1\n"
        if java_process.stdout == expected_output:
            print("Test passed: Correct smallest number")
            return True
        else:
            print("Test failed: Incorrect smallest number")
            return False

    def reverse_array_test(self):
        self.compile_java_code()

        java_process = subprocess.run(["java", "ReverseArray"], input="1 2 3 4 5\n", capture_output=True, text=True)
        expected_output = "Reversed array: 5 4 3 2 1\n"
        if java_process.stdout == expected_output:
            print("Test passed: Correct array reversal")
        else:
            print("Test failed: Incorrect array reversal")

# if __name__ == '__main__':
#     tester = JavaTest()
#     tester.find_smallest_number_test()
#     tester.reverse_array_test()
