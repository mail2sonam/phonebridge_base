/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridge.postdata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import phonebridge.util.LogClass;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author bharath
 */
public class GetClientIdForNumber{
    private String url;
    private String phoneNo;
    
    public GetClientIdForNumber(String phoneNo,String url){
        this.phoneNo = phoneNo;
        this.url = url;
    }
    
    private JsonNode curlData(String url,String phoneNo){
        JsonNode data = null;
        try{
            url = url.replace("{phoneNo}", phoneNo);
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            System.out.println("RESP IS "+con.getResponseCode());
            ObjectMapper mapper = new ObjectMapper();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String current;
            if((current = in.readLine()) != null) {
                data = mapper.readTree(current);
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CurlDataToURL", "curlData", ex.getMessage());
        }
        return data; 
    }

    //@Override
    public JsonNode run() {
        return this.curlData(url, phoneNo);
    }
}
