# Santorini board game (ING-SW Prova Finale 2019-2020)
Project's aim is to develop an online distributed version of the table game Santorini from Cranio Creations, the software is developed using the Distributed MVC pattern with java programming language. We developed the "Complete Rules + CLI + GUI + Socket + 2 AF" implementation.

### Images of the game
The client application can be launched either via CLI or GUI. This is what the user sees as he/she launches the game.

![alt text](https://github.com/marcopetri98/Santorini_JavaGame_INGSW/blob/master/deliveries/images/Home.png)

Once the player starts a game on a server, and there are enough players on the server to start a match. Players will first choose a colors, and one player will be randomly chosen to select gods for the match. This is what the user sees:

![alt text](https://github.com/marcopetri98/Santorini_JavaGame_INGSW/blob/master/deliveries/images/Choose_god.png)

Then, all players will have to choose their god and the match will start. The first thing in the game will be to position the warriors. Here is what the first user will see:

![alt text](https://github.com/marcopetri98/Santorini_JavaGame_INGSW/blob/master/deliveries/images/Preparation.png)

After the prepration, the players will play in turns. The first thing the player have to do is to move. Here is the interface when the player has select a warrior that can move:

![alt text](https://github.com/marcopetri98/Santorini_JavaGame_INGSW/blob/master/deliveries/images/Move.png)

Before the turn ends, this is the interface the user will see (he can undo the movement in 5 seconds):

![alt text](https://github.com/marcopetri98/Santorini_JavaGame_INGSW/blob/master/deliveries/images/Moved_with_undo_counter.png)

Once the player has moved, he/she can build with a similar interface to that of moving: cells in which the user can build will be yellow. Here is the graphics after the player has built something:

![alt text](https://github.com/marcopetri98/Santorini_JavaGame_INGSW/blob/master/deliveries/images/Build_with_undo_counter.png)

Once the player's turn is ended, he needs to wait others turn. Here is the interface (on the left is highlighted the active player and its god):

![alt text](https://github.com/marcopetri98/Santorini_JavaGame_INGSW/blob/master/deliveries/images/Others_turn.png)

## Project description requirements

### Software requirements
The first thing to do to install Santorini software is to install java 14 version, it is downloadable from oracle website (https://www.oracle.com/java/technologies/javase-jdk14-downloads.html), no other software is needed to run the application.
The project uses maven to deploy, build and test with JUnit, this is the reason why these two components for java must be installed in your computer before deploying or testing the application.

### Documentation
Documentation for this software comes with this file readme and two different UML file that can be found in the following unordered list, one of this UML files is a general representation about the program's structure and the other is the definitive UML containing every element of the application (some arrows as well some trivial methods have been excluded in order to maintain a cleaner view).
- [General UML](https://github.com/marcopetri98/Santorini_JavaGame_INGSW/blob/master/deliveries/UML/UML_Generic.pdf)
- [Detailed UML](https://github.com/marcopetri98/Santorini_JavaGame_INGSW/blob/master/deliveries/UML/UML_Final.pdf)
#### Compiling the application using maven
This software has been developed with Maven. In order to deploy the application, both Maven and openJFX are needed. To compile the application open a terminal in the project directory and run this command:
```
mvn clean package
```
The "clean" command deletes any precedent build, while the "package" command actually creates the new .jar packages in the "target" directory.<br/>
The .jar packages can be used on every OS and have been correctly tested both on Linux and Windows.

### Running the application
#### Launching the server
Server has been deployed with java archive, server hasn't a graphical user interface and for this reason it can only be started using the command line of your operating system, once you've deployed the application as showed in previous steps of the documentation you can find the server jar file on target folder. Using java you only need the instruction to launch the server and no options are needed, so write the following command:
```
java -jar Santorini-Server.jar-jar-with-dependencies.jar
```

#### Launching the CLI
As for the server, CLI version of the game can only be started from the command line of your operating system, to run client CLI you must write the following command:
```
java -jar Santorini-CLI.jar-jar-with-dependencies.jar
```
In order to have the best experience, consider using Linux or WSL on Windows.

#### Launching the GUI
GUI version of the game is slightly different from the other ones, it can be started both from the command line of your operating system and can be started also from your operating system graphical interface. If you want to start the GUI client using the command line you can use the same command you used for the previous versions of the readme (server and cli client) with the following command:
```
java -jar Santorini-GUI.jar-jar-with-dependencies.jar
```
By the way you can also start gui like every other program with a graphical user interface on your computer, you can go to the java archive folder for the GUI client and double click its icon and wait till it starts.

### Functionalities
We developed the following functionalities:
* Complete rules
* CLI Client
* GUI Client
* Socket
* 2 Advanced functionalities: "Multiple Matches" and "Undo"

### Tests
Here are the results obtained from the tests that have been done on our project using JUnit. It should be noticed that some god classes are tested with less than 90% of coverage because of the presence of many trivial untested getter and setter methods. The important methods present a minimum of 98% coverage.

![alt text](https://github.com/marcopetri98/Santorini_JavaGame_INGSW/blob/master/deliveries/Tests/tests_1.PNG)
![alt text](https://github.com/marcopetri98/Santorini_JavaGame_INGSW/blob/master/deliveries/Tests/tests_2.PNG)
![alt text](https://github.com/marcopetri98/Santorini_JavaGame_INGSW/blob/master/deliveries/Tests/tests_3.PNG)

