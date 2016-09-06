/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import com.mongodb.MongoClient;
import phonebridge.util.LogClass;
import com.phonebridgecti.dao.CtiDAO;
import java.util.Date;
import agentperformance.dao.AgentPerformanceDAO;
import campaignconfig.model.CampaignModel;
import campaignconfig.model.UserModel;
import com.phonebridge.callmodel.CallDao;
import com.phonebridge.callmodel.CallModel;
import com.phonebridgecti.dao.CampaignDAO;
import com.phonebridgecti.dao.UserDAO;
import com.phonebridgecti.db.CtiDB;
import java.util.ArrayList;
import java.util.HashMap;
import org.bson.Document;
import processdbevents.db.ProcessAMIDB;
import processdbevents.db.ProcessForReportsDB;

/**
 *
 * @author bharath
 */
public class ProcessForReportsDAO implements Runnable{
    private MongoClient mongoConn;
    private Date dateToUpdate;
    private String docID;
    private String serverNamePrefix;
    private String switchCaseCondition;
    private String queueNumber;
    private String valToUpdate;
    private String extension;
    private String recordingFileName;
    private String mmYYYY;
    private String phoneNumber;
    private String serverID;
    private String uniqueID;
    private String campaignID;
    
    public ProcessForReportsDAO(){
        
    }
    
    public ProcessForReportsDAO(MongoClient mongoConn,Date dateToUpdate,String valToUpdate,String docID,String serverNamePrefix,
            String switchCaseCondition,String queueNumber,String extension,String recordingFileName,String mmYYYY,
            String phoneNumber,String serverID,String uniqueID,String campaignID){
        this.mongoConn = mongoConn;
        this.dateToUpdate = dateToUpdate;
        this.docID = docID;
        this.serverNamePrefix = serverNamePrefix;
        this.switchCaseCondition = switchCaseCondition;
        this.queueNumber = queueNumber;
        this.valToUpdate = valToUpdate;
        this.extension = extension;
        this.recordingFileName = recordingFileName;
        this.mmYYYY = mmYYYY;
        this.phoneNumber = phoneNumber;
        this.serverID = serverID;
        this.uniqueID = uniqueID;
        this.campaignID = campaignID;
    }
    
