package phonebridge.originate;

import com.mongodb.MongoClient;
import com.phonebridgecti.dao.CtiDAO;
import com.phonebridgecti.db.CheckCampaignActiveDB;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import phonebridgelogger.model.Server;
import phonebridge.util.LogClass;

public class OriginateCall implements Runnable{
    private String lastReadLine;
    private List<String> lastReadBlock;
    private Socket socketConn;
    private PrintWriter socketWrite;
    private BufferedReader socketRead;

    Server serverDetails;
    String prefix;
    String mobileNumber;
    String extension;
    String typeOfDialer;
    MongoClient mongoConn;
    String mobileNumberToUse;
    String callContext;
    String callerID;
    boolean followMeExists;
    String extensionType;
    String followMeNumber;
    
    public OriginateCall(Server serverDetails,String prefix,String mobileNumber,String extension,String typeOfDialer,
        String callContext,String nextcallerID,boolean followMeExists,String extensionType,String followMeNumber,MongoClient mongoConn){
        this.serverDetails = serverDetails;
        this.prefix = prefix;
        this.mobileNumber = mobileNumber;
        this.callerID = nextcallerID;
        this.extension = extension;
        this.typeOfDialer = typeOfDialer;
        this.extensionType=extensionType;
        this.followMeNumber = followMeNumber;
        
        this.mongoConn = mongoConn;
        if(this.prefix==null || this.prefix.length()==0)
            this.mobileNumberToUse = this.mobileNumber;
        else
            this.mobileNumberToUse = this.prefix.concat(this.mobileNumber);
        this.callContext = callContext;
        this.followMeExists = followMeExists;
    }
    private void gracefulExit(){
        try {
            socketRead.close();
        } catch (IOException ex) {
            Logger.getLogger(OriginateCall.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Socket Read Close");
        try {
            socketWrite.close();
         } catch (Exception ex) {
            Logger.getLogger(OriginateCall.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Socket Write Close");
        try {
            socketConn.close();
        } catch (IOException ex) {
            Logger.getLogger(OriginateCall.class.getName()).log(Level.SEVERE, null, ex);
        }
        //close any existing mongo connection
    }

   
    
    private boolean connectSocket(){
        boolean socketConnected = false;
        try{
            if(new CheckCampaignActiveDB().checkUserAvailablityForDialer(serverDetails.getServerID(), extension, mongoConn) || typeOfDialer.equals("manual") || typeOfDialer.equals("Missed Call CB") || typeOfDialer.equals("Click And Call")){
                socketConn = new Socket(serverDetails.getServerIP(),serverDetails.getAmiPort());
                socketRead = new BufferedReader(new InputStreamReader(socketConn.getInputStream()));
                socketWrite = new PrintWriter(socketConn.getOutputStream());
                socketConnected = true;
            }
        }
        catch (IOException ex) {
            Logger.getLogger(OriginateCall.class.getName()).log(Level.SEVERE, null, ex);
        }
        return socketConnected;
    }
    
    public void amiOriginateProcess() throws IOException{
        if(this.connectSocket()){
            try{
                boolean continueLoop=true;
                lastReadBlock=new ArrayList<String>();
                while(continueLoop){
                    lastReadLine = socketRead.readLine();
                    System.out.println(lastReadLine);
                    if(lastReadLine.contains("Asterisk Call Manager")){
                        String cmd = "Action: login\r\nUsername: "+serverDetails.getAmiUserName()+"\r\nSecret: "+
                            serverDetails.getAmiPassword()+"\r\nEvents: call,cdr,agent,dtmf,system\r\n\r\n";
                            socketWrite.write(cmd);
                            socketWrite.flush();
                            System.out.println(cmd);
                    }
                    else if(lastReadLine.contains("Message: Authentication accepted")){
                        String channel="";
                        if(extensionType.equals("Local"))
                        {
                            if(serverDetails.getServerType().equalsIgnoreCase("Euprravox"))
                            {
                                if(followMeExists)
                                    channel = "Local/"+followMeNumber+"@"+serverDetails.getDialOutContext();
                            }
                                //else
                                  //  channel = "Local/"+extension+"@"+serverDetails.getDialOutContext();
                            else
                                channel = "Local/"+extension+"@"+serverDetails.getDialOutContext(); 
                        }
                        else
                            channel=extensionType+"/"+extension;
                            
                        String cmd = "Action: Originate\r\nChannel: "+channel+
                                "\r\nContext: "+callContext+"\r\nExten: "+mobileNumberToUse+"\r\ncallerID: "+callerID+
                                "\r\nPriority: 1\r\n";
                        System.out.println("ORIGINATE "+cmd);
                        socketWrite.write(cmd);
                        cmd = "Action: Logoff\r\n\r\n";
                        socketWrite.write(cmd);
                        socketWrite.flush();
                        System.out.println(cmd);
                        gracefulExit();
                        continueLoop = false;
                    }
                    else if(lastReadLine.contains("Message:Originate Failed")){
                        new CtiDAO().deletePopupIfExistsDuringNextCall(mobileNumber, extension, serverDetails.getServerNamePrefix(), mongoConn);
                    }
                    else if(lastReadLine.length()==0){
                        lastReadBlock.clear();
                    }
                    else if(lastReadLine.length()>0){
                        lastReadBlock.add(lastReadLine);
                    }
                    this.lastReadLine = "";
                }//end of while
            }
            catch(Exception ex){
                LogClass.logMsg("OriginateCall", "amiOriginateProcess", ex.getMessage());
            }
        }
    }
    
    @Override
    public void run()
    {
        try {
            amiOriginateProcess();
        }
        catch (IOException ex) {
            LogClass.logMsg("OriginateCall", "amiOriginateProcess", ex.getMessage());
        }
    }

}