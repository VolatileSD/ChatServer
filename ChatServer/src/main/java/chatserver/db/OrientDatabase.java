package chatserver.db;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import java.util.ArrayList;
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
      StringBuilder sb = new StringBuilder(whatToDo).append(" EDGE ").append(edgeClass);
      sb.append(" FROM ").append(from).append(" TO ").append(to);
      logger.log(Level.INFO, sb.toString());
      execute(sb.toString());
   }

   public Object execute(String command) {
      OCommandSQL oCommand = new OCommandSQL(command);
      return getDB().command(oCommand).execute();
   }

   public List<ODocument> executeSynchQuery(String query) {
      return getDB().command(new OSQLSynchQuery(query)).execute();
   }

   public List<ODocument> executeSynchQuery(String query, Object params) {
      return getDB().command(new OSQLSynchQuery(query)).execute(params);
   }

   private static List<Long> getEntries(String logEntry) {
      if (logEntry == null) {
         return null;
      }

      List<Long> entries = new ArrayList<>();
      String[] listEntries = logEntry.split("\\s*,\\s*");

      if (listEntries.length == 0) {
         return null;
      }

      for (String entry : listEntries) {
         entries.add(Long.valueOf(entry));
      }

      return entries.isEmpty() ? null : entries;
   }
}
