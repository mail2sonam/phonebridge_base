/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridge.originate;

import campaignconfig.db.TwoLegDialingDB;
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
import phonebridge.util.LogClass;
import phonebridgelogger.model.Server;

/**
 *
 * @author venky
 */
public class TwoLegOriginateCall implements Runnable{
    private String lastReadLine;
    private List<String> lastReadBlock;
    private Socket socketConn;
    private PrintWriter socketWrite;
    private BufferedReader socketRead;

    Server serverDetails;
    String prefix;
    String Phone1;
    String Phone2;
    MongoClient mongoConn;
    String phone1ToUse;
    String phone2ToUse;
    String callContext;
    String campaignID;
    
    public TwoLegOriginateCall(Server serverDetails,String prefix,String phone1,String phone2,String callContext,
            String campaignID,MongoClient mongoConn){
        this.serverDetails = serverDetails;
        this.prefix = prefix;
        this.Phone1 = phone1;
        this.Phone2 = phone2;
        
        this.mongoConn = mongoConn;
        if(this.prefix==null || this.prefix.length()==0){
            this.phone1ToUse = this.Phone1;
            this.phone2ToUse = this.Phone2;
        }
        else{
            this.phone1ToUse = this.prefix.concat(this.Phone1);
            this.phone2ToUse = this.prefix.concat(this.Phone2);
        }
        this.callContext = callContext;
        this.campaignID = campaignID;
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
            socketConn = new Socket(serverDetails.getServerIP(),serverDetails.getAmiPort());
            socketRead = new BufferedReader(new InputStreamReader(socketConn.getInputStream()));
            socketWrite = new PrintWriter(socketConn.getOutputStream());
            socketConnected = true;
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
                            serverDetails.getAmiPassword()+"\r\nEvents: call,cdr,agent,dtmf\r\n\r\n";
                            socketWrite.write(cmd);
                            socketWrite.flush();
                            System.out.println(cmd);
                    }
                    else if(lastReadLine.contains("Message: Authentication accepted")){
                        String channel = "Local/"+phone1ToUse+"@"+callContext;
                        String cmd = "Action: Originate\r\nChannel: "+channel+
                                "\r\nContext: "+callContext+"\r\nExten: "+phone2ToUse+
                                "\r\nPriority: 1\r\n";
                        System.out.println("ORIGINATE "+cmd);
                        socketWrite.write(cmd);
                        new TwoLegDialingDB().updateusingPhoneAndCampaignID(Phone1, Phone2, campaignID, "processed", cmd);
                        cmd = "Action: Logoff\r\n\r\n";
                        socketWrite.write(cmd);
                        socketWrite.flush();
                        System.out.println(cmd);
                        gracefulExit();
                        continueLoop = false;
                    }
                    else if(lastReadLine.contains("Message:Originate Failed")){
                        //new CtiDAO().deletePopupIfExistsDuringNextCall(mobileNumber, extension, serverDetails.getServerNamePrefix(), mongoConn);
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
