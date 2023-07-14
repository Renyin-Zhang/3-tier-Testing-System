

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



from test_java import JavaTest

def check_find_smallest_number():
    tester = JavaTest()
    return tester.find_smallest_number_test()

def check_reverse_array():
    tester = JavaTest()
    return tester.reverse_array_test()

from test_python import PythonTest

def check_find_largest_number():
    tester = PythonTest()
    return tester.find_largest_number_test()

def check_reverse_array():
    tester = PythonTest()
    return tester.reverse_array_test()

def check_calculate_sum():
    tester = PythonTest()
    return tester.calculate_sum_test()





print(check_reverse_array())
print(check_find_smallest_number())