How to run TM application:

TestManager is the main file all subsidiary files depend on 

All subsidiary files are TestManager dependencies

Instructions:
"cd" into directory of "TM" where "TestManager.java" is located

Compile "TestManager.java" with:
"javac TestManager.java"

Start TM server with:
"java TestManager <Browser's port> <QB1's port> <QB2's port>"

Terminal console should print:
"Server Start and Listening to port (for browser): 2197"
"Server Start and Listening to port (for QB): 5997 and 5998"


Enter this in search bar of your chosen browser:
http://127.0.0.1:2197/