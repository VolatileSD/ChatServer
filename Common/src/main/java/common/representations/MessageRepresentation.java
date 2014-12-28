package common.representations;

import java.util.Date;

public class MessageRepresentation {
   
   private String from;
   private String text;
   private Date date;

   public MessageRepresentation(String from, String text, Date date) {
      this.from = from;
      this.text = text;
      this.date = date;
   }

   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public String getFrom() {
      return from;
   }

   public void setFrom(String from) {
      this.from = from;
   }

   public Date getDate() {
      return date;
   }

   public void setDate(Date date) {
      this.date = date;
   }
   
   @Override
   public String toString(){
      StringBuilder sb = new StringBuilder(date.toString()).append("\n");
      sb.append(from).append("\n");
      sb.append(text).append("\n\n");

      return sb.toString();
   }
}
