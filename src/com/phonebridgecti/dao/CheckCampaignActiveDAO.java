package com.phonebridgecti.dao;

import com.phonebridgecti.dao.CheckCampActivewitoutThread;
import com.mongodb.MongoClient;
import phonebridge.originate.OriginateCall;
import phonebridgelogger.model.Server;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import campaignconfig.model.CampaignModel;

public class CheckCampaignActiveDAO implements Runnable{
    
    private String extension;
    private Server serverDetails;
    private MongoClient mongoConn;
    boolean userStatus;
    String prefix;
    CampaignModel campDetails;
    String mobileNumber; 
    int delayForCall;
    String typeOfDialer;
    
    public CheckCampaignActiveDAO(String extension,Server serverDetails,MongoClient mongoConn){
        this.extension = extension;
        this.serverDetails = serverDetails;
        this.mongoConn = mongoConn;
    }
    
   @Override
    public void run() {
        /*CheckCampActivewitoutThread checkCampActivewitoutThread=new CheckCampActivewitoutThread(this.extension, 
                this.serverDetails, this.mongoConn);
        if(!checkCampActivewitoutThread.getOriginateDetails())
            return;
        try {
            Thread.sleep(delayForCall*1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(OriginateCall.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            new OriginateCall(serverDetails.getServerIP(),serverDetails.getAmiPort(),serverDetails.getAmiUserName(),
                serverDetails.getAmiPassword(),prefix,serverDetails.getDialOutContext(), mobileNumber, extension,typeOfDialer,mongoConn).amiOriginateProcess();
        } catch (IOException ex) {
            Logger.getLogger(OriginateCall.class.getName()).log(Level.SEVERE, null, ex);
        }
                */
    }
}