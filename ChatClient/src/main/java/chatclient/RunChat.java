package chatclient;

import common.representation.RoomRepresentation;
import common.representation.RoomsRepresentation;
import com.google.gson.Gson;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.ResponseHandler;

public class RunChat extends JFrame {

   private final int MAXLEN = 4096; // problem when receiving json with more than 4096 char
   private SocketChannel socket;
   private Inbox inbox;
   private Map<String, SimpleAttributeSet> usernames;
   private Random random;
   private SimpleAttributeSet bold;
   private SimpleAttributeSet biggerBold;

   /**
    * Creates new form RunChat
    */
   public RunChat(SocketChannel socket) {
      initComponents();
      initStyle();
      alwaysScrollDown();
      this.socket = socket;
      this.inbox = null;
      this.usernames = new HashMap();
      this.random = new Random();
      new LineReader().start();
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
        jScrollPane3 = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        listRoomsBtn = new javax.swing.JButton();
        listUsersBtn = new javax.swing.JButton();
        inboxBtn = new javax.swing.JButton();
        logoutBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        sendTxt = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatTxt = new javax.swing.JTextPane();

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
        setTitle("Chat Conversation");
        setBackground(new java.awt.Color(0, 153, 204));
        setFocusable(false);
        setMinimumSize(new java.awt.Dimension(600, 410));

        jPanel1.setBackground(new java.awt.Color(153, 204, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Settings", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        list.setEnabled(false);
        jScrollPane3.setViewportView(list);

        listRoomsBtn.setBackground(new java.awt.Color(0, 0, 0));
        listRoomsBtn.setForeground(new java.awt.Color(255, 255, 255));
        listRoomsBtn.setText("List Rooms");
        listRoomsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listRoomsBtnActionPerformed(evt);
            }
        });

