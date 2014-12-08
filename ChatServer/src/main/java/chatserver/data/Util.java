package chatserver.data;

public class Util {

  public CommandType getCommandType(String cmd){
    /*
    * Filters commands for the simple client
    */
    CommandType type = UNKNOWN;

    if (cmd ==":cr") type = CHANGE_ROOM;
    else if (cmd==":h" || cmd==":help") type = HELP;

    return type;
  }
}
