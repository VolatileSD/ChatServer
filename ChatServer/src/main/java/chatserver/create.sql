connect remote:localhost/ChatServer admin admin
script sql
begin
commit
let a  = ALTER DATABASE CUSTOM useLightweightEdges=true
#
# User
#
DROP CLASS User
CREATE CLASS User EXTENDS V
CREATE PROPERTY User.username string
CREATE PROPERTY User.password string
CREATE PROPERTY User.roomLog embeddedmap
CREATE PROPERTY User.registrationDate datetime
 
CREATE INDEX User.username unique
 
#
# Message
#
DROP CLASS Message
CREATE CLASS Message EXTENDS V
CREATE PROPERTY Message.id long
CREATE PROPERTY Message.message string
CREATE PROPERTY Message.date datetime
 
CREATE INDEX Message.id unique
 
#
# Room 
#
DROP CLASS Room
CREATE CLASS Room EXTENDS V
CREATE PROPERTY Room.name string

CREATE INDEX Room.name unique
 
#
# EDGES
#
DROP CLASS Messages
CREATE CLASS Messages EXTENDS E
# from Room to Message
 
DROP CLASS PrivateMessages
CREATE CLASS PrivateMessages EXTENDS E
# from User to Message
 
 
return $e
end