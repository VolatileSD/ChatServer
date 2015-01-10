ChatServer
==========

## Version 0

### Summary:

Implement a chat server which allows users to authenticate, chose room and send text lines to other users
in the same room. The service should be scalable in the number of connected users, allow subscription
of notable events, and provide a REST interface for management and description.

### How to connect
Prerequisite: This projects requires that you have [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html), [0MQ 3.2.5](http://zeromq.org/intro:get-the-software) and Maven installed.

#### Download compiled jars
Available soon.

#### Compile the most recent version
1. Download the package, go to the Common folder and compile with "mvn clean package";
2. Go to the ChatServer folder and run the "dbServer" file to initialize the database;
3. Run the "run" file to compile and run the server;
4. Go to the NotificationClient folder and tun the "run" file to start receiving notifications.

##### GUI client
Go to the ChatClient folder and run the file "run" as many times as GUI clients you want.

##### Command line client (e.g. telnet)
To use telnet type "telnet localhost 1111"
All commands start with the ":" character for an easiest parsing.

##### List of commands
* :create username password
* :remove username password
* :login username password
* :logout
* :cr/:changeroom roomName
* :private user <message>
* :inbox
* :h/:help


### Features

* User registration, given name and password; registration removal; a user should be authenticated
to use the service;
* Choice of room (from existing ones), to which text messages will be sent;
* Sending of private messages to other connected users;
* Have a simple text-based protocol to allow simple chat clients, being usable by telnet;
* Have a REST API for management and description: e.g., room creation/removal, list of rooms,
list of users in room;
* Have a notification API to allow subscribing to relevant events: room creation/removal, user joining/leaving
room;


