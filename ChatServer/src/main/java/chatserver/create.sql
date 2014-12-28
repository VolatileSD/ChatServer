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
CREATE INDEX User.username unique
 
#
# Message
#
DROP CLASS Message
CREATE CLASS Message EXTENDS V
 
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
# from Message to User
 
 
return $e
end