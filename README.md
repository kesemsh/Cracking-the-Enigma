# Cracking the Enigma
Repository for Cracking the Enigma project, developed during a Java course in The Academic College of Tel Aviv, Yaffo. July 2022.
  
This project consisted of the development of an Enigma machine, capable of encrypting and decrypting messages, and utilizing code cracking techniques.  
There are 3 [Versions](https://github.com/orelgs/Cracking-the-Enigma#versions) to this project.  
The latest version includes the enigma machine base in an HTTP-based client-server architecture, with a fun code breaking game, demonstrating web app and multithreading capabilities.

# Versions

### [Version 1](https://github.com/orelgs/Cracking-the-Enigma/releases/tag/Cracking-the-Enigma-v1)
The basic Enigma machine, wrapped in a smart engine.  
User interface through console.  
Machine input through XML files (Examples in relevant Source Code).  
Capabilities:
1. Load machine from XML file.
2. Show current loaded machine state.
3. Set the machine configuration manually.
4. Set the machine configuration randomly.
5. Process input (encrypt text).
6. Reset the machine configuration.
7. Show the machine history and statistics.
8. Save the machine to Magic file (custom serialization of the machine, with history and current configuration).
9. Load the machine from Magic file.
  
*Machine configuration is shown as following:
1. Chosen rotor ids in order, separated by commas (Example: <45,27,94>).
2. Rotor positions (with distance from notch in parenthesis) in order, characters separated by commas (Example: <A(2),O(5),!(20)>).
3. Reflector id in Roman letters (Example: <III>).
4. Plugs separated by commas, each plug is separated by a pipe(|) (Example: <A|Z,D|E>).

### [Version 2](https://github.com/orelgs/Cracking-the-Enigma/releases/tag/Cracking-the-Enigma-v2)
The basic Enigma machine, wrapped in a smart engine.  
This time the app includes the ability to crack encrypted text, and decrypt it using Brute Force.  
The decryption process uses multi-threading to make the job a lot quicker.  
User interface through Java FX application.  
Machine input through XML files (Examples in relevant Source Code).  
*Encrypted text must be a part of the dictionary of allowed words - defined in the XML file)  
Capabilities:  
All previous capabilities plus:  
1. Visual keyboard.
2. Automatic text decryption using Brute Force (with real time process view + statistics).

*The JavaFX application JAR file requires Java 1.8 to run.

### [Version 3](https://github.com/orelgs/Cracking-the-Enigma/releases/tag/Cracking-the-Enigma-v3)
This version takes the brute force decryption one step further and introduces a mini game.  
The Enigma machine and the engine are now accessible through a web application.  
There are now 3 applications:  
1. UBoat - The German submarine in charge of setting the machine configuration, and encrypting the text.  
   Essentially, the UBoat starts a contest.
2. Ally - The decryption team participating in the UBoat's contest, attempting to be the first to decrypt the text.
   There are multiple allies competing in the contest.
3. Agent - A member of the Ally's team.
   In charge of doing the actual brute force work required to decrypt the text.
  
The web application (uses Tomcat) allows synchronization of the contests between those entities.  
The server side logic included dealing with vast multithreading aspects (such as: thread pools manipulation, internal blocking queue management, threads synchronization, ETC).  
The front end included 3 types of clients, developed as 3 independent desktop applications (Java FX).  
Data delivery between the server and clients was based on various pull techniques.  
The project included intergration and working with 3rd parties:
- GSON for json handling
- OKHttp for the client app.
  
*The JavaFX applications JAR files require Java 1.8 to run.  
**The web application WAR file requires Tomcat to run.
