ChatServer
==========

## Version 0

Deliver by 14 december 2014

### Summary:

Implement a chat server which allows users to authenticate, chose room and send text lines to other users
in the same room. The service should be scalable in the number of connected users, allow subscription
of notable events, and provide a REST interface for management and description.

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

### Clients

There should be three clients: a chat client, for end users, which also allows listing rooms, chosing room
and listing users through the REST API (even though it should be possible to use telnet + an http client,
less pleasantly); an administration client which uses the REST API (even if some features can be trivially
acessible by a browser and others less pleasantly by a generic http client); a notification console, which
allows observing the system by chosing relevant events to subscribe to.

### Server

The server should be written in Java, using relevant paradigms for the several components, namely
actors, message-oriented and resource-oriented, through Quasar, ZeroMQ and Dropwizard.




