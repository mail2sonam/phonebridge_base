/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridge.originate;

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
public class RemoveFromQueue {
    String extension;
    Server serverDetails;
    String queueName;
            
    private String lastReadLine;
    private List<String> lastReadBlock;
    private Socket socketConn;
    private PrintWriter socketWrite;
    private BufferedReader socketRead;
    
    public RemoveFromQueue(String extension,Server serverDetails,String queueName){
        this.extension = extension;
        this.serverDetails = serverDetails;
        this.queueName = queueName;
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
    
    public void amiRemoveFromQueueProcess(){
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
                        String cmd = "Action: QueueRemove\r\nQueue: "+queueName+
                                "\r\nInterface: local/"+extension+"@from-queue\r\n";
                        System.out.println("ADD To QUEUE: "+cmd);
                        socketWrite.write(cmd);
                        cmd = "Action: Logoff\r\n\r\n";
                        socketWrite.write(cmd);
                        socketWrite.flush();
                        System.out.println(cmd);
                        gracefulExit();
                        continueLoop = false;
                    }
                    else if(lastReadLine.contains("Message:Originate Failed")){
                        
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
}
