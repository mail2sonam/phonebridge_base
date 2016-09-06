/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridge.postdata;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
/**
 *
 * @author bharath
 */
public class CurlDataToURL{
    private String extension;
    private String message;
    private String curlFor;
    private String url;
    private String urlParams;
    private String callID;
    private String mobileNumber;
    private String callStatus;
    private String recordingFileName;
    
    public CurlDataToURL(String url,String extension,String message,String curlFor,String callID,String mobileNumber,
            String callStatus,String recordingFileName){
        this.url = url;
        this.extension = extension;
        this.message = message;
        this.curlFor = curlFor;
        this.callID = callID;
        this.mobileNumber = mobileNumber;
        this.callStatus = callStatus;
        this.recordingFileName = recordingFileName;
    }
    
    /*
    private void curlData(String url,String urlParams){
        BufferedReader reader = null;
        try{
            System.out.println("URL IS "+url+" PARAMS PASSED "+urlParams);
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setDoOutput(true);
            DataOutputStream output = new DataOutputStream(con.getOutputStream());
            output.writeBytes(urlParams);
            System.out.println("RESP IS "+con.getResponseCode());
            //OutputStream os = con.getOutputStream();
        }
        catch(Exception ex){
            LogClass.logMsg("CurlDataToURL", "curlData", ex.getMessage());
        }
    }
    
    
       public static void main(String[] args){*/
    /*
        Changed By Bharath on 23/04/2016
        Changing return type to string
        Changed because require the JSON response which will be sent for CRMDialerCampaign
    */
    private String curlData(String myurl,String urlParams,String source){
        /*
            Changed By Bharath on 23/04/2016
            Changing concatination of URL Params
            Changed because CRMDialerCampaign is using path variable
            Reason for using path variable is added in CRMDialerCampaignSr.java
        */
        String url = myurl;
        if(urlParams!=null && urlParams.length()>0)
            url = myurl+ "?"+urlParams;
        String response = null;
        System.out.println("URL BEING CURLED "+url);
        try {
            response = getResponse(url);
            System.out.println(response);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
   
    /*
     * Retrieve response using commons HttpClient API.
     */
    private static String getResponse(String url) throws IOException {
        GetMethod get = new GetMethod(url);
        new HttpClient().executeMethod(get);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] byteArray = new byte[1024];
        int count = 0 ;
        while((count = get.getResponseBodyAsStream().read(byteArray, 0, byteArray.length)) > 0) {
            outputStream.write(byteArray, 0, count) ;
        }
        return new String(outputStream.toByteArray(), "UTF-8");
    }
    
    private static String getResponseForRest(String urlStr) {
        String totalOutput="";        
        try {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));

		String outputLine;
		while ((outputLine = br.readLine()) != null) {
			totalOutput=totalOutput.concat(outputLine);
		}

		conn.disconnect();

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }
        return totalOutput;
    }
    
    

//    @Override
    public String run() {
        String response = null;
        switch(curlFor){
            case "popup":
                //this.url = "http://localhost:8082/phonebridge_cti/faces/postIntoWS.xhtml";
                if(this.url==null ||this.url.length()<1)
                    return null;
                this.urlParams = "extension="+extension+"&message="+message;
                curlData(url,urlParams,"Dialer");
                break;
            case "crmpopup":
                if(this.url==null ||this.url.length()<1)
                    return null;
                this.urlParams = "callId="+callID+"&phoneNumber="+mobileNumber+"&extension="+extension+"&callStatus="+callStatus;
                curlData(url,urlParams,"CRM Popup");
                break;
            case "cdrrecording":
                if(this.url==null ||this.url.length()<1 || recordingFileName.length()<1 || recordingFileName==null)
                    return null;
                this.urlParams = "callId="+callID+"&recording="+recordingFileName;
                curlData(url,urlParams,"CDR Recording");
                break;
            case "livemonitoring":
                /*
                this.url = "http://localhost:8082/sheenlac_web/postIntoWS.xhtml";
                this.urlParams = "extension="+extension+"&message="+message+"&responseFor=LIVEMONITOR";
                //curlData(url,urlParams);*/
                break;
            case "crmdialercampaign":
                response = getResponseForRest(url);
                break;
        }
        return response;
    }
}
