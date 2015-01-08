package chatserver.db;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrientDatabase {

   private static final Logger logger = Logger.getLogger(OrientDatabase.class.getName());

   private final String user;
   private final String password;
   private final ODatabaseDocumentTx db;

   public OrientDatabase(String database, String user, String password) {
      this.user = user;
      this.password = password;
      this.db = new ODatabaseDocumentTx(database);
   }

   public ODatabaseDocumentTx getDB() {
      open();
      return db;
   }

   public void open() {
      if (db != null && db.isClosed()) {
         db.open(user, password);
      }
   }

   public void close() {
      if (db != null) {
         db.close();
      }
   }

   public void deleteVertex(String rid) {
      execute("DELETE VERTEX " + rid);
   }

   public void createEdge(String edgeClass, String from, String to) {
      createOrDeleteEdge("CREATE", edgeClass, from, to);
   }

   public void deleteEdge(String edgeClass, String from, String to) {
      createOrDeleteEdge("DELETE", edgeClass, from, to);
   }

   private void createOrDeleteEdge(String whatToDo, String edgeClass, String from, String to) {
      execute(whatToDo + " EDGE " + edgeClass + " FROM " + from + " TO " + to);
   }

   public Object execute(String command) {
      logger.log(Level.INFO, command);
      return getDB().command(new OCommandSQL(command)).execute();
   }

   public List<ODocument> executeSynchQuery(String query) {
      logger.log(Level.INFO, query);
      return getDB().command(new OSQLSynchQuery(query)).execute();
   }
}