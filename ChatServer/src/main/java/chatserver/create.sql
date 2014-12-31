connect localhost root root
create database remote:localhost/ChatServer root root plocal
script sql
begin
commit
let a  = ALTER DATABASE CUSTOM useLightweightEdges=true
#
# User
#
CREATE CLASS User EXTENDS V
CREATE PROPERTY User.username string
CREATE PROPERTY User.password string
CREATE PROPERTY User.registrationDate datetime
CREATE PROPERTY User.loggedIn boolean
CREATE PROPERTY User.active boolean
CREATE INDEX User.username unique
 
#
# Message
#
CREATE CLASS Message EXTENDS V
CREATE PROPERTY Message.from string
CREATE PROPERTY Message.to string
CREATE PROPERTY Message.text string
CREATE PROPERTY Message.date datetime

#
# Room 
#
CREATE CLASS Room EXTENDS V
CREATE PROPERTY Room.name string
CREATE PROPERTY Room.creationDate datetime
CREATE PROPERTY Room.active boolean
CREATE INDEX Room.name unique
 
#
# EDGES
#
CREATE CLASS Messages EXTENDS E
# from Room to Message
 
CREATE CLASS PrivateMessages EXTENDS E
# from User to Message
# from Message to User
 

INSERT INTO Room set name = "Main", creationDate = sysdate(), active = true
 
return $e
end
