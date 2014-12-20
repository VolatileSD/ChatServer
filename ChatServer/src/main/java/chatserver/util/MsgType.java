package chatserver.util;

public enum MsgType {

   DATA, COMMAND, EOF, IOE, ENTER, LEAVE, LINE,
   ROOM_INFO, CREATE_ROOM, DELETE_ROOM, // REST
   OK, INVALID,
   CREATE, REMOVE, LOGIN, LOGIN_OK, LOGOUT,
   PRIVATE, INBOX,
   CHANGE_ROOM
}
