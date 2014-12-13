package chatserver.rest.entities;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.ResponseHandler;


/*
 * Admin client : resquests to the REST API
 */
public class Admin {

   public final void listRoomsRequest() throws Exception {
      CloseableHttpClient httpclient = HttpClients.createDefault();
      try {
         HttpGet httpget = new HttpGet("http://localhost:8080/rooms");

         System.out.println("Executing request " + httpget.getRequestLine());

         // Create a custom response handler
         ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            public String handleResponse(
                    final HttpResponse response) throws ClientProtocolException, IOException {
               int status = response.getStatusLine().getStatusCode();
               if (status >= 200 && status < 300) {
                  HttpEntity entity = response.getEntity();
                  return entity != null ? EntityUtils.toString(entity) : null;
               } else {
                  throw new ClientProtocolException("Unexpected response status: " + status);
               }
            }
         };
         String responseBody = httpclient.execute(httpget, responseHandler);
         System.out.println("----------------------------------------");
         System.out.println(responseBody);
      } finally {
         httpclient.close();
      }
   }

   public void addRoomRequest(String roomname) throws Exception {
      CloseableHttpClient httpclient = HttpClients.createDefault();
      try {
         HttpPut httpput = new HttpPut("http://localhost:8080/room/" + roomname);

         System.out.println("Executing request " + httpput.getRequestLine());

         // Create a custom response handler
         ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            public String handleResponse(
                    final HttpResponse response) throws ClientProtocolException, IOException {
               int status = response.getStatusLine().getStatusCode();
               if (status >= 200 && status < 300) {
                  HttpEntity entity = response.getEntity();
                  return entity != null ? EntityUtils.toString(entity) : null;
               } else {
                  throw new ClientProtocolException("Unexpected response status: " + status);
               }
            }
         };
         String responseBody = httpclient.execute(httpput, responseHandler);
         System.out.println("----------------------------------------");
         System.out.println(responseBody);
      } finally {
         httpclient.close();
      }

   }

}
