/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridge.postdata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import phonebridge.util.util;
import processdbevents.db.ProcessForReportsDB;

/**
 *
 * @author venky
 */
public class CurlDataForAPI {
    private String extension;
    private String curlFor;
    private String url;
    private HashMap<String,String> urlParams;
    private String callID;
    private String mobileNumber;
    private String callStatus;
    private String callDirection;
    private String source;
    private String destination;
    private String channel;
    private String destChannel;
    private String destContext;
    private String uniqueID;
    private Date startTime;
    private Date answerTime;
    private Date endTime;
    private String userField;
    private String disposition;
    private int duration;
    private int billableSeconds;
    private String hangUpCauseCode;
    private String recordingFileName;
    private String transferFrom;
    private String transferTo;
    
    public CurlDataForAPI(String url,String curlFor,String extension,String callID,String mobileNumber,String callStatus,
            String callDirection,String source,String destination,String channel,String destChannel,String destContext,
            String uniqueID,Date startTime,Date answerTime,Date endTime,String userField,String disposition,
            int duration,int billableSeconds,String hangUpCauseCode,String recordingFileName,String transferFrom,
            String transferTo){
        this.url = url;
        this.curlFor = curlFor;
        this.extension = extension;
        this.callID = callID;
        this.mobileNumber = mobileNumber;
        this.callStatus = callStatus;
        this.callDirection = callDirection;
        this.source = source;
        this.destination = destination;
        this.channel = channel;
        this.destChannel = destChannel;
        this.destContext = destContext;
        this.uniqueID = uniqueID;
        this.startTime = startTime;
        this.answerTime = answerTime;
        this.endTime = endTime;
        this.userField = userField;
        this.disposition = disposition;
        this.duration = duration;
        this.billableSeconds = billableSeconds;
        this.hangUpCauseCode = hangUpCauseCode;
        this.recordingFileName = recordingFileName;
        this.transferFrom = transferFrom;
        this.transferTo = transferTo;
    }
    
    private void curlData(String myurl,HashMap<String,String> urlParams,String source){
        try
        {
           String response = getResponse(myurl,urlParams);
           System.out.println(response);
           new ProcessForReportsDB().insertCurlDetails(url,urlParams,source,response);
           new ProcessForReportsDB().removeCurlDetails();
            System.out.println("URL is:"+url);
        }
        catch (IOException e)
        {
           e.printStackTrace();
        }
    }
   
   /*
    * Retrieve response using commons HttpClient API.
    */
    private static String getResponse(String url,HashMap<String,String> map) throws IOException {
        PostMethod post = new PostMethod(url);
        for(Map.Entry<String,String> e:map.entrySet())
        {
            if(e.getValue()==null)
                post.addParameter(e.getKey(), "");
            else
                post.addParameter(e.getKey(), e.getValue());
        }
        new HttpClient().executeMethod(post);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;
        byte[] byteArray = new byte[1024];
        int count = 0 ;
        while((count = post.getResponseBodyAsStream().read(byteArray, 0, byteArray.length)) > 0)
        {
           outputStream.write(byteArray, 0, count) ;
        }
        return new String(outputStream.toByteArray(), "UTF-8");
    }
    
