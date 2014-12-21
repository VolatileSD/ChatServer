package chatserver.db.entity;

public abstract class AbstractEntity {

   private final String rid;

   public AbstractEntity(String rid) {
      this.rid = rid;
   }

   public String getRid() {
      return rid;
   }
}