    @Override
    public void run() {
        String campaignIDRetrievedFromDb = null;
        String extensionToSend = null;
        double answeredDuration = 0;
        switch(switchCaseCondition){
            case "incoming":
                /*
                    1) update TypeOfIncoming To Did Direct only if its null
                    2) Update CallStatus
                    3) Update extension details
                    4) Update hopping details
                    5) Update IVR Entry Time
                    6) Update userName and userID
                    7) Get queueNumber and create popup
                */
                this.updateTypeOfIncoming(docID, "DID Direct", mmYYYY, serverNamePrefix, mongoConn);
                this.updateCallStatus(docID, valToUpdate, "Incoming", mmYYYY, serverNamePrefix, mongoConn);
                this.updateExtension(docID, extension, mmYYYY, serverNamePrefix, mongoConn);
                this.updateCurrentExtension(docID, extension, mmYYYY, serverNamePrefix, mongoConn);
                this.pushHoppingDetails(docID, extension, dateToUpdate, mmYYYY, serverNamePrefix, mongoConn);
                this.updateIvrEntryTime(docID, mmYYYY, serverNamePrefix, mongoConn);
                this.updateUserNameAndID(docID, extension, serverID, mmYYYY, serverNamePrefix, mongoConn);
                String queue = this.getQueueNumberFromDoc(docID, mmYYYY, serverNamePrefix, mongoConn);
                new CtiDAO("incoming", extension, phoneNumber, queueNumber,campaignID,serverNamePrefix,
                    serverID,uniqueID, docID, dateToUpdate, null, mongoConn,null,null,null).createPopup();
                break;
            case "outgoing":
                /*
                    1) Update Call Status
                    2) Update extension
                    3) Update userName and userID
                    4) Create popup
                    5) Update whether agent has answered the call
                */
                this.updateCallStatus(docID, valToUpdate, "Outgoing", mmYYYY, serverNamePrefix, mongoConn);
                this.updateExtension(docID, extension, mmYYYY, serverNamePrefix, mongoConn);
                this.updateCurrentExtension(docID, extension, mmYYYY, serverNamePrefix, mongoConn);
                this.updateUserNameAndID(docID, extension, serverID, mmYYYY, serverNamePrefix, mongoConn);
                CtiDAO ctiDao = new CtiDAO("outgoing", extension, phoneNumber, queueNumber,campaignID,serverNamePrefix,
                    serverID,uniqueID, docID, dateToUpdate, "Dialing", mongoConn,null,null,null);
                ctiDao.createPopup();
                ctiDao.updateHasAgentAnsweredInOriginateCall(extension, phoneNumber,"Yes", serverNamePrefix, new CtiDB(), mongoConn);
                break;
            case "End":
                /*
                    1) Get extension details from call record
                    2) Get user details and if followMe exists for user, then do not send call closed.
                */
                extensionToSend = this.getExtensionFromDoc(docID, mmYYYY, serverNamePrefix, mongoConn);
                UserModel user = null;
                if(extensionToSend!=null && extensionToSend.length()>0){
                    user = new UserDAO().checkExtensionExistInUsers(extensionToSend, mongoConn);
                }
                this.updateCallStatus(docID, valToUpdate, "End", mmYYYY, serverNamePrefix, mongoConn);
                if(user!=null && user.isIsFollowMe())
                    return;
                new CtiDAO(null, extensionToSend, null, null,campaignID,serverNamePrefix,
                    serverID,uniqueID, docID, dateToUpdate,"HangUp" ,mongoConn,null,null,null).updateCallStatusAndTime();
                break;
            case "Cdr":
                /*
                    1) Called Only For Incoming, For IVR
                    2) Update recordings in call record. Concat with existing recording files
                */
                this.updateCallStatus(docID, valToUpdate, "Cdr", mmYYYY, serverNamePrefix, mongoConn);
                recordingFileName = recordingFileName.replace("audio:", "");
                this.updateRecordingFileNames(docID,recordingFileName, mmYYYY,serverNamePrefix, mongoConn);
                break;
            case "Bridge":
                /*
                    1) Update Call Status
                    2) Update queue waittime(only for incoming)
                */
                this.updateCallStatus(docID, valToUpdate, "Bridge", mmYYYY, serverNamePrefix, mongoConn);
                this.updateQueueWaitTime(docID, dateToUpdate, mmYYYY, serverNamePrefix, mongoConn);
                //this.updateCntInQueue(docID, null, "dec", mmYYYY, serverNamePrefix, mongoConn);
                extensionToSend = this.getCurrentExtensionFromDoc(docID,mmYYYY, serverNamePrefix, mongoConn);
                this.updateCallConnectTime(docID, dateToUpdate, mmYYYY, serverNamePrefix, mongoConn);
                this.updateCallConnectTimeInSecs(docID, dateToUpdate, mmYYYY, serverNamePrefix, mongoConn);
                new CtiDAO(null, extensionToSend, null, null,campaignID,serverNamePrefix,
                    serverID,uniqueID, docID, dateToUpdate, "Connected", mongoConn,null,null,null).updateCallStatusAndTime();
                campaignIDRetrievedFromDb = this.getCampaignID(docID, mmYYYY, serverNamePrefix, mongoConn);
                new AgentPerformanceDAO("totalAnswered", 0, extensionToSend, campaignIDRetrievedFromDb,serverNamePrefix,dateToUpdate, null,mongoConn).run();
                break;
            case "Join":
                /*
                    1) Update event time
                    2) Update Call Status
                    3) Update QueueNumber
                    4) Update Incoming Type(Join)
                */
                this.updateEventTime(docID, "queueJoinTime", dateToUpdate, mmYYYY, serverNamePrefix, mongoConn);
                this.updateCallStatus(docID, valToUpdate, "Join", mmYYYY, serverNamePrefix, mongoConn);
                this.updateQueueNumber(docID, queueNumber, mmYYYY, serverNamePrefix, mongoConn);
                this.updateTypeOfIncoming(docID, "Queue", mmYYYY, serverNamePrefix, mongoConn);
                //this.updateCntInQueue(docID, queueNumber, "inc", mmYYYY, serverNamePrefix, mongoConn);
                break;
            case "Leave":
                /*
                    1) Update Call Status
                    2) Update queue waittime
                */
                this.updateCallStatus(docID, valToUpdate, "Leave", mmYYYY, serverNamePrefix, mongoConn);
                this.updateQueueWaitTime(docID, dateToUpdate, mmYYYY, serverNamePrefix, mongoConn);
                //this.updateCntInQueue(docID, null, "dec", mmYYYY, serverNamePrefix, mongoConn);
                break;
            case "Hangup":
                /*
                    1) Update callStatus
                    2) Update eventTime
                    3) Update hangUp reason
                    4) Update answerDuration
                    5) Update typeOfIncoming
                    6) Update IVR entry time
                */
                this.updateCallStatus(docID, valToUpdate, "HangUp", mmYYYY, serverNamePrefix, mongoConn);
                extensionToSend = this.getExtensionFromDoc(docID, mmYYYY, serverNamePrefix, mongoConn);
                new CtiDAO(null, extensionToSend, null, null,campaignID,serverNamePrefix,
                    serverID,uniqueID, docID, dateToUpdate,"HangUp" ,mongoConn,null,null,null).updateCallStatusAndTime();
                
                this.updateEventTime(docID, "hangUpTime", dateToUpdate, mmYYYY, serverNamePrefix, mongoConn);
                this.updateHangUpReason(docID, valToUpdate, mmYYYY, serverNamePrefix, mongoConn);
                answeredDuration = this.updateAnswerDuration(docID, dateToUpdate, mmYYYY, serverNamePrefix, mongoConn);
                this.updateTypeOfIncomingInHangup(docID, mmYYYY, serverNamePrefix, mongoConn);
                this.updateIvrEntryTime(docID, mmYYYY, serverNamePrefix, mongoConn);
                extensionToSend = this.getExtensionFromDoc(docID, mmYYYY, serverNamePrefix, mongoConn);
                this.updatePopupStatus(extensionToSend, serverID, mongoConn);
                if(answeredDuration>0){
                    campaignIDRetrievedFromDb = this.getCampaignID(docID, mmYYYY, serverNamePrefix, mongoConn);
                    if(campaignIDRetrievedFromDb!=null)
                        new AgentPerformanceDAO("totalTalkTime", answeredDuration, extensionToSend, campaignIDRetrievedFromDb,serverNamePrefix, dateToUpdate,null,mongoConn).run();
                }
                CallModel callDetails = new CallDao().getCallDataForId(docID, serverNamePrefix, mmYYYY, serverID);
                try{
                    if(callDetails.getCampaignName()==null || callDetails.getCampaignName().length()<1){
                        /*CampaignModel campaign = new CampaignDB().getDefaultCampaignDetails(callDetails.getCallDirection(), mongoConn);
                        this.updateCampaignDetailsInCalls(docID, mmYYYY, serverNamePrefix, campaign, mongoConn);*/
                        CampaignModel campaign = this.updateCampaignDetailsInCalls(callDetails.getCallDirection(), docID, phoneNumber, mmYYYY, serverNamePrefix);
                        if(campaign!=null)
                            callDetails.setCampaignName(campaign.getCampaignName());
                    }
                }
                catch(Exception ex){
                    System.out.println("ERROR IN UPDATING CAMPAIGN DETAILS IN HANGUP");
                }
                new CallDao().callBackURL(callDetails, serverNamePrefix, mmYYYY, serverID);
                /*if(callDetails.getPhoneNumber()!=null && callDetails.getPhoneNumber().length()>0 && callDetails.getDuration()>0){
                    AdditionalSettings settings = new AdditionalSettingsDao().getCurrentSettings();
                    new SMSDao().insertSMSData(callDetails.getPhoneNumber(), settings.getAfterCallSMSContent(),serverNamePrefix);
                }*/
                //this.checkForBCCBAndInsert(docID,mmYYYY,serverNamePrefix,mongoConn);
                break;
            case "Transfer":
                /*
                    1) Update If Transfered
                    2) Update Details of Transfer
                */
                this.updateIfTransfered(docID, mmYYYY, serverNamePrefix, mongoConn);
                this.updateTranserDetails(docID, valToUpdate, dateToUpdate,mmYYYY, serverNamePrefix, mongoConn);
                break;
            case "CoreShowChannelsComplete":
                ArrayList<String> channelsFromCall = this.getChannelsOfOpenCalls(mmYYYY, serverNamePrefix, mongoConn);
                ArrayList<String> channelsFromCoreShow = new ArrayList<>();
                if(docID!=null)
                    channelsFromCoreShow = this.getChannelsFromCoreShowCollection(docID, serverNamePrefix, mongoConn);
                channelsFromCall.removeAll(channelsFromCoreShow);
                for(String eachChannel : channelsFromCall){
                    HashMap<String,String> callDet = this.getCallIDForChannel(eachChannel, mmYYYY, serverNamePrefix, mongoConn);
                    if(!callDet.isEmpty()){
                        this.updateCallClosedIfChannelNotExists(callDet.get("callID"), mmYYYY, serverNamePrefix, mongoConn);
                        extensionToSend = this.getExtensionFromDoc(callDet.get("callID"), mmYYYY, serverNamePrefix, mongoConn);                
                        /*CtiDAO ctiDaoObjt = new CtiDAO(null, extensionToSend,phoneNumber, null,null,null, null,serverNamePrefix,
                            serverID,uniqueID, callDetails.get("callID"), dateToUpdate,"HangUp" ,mongoConn,null,0,null,null,null);*/
                        CtiDAO ctiDaoObjt = new CtiDAO(null, extensionToSend, null, null, null, serverNamePrefix, 
                            serverID, null, callDet.get("callID"), dateToUpdate, "HangUp", mongoConn, null, null, null);
                        if(extensionToSend!=null){
                            ctiDaoObjt.updateCallStatusAndTime();
                            this.updatePopupStatus(extensionToSend, serverID, mongoConn);
                            new ProcessAMIDB().updateUserExtensionStatus(extensionToSend, "0", serverID, mongoConn);
                        }
                    }
                }
                if(docID!=null)
                    this.deleteRecordFromCoreShowCollection(docID, serverNamePrefix, mongoConn);
                break;
        }
    }
    
