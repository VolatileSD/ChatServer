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
CREATE INDEX User.username unique
 
#
# Message
#
CREATE CLASS Message EXTENDS V
 
#
# Room 
#
CREATE CLASS Room EXTENDS V
CREATE PROPERTY Room.name string

CREATE INDEX Room.name unique
 
#
# EDGES
#
CREATE CLASS Messages EXTENDS E
# from Room to Message
 
CREATE CLASS PrivateMessages EXTENDS E
# from User to Message
# from Message to User
 
 
return $e
end
