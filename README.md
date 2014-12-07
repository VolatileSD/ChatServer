ChatServer
==========

## Version 0

### Summary:

Implement a chat server which allows users to authenticate, chose room and send text lines to other users
in the same room. The service should be scalable in the number of connected users, allow subscription
of notable events, and provide a REST interface for management and description.

### How to connect

### Command line client (e.g. telnet)

All commands start with the ":" character for an easiest parsing.

### List of commands
* __:h/:help__  outputs a list of all available commands and the current status (room, log state);
* __:login *user* *passwd*__ logs the user to the chat creating a new user if it is the first time;
* __:listrooms__ lists all available rooms;
*  __:cr/:changeroom *newroom*__ transfers the user from his current room to *newroom*;
* __:listusers__ lists all users from the current room;
* __:logout__ The user is logged out, having to log in again to chat.


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