    public void updateEventTime(String docID,String fieldName,Date eventTime,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().updateEventTime(docID, fieldName, eventTime, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportDAO", "updateEventTime", ex.getMessage());
        }
    }
    
    public void updateQueueNumber(String docID,String queueNumber,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().updateQueueNumber(docID, queueNumber, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "updateQueueNumber", ex.getMessage());
        }
    }
    
    public void updateTypeOfIncoming(String docID,String typeOfIncoming,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().updateTypeOfIncoming(docID, typeOfIncoming, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "updateTypeOfIncoming", ex.getMessage());
        }
    }
    
    public void updateIfTransfered(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().updateIfTransfered(docID, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "updateIfTransfered", ex.getMessage());
        }
    }
    
    public void updateTranserDetails(String docID,String fromExtension,Date transferTime,String mmYYYY,String serverNamePrefix,
            MongoClient mongoConn){
        try{
            new ProcessForReportsDB().updateTranserDetails(docID, fromExtension,transferTime, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "updateTranserDetails", ex.getMessage());
        }
    }
    
    public void updateQueueWaitTime(String docID,Date timeToCalculateWith,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
                new ProcessForReportsDB().updateQueueWaitTime(docID, timeToCalculateWith, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "updateQueueWaitTime", ex.getMessage());
        }
    }
    
    public void updateExtension(String docID,String extensionToAppend,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().updateExtension(docID, extensionToAppend, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "pushExtensionIntoArr", ex.getMessage());
        }
    }
    
    public void updateCurrentExtension(String docID,String extensionToAppend,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().updateCurrentExtension(docID, extensionToAppend, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "pushCurrentExtensionIntoArr", ex.getMessage());
        }
    }
    
    public void updateCallStatus(String docID,String callStatus,String checkForCondition,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().updateCallStatus(docID, callStatus, checkForCondition, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "updateCallStatus", ex.getMessage());
        }
    }
    
    public void updateHangUpReason(String docID,String hangUpReason,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().updateHangUpReason(docID, hangUpReason, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "updateHangUpReason", ex.getMessage());
        }
    }
    
    public void pushHoppingDetails(String docID,String extension,Date hoppingTime,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().pushHoppingDetails(docID, extension, hoppingTime,mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "pushHoppingDetails", ex.getMessage());
        }
    }
    
    public void updateIvrEntryTime(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().updateIvrEntryTime(docID, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDB", "updateIvrEntryTime", ex.getMessage());
        }
    }
    
    public void updateRecordingFileNames(String docID,String recordingFileName,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            if(recordingFileName.length()>0)
                new ProcessForReportsDB().updateRecordingFileNames(docID,recordingFileName, mmYYYY,serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "updateRecordingFileNames", ex.getMessage());
        }
    }
    
    public double updateAnswerDuration(String docID,Date timeToCalculateWith,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        double answerDuration = 0;
        try{
            answerDuration = new ProcessForReportsDB().updateAnswerDuration(docID, timeToCalculateWith, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "updateAnswerDuration", ex.getMessage());
        }
        return answerDuration;
    }
    
    public void updateCntInQueue(String docID,String queueNumber,String incOrDec,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().updateCntInQueue(docID, queueNumber, incOrDec, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "updateCntInQueue", ex.getMessage());
        }
    }
    
    public void updateTypeOfIncomingInHangup(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().updateTypeOfIncomingInHangup(docID, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "updateTypeOfIncomingInHangup", ex.getMessage());
        }
    }
    
    public String getQueueNumberFromDoc(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String queue = null;
        try{
            queue = new ProcessForReportsDB().getQueueNumberFromDoc(docID, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "getQueueNumberFromDoc", ex.getMessage());
        }
        return queue;
    }
    
    public String getExtensionFromDoc(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String extension = null;
        try{
            extension = new ProcessForReportsDB().getExtensionFromDoc(docID, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "getExtensionFromDoc", ex.getMessage());
        }
        return extension;
    }
    
    public String getCurrentExtensionFromDoc(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String extension = null;
        try{
            extension = new ProcessForReportsDB().getCurrentExtensionFromDoc(docID, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "getExtensionFromDoc", ex.getMessage());
        }
        return extension;
    }
    
    public void updatePopupStatus(String extension,String serverID,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().updatePopupStatus(extension, serverID, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "updatePopupStatus", ex.getMessage());
        }
    }
    
    public void updateCallConnectTime(String docID,Date connectTime,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().updateCallConnectTime(docID, connectTime, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "updateCallConnectTime", ex.getMessage());
        }
    }
    
    public void updateCallConnectTimeInSecs(String docID,Date connectTime,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().updateCallConnectTimeInSecs(docID, connectTime, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "updateCallConnectTimeInSecs", ex.getMessage());
        }
    }
    
    public String getCampaignID(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String campaignID = null;
        try{
            campaignID = new ProcessForReportsDB().getCampaignID(docID, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "getCampaignID", ex.getMessage());
        }
        return campaignID;
    }
    
    public void updateUserNameAndID(String docID,String extension,String serverID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            UserModel userDetails = new UserDAO().getUserNameAndIDForExtensionAndServerID(extension, serverID, mongoConn);
            new ProcessForReportsDB().updateUserNameAndID(docID, userDetails, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "updateUserNameAndID", ex.getMessage());
        }
    }
    
    /*
        Used to get channels from calls collection
    */
    public ArrayList<String> getChannelsOfOpenCalls(String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        ArrayList<String> channelInCalls = new ArrayList<>();
        try{
            channelInCalls = new ProcessForReportsDB().getChannelsOfOpenCalls(mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "getChannelsOfOpenCalls", ex.getMessage());
        }
        return channelInCalls;
    }
    
    /*
        Used to get channels from coreShowChannel collection
    */
    public ArrayList<String> getChannelsFromCoreShowCollection(String docID,String serverNamePrefix,MongoClient mongoConn){
        ArrayList<String> channelInCoreShow = new ArrayList<>();
        try{
            channelInCoreShow = new ProcessForReportsDB().getChannelsFromCoreShowCollection(docID, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "getChannelsFromCoreShowCollection", ex.getMessage());
        }
        return channelInCoreShow;
    }
    
    /*
        Used to get callID for channel
    */
    public HashMap<String,String> getCallIDForChannel(String channel,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        HashMap<String,String> callDetails = new HashMap<>();
        try{
            callDetails = new ProcessForReportsDB().getCallIDForChannel(channel, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "getCallIDForChannel", ex.getMessage());
        }
        return callDetails;
    }
    
    /*
        Update Call Closed if channel not found
    */
    public void updateCallClosedIfChannelNotExists(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().updateCallClosedIfChannelNotExists(docID, mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "updateCallClosedIfChannelNotExists", ex.getMessage());
        }
    }
    
    /*
        Delete record from coreShow collection after processing
    */
    public void deleteRecordFromCoreShowCollection(String docID,String serverNamePrefix,MongoClient mongoConn){
        try{
            new ProcessForReportsDB().deleteRecordFromCoreShowCollection(docID, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessForReportsDAO", "deleteRecordFromCoreShowCollection", ex.getMessage());
        }
    }
    
    public boolean checkForBCCBAndInsert(String callId,String mmYYYY,String serverPrefix,MongoClient mongoConn){
        return new ProcessForReportsDB().checkForBCCBAndInsert(callId, mmYYYY, serverPrefix, mongoConn);
    }
    
    public void updateCampaignDetailsInCalls(String callId,String mmYYYY,String serverPrefix,CampaignModel campaign,
        MongoClient mongoConn){
        new ProcessForReportsDB().updateCampaignDetailsInCalls(callId, mmYYYY, serverPrefix, campaign, mongoConn);
    }
    
    public CampaignModel updateCampaignDetailsInCalls(String callDirection,String callId,String phoneNumber,String mmYYYY,String serverPrefix){
        Document insertDoc = null;
        CampaignModel campaign = null;
        if(callDirection==null || callDirection.length()<1 || phoneNumber==null || phoneNumber.length()<1)
            return campaign;
        CampaignModel moduleLinkedCampaign = new CampaignDAO().getDefaultModuleLinkedCampaign(mongoConn,callDirection);
        try{
            insertDoc = new CampaignDAO().checkAndGetDetailsForNumber(phoneNumber,callDirection,
                moduleLinkedCampaign,extension,queueNumber,serverID,mongoConn);
        }
        catch(Exception ex){
            System.out.println("ERROR IN GETTING CAMPAIGN ID FOR UPDATING IN DETAILS ");
        }
        if(!insertDoc.isEmpty()){
            campaign = moduleLinkedCampaign;
        }
        else{
            campaign = new CampaignDAO().getDefaultCampaignDetails(callDirection, mongoConn);
        }
        if(campaign!=null){
            try{
                this.updateCampaignDetailsInCalls(callId, mmYYYY, serverPrefix, campaign, mongoConn);
            }
            catch(Exception ex){
                
            }
        }
        return campaign;
    }
}