    public void curl(){
        switch(curlFor){
            case "apipopup":
                if(this.url==null ||this.url.length()<1)
                    return;
                urlParams = new HashMap<>();
                try{
                    urlParams.put("CallId", callID);
                }
                catch(Exception e){}
                try{
                    urlParams.put("PhoneNumber", mobileNumber);
                }catch(Exception e){}
                try{
                    urlParams.put("Extension", extension);
                }catch(Exception e){}
                try{
                    urlParams.put("CallStatus", callStatus);
                }catch(Exception e){}
                try{
                    urlParams.put("CallDirection", callDirection);
                }catch(Exception e){}
                /*this.urlParams = "CallId="+callID+"&PhoneNumber="+mobileNumber+"&Extension="+extension+"&CallStatus="
                        +callStatus+"&CallDirection="+callDirection;*/
                curlData(url,urlParams,"apiPopup");
                break;
            case "hangup":
                if(this.url==null || this.url.length()<1)
                    return;
                urlParams = new HashMap<>();
                try{
                    urlParams.put("CallId", callID);
                }catch(Exception e){}
                try{
                    urlParams.put("PhoneNumber", mobileNumber);
                }catch(Exception e){}
                try{
                    urlParams.put("Extension", extension);
                }catch(Exception e){}
                try{
                    urlParams.put("CallStatus", callStatus);
                }catch(Exception e){}
                try{
                    urlParams.put("CallDirection", callDirection);
                }catch(Exception e){}
                try{
                    urlParams.put("HangUpCause", hangUpCauseCode);
                }catch(Exception e){}
                /*this.urlParams = "CallId="+callID+"&PhoneNumber="+mobileNumber+"&Extension="+extension+"&CallStatus="
                        +callStatus+"&CallDirection="+callDirection+"&HangUpCause="+hangUpCauseCode;*/
                curlData(url, urlParams,"apiHangUp");
                break;
            case "cdr":
                if(this.url==null || this.url.length()<1)
                    return;
                urlParams = new HashMap<>();
                try{
                    urlParams.put("CallId", callID);
                }catch(Exception e){}
                try{
                    urlParams.put("source", source);
                }catch(Exception e){}
                try{
                    urlParams.put("destination", destination); 
                }catch(Exception e){}
                try{
                    urlParams.put("channel", channel); 
                }catch(Exception e){}
                try{
                    urlParams.put("destChannel", destChannel); 
                }catch(Exception e){}
                try{
                    urlParams.put("destContext", destContext); 
                }catch(Exception e){}
                try{
                    urlParams.put("uniqueId", uniqueID); 
                }catch(Exception e){}
                try{
                    
                    String starttime = this.generateDateInYYYYMMDDHHMMSSFormat(startTime);
                    System.out.println("start Time:"+starttime);
                    urlParams.put("callStartTime", starttime); 
                }catch(Exception e){}
                try{
                    String answertime = this.generateDateInYYYYMMDDHHMMSSFormat(answerTime);
                    System.out.println("answer Time:"+answertime);
                    urlParams.put("answerTime", answertime); 
                }catch(Exception e){}
                try{
                    String endtime = this.generateDateInYYYYMMDDHHMMSSFormat(endTime);
                    System.out.println("end Time:"+endtime);
                    urlParams.put("callEndTime", endtime); 
                }catch(Exception e){}
                try{
                    urlParams.put("disposition", disposition); 
                }catch(Exception e){}
                try{
                    urlParams.put("duration", String.valueOf(duration)); 
                }catch(Exception e){}
                try{
                    urlParams.put("billableSeconds", String.valueOf(billableSeconds)); 
                }catch(Exception e){}
                
                /*this.urlParams = "CallId="+URLEncoder.encode(callID)+"&source="+URLEncoder.encode(source)
                        +"&destination="+URLEncoder.encode(destination)+"&channel="+URLEncoder.encode(channel)
                        +"&destChannel="+URLEncoder.encode(destChannel)+"&destContext="+URLEncoder.encode(destContext)
                        +"&uniqueId="+URLEncoder.encode(uniqueID)+"&callStartTime="+URLEncoder.encode(startTime.toString())
                        +"&answerTime="+URLEncoder.encode(answerTime.toString())+"&callEndTime="+URLEncoder.encode(endTime.toString())
                        +"&disposition="+URLEncoder.encode(disposition)+"&duration="+URLEncoder.encode(String.valueOf(duration))
                        +"&billableSeconds="+URLEncoder.encode(String.valueOf(billableSeconds));
                //System.out.println("encded params:"+encodedParameters);*/
                curlData(url, urlParams,"apiCdr");
                break;
            case "cdrrecording":
                if(this.url==null ||this.url.length()<1 || recordingFileName.length()<1 || recordingFileName==null)
                    return;
                urlParams = new HashMap<>();
                try{
                    urlParams.put("CallId", callID);
                }catch(Exception e){}
                try{
                    urlParams.put("PhoneNumber", mobileNumber);
                }catch(Exception e){}
                try{
                    urlParams.put("Extension", extension);
                }catch(Exception e){}
                try{
                    urlParams.put("CallStatus", callStatus);
                }catch(Exception e){}
                try{
                    urlParams.put("CallDirection", callDirection);
                }catch(Exception e){}
                try{
                    urlParams.put("Recording", recordingFileName);
                }catch(Exception e){}
                curlData(url, urlParams, "apiRecording");
                break;
            case "transfer":
                if(this.url==null || this.url.length()<1)
                    return;
                urlParams = new HashMap<>();
                try{
                    urlParams.put("CallId", callID);
                }catch(Exception e){}
                try{
                    urlParams.put("TransferFrom", transferFrom);
                }catch(Exception e){}
                try{
                    urlParams.put("TransferTo", transferTo);
                }catch(Exception e){}
                try{
                    urlParams.put("CallStatus", callStatus);
                }catch(Exception e){}
                curlData(url, urlParams, "apiTransfer");
                break;
        }
    }
    
    public String generateDateInYYYYMMDDHHMMSSFormat(Date dateToConvert){
        String generatedDateString = null;
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            generatedDateString = formatter.format(dateToConvert);
            //dt = formatter.parse(generatedDateString);
        }catch(Exception e){
            e.getMessage();
        }
        return generatedDateString;
    }
}
