package chatserver.data;

public class Util {

  public CommandType getCommandType(String cmd){
    /*
    * Filters commands for the simple client
    */
    CommandType type = CommandType.CHAUNKNOWN;

    if (cmd ==":cr") type = CommandType.CHANGE_ROOM;
    else if (cmd==":h" || cmd==":help") type = CommandType.HELP;

    return type;
  }
}
