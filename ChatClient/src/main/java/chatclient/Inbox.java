package chatclient;

import com.google.gson.Gson;
import common.representation.MessageRepresentation;
import common.representation.TalkRepresentation;
import common.representation.UsersRepresentation;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Inbox extends JFrame {

   private SocketChannel socket;
   private String selectedUser;

   private SimpleAttributeSet recipient;
   private SimpleAttributeSet date;

   /**
    * Creates new form Inbox
    */
   public Inbox(SocketChannel socket) {
      initComponents();
      initStyle();
      alwaysScrollDown();
      onClose();
      this.selectedUser = "";
      privateTxt.setText("");
      this.socket = socket;
      say(":iu\n");
   }

   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jPopupMenu1 = new javax.swing.JPopupMenu();
      jDialog1 = new javax.swing.JDialog();
      jPanel1 = new javax.swing.JPanel();
      privateTxt = new javax.swing.JTextField();
      jScrollPane2 = new javax.swing.JScrollPane();
      inboxUsersList = new javax.swing.JList();
      sendToTxt = new javax.swing.JTextField();
      jScrollPane1 = new javax.swing.JScrollPane();
      messageTxt = new javax.swing.JTextPane();

      javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
      jDialog1.getContentPane().setLayout(jDialog1Layout);
      jDialog1Layout.setHorizontalGroup(
         jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 400, Short.MAX_VALUE)
      );
      jDialog1Layout.setVerticalGroup(
         jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGap(0, 300, Short.MAX_VALUE)
      );

      setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
      setTitle("Private Messaging");
      setBackground(new java.awt.Color(0, 204, 204));
      setFocusable(false);
      setMinimumSize(new java.awt.Dimension(600, 410));

      jPanel1.setBackground(new java.awt.Color(0, 204, 153));

      privateTxt.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            privateTxtActionPerformed(evt);
         }
      });

      inboxUsersList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
      jScrollPane2.setViewportView(inboxUsersList);

      messageTxt.setEditable(false);
      jScrollPane1.setViewportView(messageTxt);

      javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
               .addComponent(sendToTxt)
               .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(jPanel1Layout.createSequentialGroup()
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addComponent(jScrollPane1))
               .addGroup(jPanel1Layout.createSequentialGroup()
                  .addGap(10, 10, 10)
                  .addComponent(privateTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)))
            .addContainerGap())
      );
      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
               .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
               .addComponent(jScrollPane1))
            .addGap(18, 18, 18)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(privateTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(sendToTxt))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      );

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, Short.MAX_VALUE))
      );

      pack();
      setLocationRelativeTo(null);
   }// </editor-fold>//GEN-END:initComponents

    private void privateTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_privateTxtActionPerformed
       String message = privateTxt.getText();
       if (!"".equals(selectedUser) && !"".equals(message)) {
          say(new StringBuilder(":private ").append(selectedUser).append(" ").append(message).append("\n").toString());
          privateTxt.setText("");
          say(new StringBuilder(":talk ").append(selectedUser).append("\n").toString());
          // do this more efficiently
       }
    }//GEN-LAST:event_privateTxtActionPerformed

   protected void updateInboxUsers(String users) {
      UsersRepresentation rr = new Gson().fromJson(users, UsersRepresentation.class);
      DefaultListModel dlm = new DefaultListModel();
      for (String user : rr.getUsers()) {
         dlm.addElement(user);
      }

      clearList();
      inboxUsersList.setModel(dlm);
      addInboxUsersOneClickAction();
   }

   protected void updateTalk(String talk) {
      clearMessageTxt();
      StyledDocument doc = messageTxt.getStyledDocument();

      TalkRepresentation tr = new Gson().fromJson(talk, TalkRepresentation.class);
      try {
         for (MessageRepresentation m : tr.getMessages()) {
            doc.insertString(doc.getLength(), m.getFrom() + "\n", recipient);
            doc.insertString(doc.getLength(), m.getDate().toString() + "\n", date);
            doc.insertString(doc.getLength(), m.getText() + "\n\n", null);
         }
      } catch (Exception e) {
         errorBox(e.getMessage());
      }
   }

   private void addInboxUsersOneClickAction() {
      inboxUsersList.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent evt) {
            if (evt.getClickCount() == 1) {
               selectedUser = (String) inboxUsersList.getSelectedValue();
               say(new StringBuilder(":talk ").append(selectedUser).append("\n").toString());
            }
         }
      });
   }

   private void clearList() {
      inboxUsersList = new javax.swing.JList();
      jScrollPane2.setViewportView(inboxUsersList);
   }

   private void clearMessageTxt() {
      messageTxt = new JTextPane();
      messageTxt.setEditable(false);
      jScrollPane1.setViewportView(messageTxt);
   }

   private void initStyle() {
      recipient = new SimpleAttributeSet();
      StyleConstants.setBold(recipient, true);
      StyleConstants.setFontSize(recipient, 13);

      date = new SimpleAttributeSet();
      StyleConstants.setBold(date, true);
      StyleConstants.setFontSize(date, 11);
      StyleConstants.setForeground(date, new Color(150, 150, 150));
   }

   private void alwaysScrollDown() {
      DefaultCaret caret = (DefaultCaret) messageTxt.getCaret();
      caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
   }

   private void onClose() {
      this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            dispose();
         }
      });
   }

   private static void errorBox(String infoMessage) {
      JOptionPane.showMessageDialog(null, infoMessage, "Something Went Wrong", JOptionPane.ERROR_MESSAGE);
   }

   private void say(byte[] whatToSay) {
      try {
         socket.write(ByteBuffer.wrap(whatToSay));
      } catch (IOException ex) {
         errorBox(ex.getMessage());
      }
   }

   private void say(String whatToSay) {
      say(whatToSay.getBytes());
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JList inboxUsersList;
   private javax.swing.JDialog jDialog1;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JPopupMenu jPopupMenu1;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JScrollPane jScrollPane2;
   private javax.swing.JTextPane messageTxt;
   private javax.swing.JTextField privateTxt;
   private javax.swing.JTextField sendToTxt;
   // End of variables declaration//GEN-END:variables

}
