package chatserver.util;

public enum MsgType {

   DATA, COMMAND, EOF, IOE, ENTER, LEAVE, LINE,
   ROOM_INFO, CREATE_ROOM, DELETE_ROOM,
   OK, INVALID,
   CREATE, REMOVE, LOGIN,
   CHANGE_ROOM
}
