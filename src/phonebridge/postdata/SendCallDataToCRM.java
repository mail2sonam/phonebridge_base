/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridge.postdata;

import com.phonebridge.callmodel.CallsDb;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 *
 * @author bharath
 */
public class SendCallDataToCRM implements Runnable{
    private String dataToSend;
    private String url;
    private String callId;
    private String serverPrefix;
    private String mmYYYY;
    
    public SendCallDataToCRM(String dataToSend,String url,String callId,String serverPrefix,String mmYYYY){
        this.dataToSend = dataToSend;
        this.url = url;
        this.callId = callId;
        this.serverPrefix = serverPrefix;
        this.mmYYYY = mmYYYY;
    }
    
    private void curlData(String url,String dataToSend,String callId,String serverPrefix,String mmYYYY){
        BufferedReader reader = null;
        try{
            dataToSend = URLEncoder.encode(dataToSend ,"UTF-8");
            url = url.replace("{callData}", dataToSend);
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            System.out.println("RESPONSE OF CALL DATA TO CRM "+con.getResponseCode());
            String status = null;
            String responseCode = String.valueOf(con.getResponseCode());
            System.out.println("CAME AFTER CONVERSION");
            switch(responseCode){
                case "200":
                    status = "SUCCESS WITH RESPONSE CODE "+responseCode;
                    break;
                default:
                    status = "FAILED WITH RESPONSE CODE "+responseCode+" AND MESSAGE "+con.getResponseMessage();
                    break;
            }
            con.disconnect();
            new CallsDb().updateCurlStatus(status, callId, serverPrefix, mmYYYY);
        }
        catch(Exception ex){
            System.out.println("ERROR IN SENDING CALL DATA TO CRM "+ex.getMessage());
        }
    }

    @Override
    public void run() {
        this.curlData(url, dataToSend,callId, serverPrefix, mmYYYY);
    }
}
