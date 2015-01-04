package chatclient;

import com.google.gson.Gson;
import common.representation.UserRepresentation;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import java.net.URI;
import org.apache.http.annotation.NotThreadSafe;

public class AdminSettings extends javax.swing.JFrame {

   private StringEntity entity;
   /**
    * Creates new form AdminSettings
    */
   public AdminSettings(UserRepresentation userCredentials) {
      initComponents();
      try {
         this.entity = new StringEntity(new Gson().toJson(userCredentials));
      } catch (UnsupportedEncodingException ex) {
         errorBox(ex.getMessage());
      }
      this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            dispose();
         }
      });
   }

   public void addRoomRequest(String roomname) throws Exception {
      CloseableHttpClient httpclient = HttpClients.createDefault();
      try {
         HttpPut httpput = new HttpPut("http://localhost:8080/room/" + roomname);
         httpput.setEntity(entity);
         httpput.setHeader("Content-type", "application/json");
         
         System.out.println("Executing request " + httpput.getRequestLine());

         // Create a custom response handler
         ResponseHandler<String> responseHandler = (final HttpResponse response) -> {
            int status = response.getStatusLine().getStatusCode();
            if (status == 201) {
               HttpEntity entity = response.getEntity();
               infoBox("Room successfully created.");
               return entity != null ? EntityUtils.toString(entity) : null;
            } else if (status == 409) {
               HttpEntity entity = response.getEntity();
               infoBox("Room name is already in use.");
               return entity != null ? EntityUtils.toString(entity) : null;
            } else if (status == 401) {
               HttpEntity entity = response.getEntity();
               infoBox("Wrong password.");
               return entity != null ? EntityUtils.toString(entity) : null;
            } else {
               errorBox("Unexpected response status: " + status);
               throw new ClientProtocolException("Unexpected response status: " + status);

            }
         };
         String responseBody = httpclient.execute(httpput, responseHandler);
         System.out.println("----------------------------------------");
         System.out.println(responseBody);
      } finally {
         httpclient.close();
      }

   }
   
   @NotThreadSafe
class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "DELETE";
    public String getMethod() { return METHOD_NAME; }

    public HttpDeleteWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }
    public HttpDeleteWithBody(final URI uri) {
        super();
        setURI(uri);
    }
    public HttpDeleteWithBody() { super(); }
}

   public void deleteRoomRequest(String roomname) throws Exception {
      CloseableHttpClient httpclient = HttpClients.createDefault();
      try {
         HttpDeleteWithBody httpput = new HttpDeleteWithBody("http://localhost:8080/room/" + roomname);
         httpput.setEntity(entity);
         httpput.setHeader("Content-type", "application/json");
         System.out.println("Executing request " + httpput.getRequestLine());

         // Create a custom response handler
         ResponseHandler<String> responseHandler = (final HttpResponse response) -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
               HttpEntity entity = response.getEntity();
               infoBox("Room successfully deleted.");
               return entity != null ? EntityUtils.toString(entity) : null;
            } else {
               errorBox("Unexpected response status: " + status);
               throw new ClientProtocolException("Unexpected response status: " + status);
            }
         };
         String responseBody = httpclient.execute(httpput, responseHandler);
         System.out.println("----------------------------------------");
         System.out.println(responseBody);
      } finally {
         httpclient.close();
      }

   }

   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        addRoomName = new javax.swing.JTextField();
        addRoomBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        deleteRoomBtn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        deleteRoomName = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Admin Options"));

        jLabel3.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel3.setText("Add Room");

        addRoomName.setText("room name");

        addRoomBtn.setText("Add");
        addRoomBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRoomBtnActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));

        deleteRoomBtn.setText("Delete");
        deleteRoomBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteRoomBtnActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Delete Room");

        deleteRoomName.setText("room name");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(30, 30, 30)
                .addComponent(deleteRoomName, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(59, 59, 59)
                .addComponent(deleteRoomBtn)
                .addContainerGap(60, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteRoomName, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteRoomBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(44, 44, 44)
                .addComponent(addRoomName, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addRoomBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63))
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addRoomName, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addRoomBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(163, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addRoomBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRoomBtnActionPerformed
       if (addRoomName.getText().matches("^[^\\d\\s]+$")) {
          try {
             addRoomRequest(addRoomName.getText());
          } catch (Exception ex) {
             Logger.getLogger(AdminSettings.class.getName()).log(Level.SEVERE, null, ex);
          }
       } else {
          errorBox("No spaces or numbers allowed");
       }
    }//GEN-LAST:event_addRoomBtnActionPerformed

    private void deleteRoomBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteRoomBtnActionPerformed
       if (deleteRoomName.getText().matches("^[^\\d\\s]+$")) {
          try {
             deleteRoomRequest(deleteRoomName.getText());
          } catch (Exception ex) {
             Logger.getLogger(AdminSettings.class.getName()).log(Level.SEVERE, null, ex);
          }
       } else {
          errorBox("No spaces or numbers allowed");
       }
    }//GEN-LAST:event_deleteRoomBtnActionPerformed

   private static void errorBox(String errorMessage) {
      JOptionPane.showMessageDialog(null, errorMessage, "Something Went Wrong", JOptionPane.ERROR_MESSAGE);
   }

   private static void infoBox(String infoMessage) {
      JOptionPane.showMessageDialog(null, infoMessage, "Some info for you", JOptionPane.INFORMATION_MESSAGE);
   }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addRoomBtn;
    private javax.swing.JTextField addRoomName;
    private javax.swing.JButton deleteRoomBtn;
    private javax.swing.JTextField deleteRoomName;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