        listUsersBtn.setBackground(new java.awt.Color(0, 0, 0));
        listUsersBtn.setForeground(new java.awt.Color(255, 255, 255));
        listUsersBtn.setText("List users in selected room");
        listUsersBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listUsersBtnActionPerformed(evt);
            }
        });

        inboxBtn.setBackground(new java.awt.Color(0, 0, 0));
        inboxBtn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        inboxBtn.setForeground(new java.awt.Color(255, 255, 255));
        inboxBtn.setText("Inbox");
        inboxBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inboxBtnActionPerformed(evt);
            }
        });

        logoutBtn.setBackground(new java.awt.Color(0, 0, 0));
        logoutBtn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        logoutBtn.setForeground(new java.awt.Color(255, 255, 255));
        logoutBtn.setText("Logout");
        logoutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(inboxBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(logoutBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane3)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(listRoomsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                                .addComponent(listUsersBtn)))))
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(listUsersBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(listRoomsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(55, 55, 55)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inboxBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logoutBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(112, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(0, 153, 153));

        sendTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendTxtActionPerformed(evt);
            }
        });

        chatTxt.setEditable(false);
        jScrollPane1.setViewportView(chatTxt);
        chatTxt.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addComponent(sendTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addGap(18, 18, 18)
                .addComponent(sendTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

   private void sendTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendTxtActionPerformed
      String text = sendTxt.getText();
      if (text.startsWith(":inbox")) {
         infoBox("There's a button called inbox. Try to use it!");
      } else {
         say(new StringBuilder(text).append("\n").toString());
      }
      sendTxt.setText("");
   }//GEN-LAST:event_sendTxtActionPerformed

   private void listRoomsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listRoomsBtnActionPerformed
      String responseBody = httpGet("rooms");

      if (responseBody != null) {
         RoomsRepresentation rr = new Gson().fromJson(responseBody, RoomsRepresentation.class);
         DefaultListModel dlm = new DefaultListModel();
         for (String room : rr.getRooms()) {
            dlm.addElement(room);
         }
         clearList();
         list.setModel(dlm);

         addRoomsDoubleClickAction();

      } else {
         errorBox("");
      }
   }//GEN-LAST:event_listRoomsBtnActionPerformed

   private void listUsersBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listUsersBtnActionPerformed
      String roomName = (String) list.getSelectedValue();
      if (roomName != null && !"".equals(roomName)) {
         String responseBody = httpGet("room/" + roomName);
         if (responseBody != null) {
            RoomRepresentation rr = new Gson().fromJson(responseBody, RoomRepresentation.class);
            DefaultListModel dlm = new DefaultListModel();
            for (String user : rr.getUsers()) {
               dlm.addElement(user);
            }
            clearList();
            list.setModel(dlm);
         } else {
            errorBox(new StringBuilder("Room ").append(roomName).append(" does not exist!").toString());
         }

      }
   }//GEN-LAST:event_listUsersBtnActionPerformed

    private void logoutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutBtnActionPerformed
       say(":logout\n");
       this.dispose();
       new RunLogin().setVisible(true);
    }//GEN-LAST:event_logoutBtnActionPerformed

   private void inboxBtnActionPerformed(java.awt.event.ActionEvent evt) {
      this.inbox = new Inbox(socket);
      this.inbox.setVisible(true);
   }

   private String httpGet(String path) {
      String result = null;
      CloseableHttpClient httpclient = HttpClients.createDefault();

      try {
         try {
            HttpGet httpget = new HttpGet("http://localhost:8080/" + path);
            ResponseHandler<String> responseHandler = (final HttpResponse response) -> {
               int status = response.getStatusLine().getStatusCode();
               String responseBody = null;
               if (status == 200) {
                  HttpEntity entity = response.getEntity();
                  responseBody = entity != null ? EntityUtils.toString(entity) : null;
               }
               return responseBody;
            };

            result = httpclient.execute(httpget, responseHandler);

         } finally {
            httpclient.close();
         }
      } catch (IOException ex) {
         errorBox(ex.getMessage());
      }

      return result;
   }

   private void clearList() {
      list = new javax.swing.JList();
      jScrollPane3.setViewportView(list);
   }

   private void addRoomsDoubleClickAction() {
      list.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent evt) {
            if (evt.getClickCount() == 2) {
               say(new StringBuilder(":cr ").append(list.getSelectedValue()).append("\n").toString());
            }
         }
      });
   }

   private class LineReader extends Thread {

      private final ByteBuffer in = ByteBuffer.allocate(MAXLEN);
      private final ByteBuffer out = ByteBuffer.allocate(MAXLEN);

      @Override
      public void run() {
         boolean eof = false;
         byte b = 0;
         try {
            for (;;) {
               if (socket.read(in) <= 0) {
                  eof = true;
               }
               in.flip();
               while (in.hasRemaining()) {
                  b = in.get();
                  out.put(b);
                  if (b == '\n') {
                     break;
                  }
               }
               if (eof || b == '\n') {
                  out.flip();
                  if (out.remaining() > 0) {
                     byte[] ba = new byte[out.remaining()];
                     out.get(ba);
                     out.clear();
                     String text = new String(ba);
                     if (text.startsWith(":iu:") && inbox != null) {
                        inbox.updateInboxUsers(text.substring(4));
                     } else if (text.startsWith(":tk:") && inbox != null) {
                        inbox.updateTalk(text.substring(4));
                     } else {
                        parseMessage(new String(ba));
                     }
                  }
               }
               if (eof && !in.hasRemaining()) {
                  break;
               }
               in.compact();
            }
            errorBox("");
         } catch (IOException | BadLocationException e) {
            errorBox(e.getMessage());
         }
      }
   }

   public void parseMessage(String message) throws BadLocationException {
      // for this to be valid usernames can only contain :  [a-zA-Z_0-9]
      // or we change the regex
      StyledDocument doc = chatTxt.getStyledDocument();
      if (message.matches("^@\\w+:.*\n")) {
         int index = message.indexOf(":");
         String username = message.substring(1, index);
         String text = message.substring(index + 2);
         if (!usernames.containsKey(username)) {
            createNewColorForThisUser(username);
         }
         // could save @user: instead of user
         doc.insertString(doc.getLength(), new StringBuilder("@").append(username).append(": ").toString(), usernames.get(username));
         doc.insertString(doc.getLength(), text, null);
      } else if (message.startsWith("#User")) {
         doc.insertString(doc.getLength(), message.substring(6, message.length()), this.bold);
      } else if (message.startsWith("----")) {
         doc.insertString(doc.getLength(), new StringBuilder("\n\t").append(message.substring(7, message.length() - 7)).append("\n\n").toString(), this.biggerBold);
      } else {
         doc.insertString(doc.getLength(), message, null);
      }
   }

   private void createNewColorForThisUser(String username) {
      SimpleAttributeSet keyWord = new SimpleAttributeSet();
      random = new Random();
      Color color = new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200));
      StyleConstants.setForeground(keyWord, color);
      StyleConstants.setBold(keyWord, true);
      usernames.put(username, keyWord);
   }

   private void initStyle() {
      bold = new SimpleAttributeSet();
      StyleConstants.setBold(bold, true);

      biggerBold = new SimpleAttributeSet();
      StyleConstants.setBold(biggerBold, true);
      StyleConstants.setFontSize(biggerBold, 14);
   }

   private void alwaysScrollDown() {
      DefaultCaret caret = (DefaultCaret) chatTxt.getCaret();
      caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
   }

   private static void errorBox(String errorMessage) {
      JOptionPane.showMessageDialog(null, errorMessage, "Something Went Wrong", JOptionPane.ERROR_MESSAGE);
   }

   private static void infoBox(String infoMessage) {
      JOptionPane.showMessageDialog(null, infoMessage, "Some info for you", JOptionPane.INFORMATION_MESSAGE);
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
    private javax.swing.JTextPane chatTxt;
    private javax.swing.JButton inboxBtn;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList list;
    private javax.swing.JButton listRoomsBtn;
    private javax.swing.JButton listUsersBtn;
    private javax.swing.JButton logoutBtn;
    private javax.swing.JTextField sendTxt;
    // End of variables declaration//GEN-END:variables
}
