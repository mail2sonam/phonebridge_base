/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import campaignconfig.db.CampaignDB;
import campaignconfig.model.CampaignModel;
import campaignconfig.model.UserModel;
import com.mongodb.MongoClient;
import com.phonebridgecti.dao.CampaignDAO;
import com.phonebridgecti.dao.CtiDAO;
import com.phonebridgecti.dao.UserDAO;
import com.settings.AdditionalSettings;
import com.settings.AdditionalSettingsDao;
import processdbevents.model.BridgeModel;
import processdbevents.model.CdrModel;
import processdbevents.model.DTMFModel;
import processdbevents.model.DialBeginModel;
import processdbevents.model.DialEndModel;
import processdbevents.model.ExtensionStatus;
import processdbevents.model.HangupModel;
import processdbevents.model.HoldModel;
import processdbevents.model.JoinModel;
import processdbevents.model.LeaveModel;
import processdbevents.model.NewChannelModel;
import processdbevents.model.NewStateModel;
import processdbevents.model.QueueCallerAbandonModel;
import phonebridgelogger.model.Server;
import processdbevents.model.TransferModel;
import phonebridgelogger.model.Trunk;
import phonebridge.util.LogClass;
import phonebridge.util.util;
import java.util.ArrayList;
import java.util.Date;
import org.bson.Document;
import phonebridge.missedcall.dao.MissedCallDAO;
import phonebridge.originate.OriginateCallDao;
import phonebridge.originate.OriginateRequestModel;
import phonebridge.postdata.CurlDataForAPI;
import phonebridge.postdata.CurlDataToURL;
import phonebridge.sms.SMSDao;
import processdbevents.db.ProcessAMIDB;
import processdbevents.db.ProcessForReportsDB;
import processdbevents.db.RetrieveEventsDB;
import processdbevents.model.CoreShowChannelModel;
import processdbevents.model.CoreShowChannelsCompleteModel;
import processdbevents.model.PeerStatusModel;

/**
 *
 * @author bharath
 */
public class ProcessEvents {
    private Server serverDetails;
    private ProcessAMIDB processAMIDB;
    private MongoClient mongoConn;
    private String serverNamePrefix;
    private RetrieveEventsDB retrieveEventsDb;
    private util utilObj;
    private boolean addCallClosedInQry;
    
    public ProcessEvents(Server serverDetails,MongoClient mongoConn){
        this.serverDetails = serverDetails;
        this.processAMIDB = new ProcessAMIDB();
        this.mongoConn = mongoConn;
        this.serverNamePrefix = this.serverDetails.getServerNamePrefix();
        this.retrieveEventsDb = new RetrieveEventsDB();
        this.utilObj = new util();
        this.addCallClosedInQry = false;
    }
    
    public Document getEventFromDB(String collName,String serverID,MongoClient mongoConn){
        Document eventsFromDB = null;
        try{
            eventsFromDB = retrieveEventsDb.getEventsFromCollection(collName,serverID, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "getEventsFromDB", ex.getMessage());
        }
        return eventsFromDB;
    }

    public void processDBEvents(Server serverDetails, String collectionName){
        Document eventsFromDB;
        try{
            do{
                eventsFromDB = this.getEventFromDB(collectionName,serverDetails.getServerID(), mongoConn);
                if(!eventsFromDB.isEmpty()){
                    if(!eventsFromDB.containsKey("event"))
                        return;
                    switch(eventsFromDB.get("event").toString()){
                        case "Dial":
                            processDial(eventsFromDB);
                            break;
                        case "Cdr":
                            processCdr(eventsFromDB);
                            break;
                        case "Bridge":
                            processBridge(eventsFromDB);
                            break;
                        case "Join":
                            processJoin(eventsFromDB);
                            break;
                        case "Leave":
                            processLeave(eventsFromDB);
                            break;
                        case "Newstate":
                            processNewState(eventsFromDB);
                            break;
                        case "DTMF":
                            processDTMF(eventsFromDB);
                            break;
                        case "Hangup":
                            processHangup(eventsFromDB);
                            break;
                        case "Transfer":
                            processTransfer(eventsFromDB);
                            break;
                        case "MusicOnHold":
                            processMusicOnHold(eventsFromDB);
                            break;
                        case "QueueCallerAbandon":
                            processQueueCallerAbandon(eventsFromDB);
                            break;
                        case "ExtensionStatus":
                            processExtensionStatus(eventsFromDB);
                            break;
                        case "Newchannel":
                            processNewChannel(eventsFromDB);
                            break;
                        case "CoreShowChannel":
                            processCoreShowChannel(eventsFromDB);
                            break;
                        case "CoreShowChannelsComplete":
                            processCoreShowChannelsComplete(eventsFromDB);
                            break;
                        case "PeerStatus":
                            processPeerStatus(eventsFromDB);
                            break;
                        default:
                            //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsFromDB.getObjectId("_id"),eventsFromDB.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
                            break;
                    }
                    retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsFromDB.getObjectId("_id"),eventsFromDB.getDate("eventtime").getTime(),"events_".concat(serverNamePrefix), mongoConn);
                }
            }
            while(!eventsFromDB.isEmpty());
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processDBEvents", ex.getMessage());
        }
    }
    
    private void processDial(Document eventsToProcess){
        try{
            switch(eventsToProcess.getString("subevent")){
                case "Begin":
                    processDialBegin(eventsToProcess);
                    break;
                case "End":
                    processDialEnd(eventsToProcess);
                    break;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processDial", ex.getMessage());
        }
    }
    
    private void processDialBegin(Document eventsToProcess){
        DialBeginModel dialBegin = new DialBeginDAO().createDialBeginObject(eventsToProcess);
        try{
            this.processDialBeginEvents(dialBegin);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processDialBegin", ex.getMessage());
        }
        //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsToProcess.getObjectId("_id"),eventsToProcess.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
    }
    
    private void processDialEnd(Document eventsToProcess){
        DialEndModel dialEnd = new DialEndDAO().createDialEndObject(eventsToProcess);
        try{
            this.processDialEndEvents(dialEnd);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processDialBegin", ex.getMessage());
        }
        //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsToProcess.getObjectId("_id"),eventsToProcess.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
    }
    
    private void processBridge(Document eventsToProcess){
        BridgeModel bridge = new BridgeDAO().createBridgeObject(eventsToProcess);
        try{
            this.processBridgeEvents(bridge);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processDialBegin", ex.getMessage());
        }
        //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsToProcess.getObjectId("_id"),eventsToProcess.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
    }
    
    private void processCdr(Document eventsToProcess){
        CdrModel cdr = new CdrDAO().createCdrObject(eventsToProcess);
        //new CdrDAO().processCdr(serverDetails, cdr, utilObj, mongoConn);
        try{
            this.processCdrEvents(cdr);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processDialBegin", ex.getMessage());
        }
        //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsToProcess.getObjectId("_id"),eventsToProcess.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
    }
    
    private void processJoin(Document eventsToProcess){
        JoinModel join = new JoinDAO().createJoinObject(eventsToProcess);
        try{
            this.processJoinEvents(join);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processDialBegin", ex.getMessage());
        }
        //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsToProcess.getObjectId("_id"),eventsToProcess.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
    }
    
    private void processLeave(Document eventsToProcess){
        LeaveModel leave = new LeaveDAO().createLeaveObject(eventsToProcess);
        try{
            this.processLeaveEvents(leave);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processDialBegin", ex.getMessage());
        }
        //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsToProcess.getObjectId("_id"),eventsToProcess.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
    }
    
    private void processNewState(Document eventsToProcess){
        NewStateModel newState = new NewStateDAO().createNewStateObject(eventsToProcess);
        try{
            this.processNewStateEvent(newState);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processDialBegin", ex.getMessage());
        }
        //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsToProcess.getObjectId("_id"),eventsToProcess.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
    }
    
    private void processDTMF(Document eventsToProcess){
        DTMFModel dtmf = new DTMFDAO().createDTMFObject(eventsToProcess);
        try{
            this.processDTMFEvents(dtmf);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processDialBegin", ex.getMessage());
        }
        //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsToProcess.getObjectId("_id"),eventsToProcess.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
    }
    
    private void processHangup(Document eventsToProcess){
        HangupModel hangUp = new HangUpDAO().createHangupObject(eventsToProcess);
        try{
            this.processHangUpEvents(hangUp);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processDialBegin", ex.getMessage());
        }
        //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsToProcess.getObjectId("_id"),eventsToProcess.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
    }
    
    private void processTransfer(Document eventsToProcess){
        TransferModel transfer = new TransferDAO().createTransferObject(eventsToProcess);
        try{
            this.processTransferEvents(transfer);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processDialBegin", ex.getMessage());
        }
        //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsToProcess.getObjectId("_id"),eventsToProcess.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
    }
    
    private void processMusicOnHold(Document eventsToProcess){
        HoldModel hold = new HoldDAO().createHoldObject(eventsToProcess);
        try{
            this.processHoldEvents(hold);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processDialBegin", ex.getMessage());
        }
        //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsToProcess.getObjectId("_id"),eventsToProcess.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
    }
    
    private void processQueueCallerAbandon(Document eventsToProcess){
        QueueCallerAbandonModel queueCallerAbandon = new QueueCallerAbandonDAO().createQueueCallerAbandonObject(eventsToProcess);
        try{
            this.processQueueCallerAbandonEvents(queueCallerAbandon);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processDialBegin", ex.getMessage());
        }
        //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsToProcess.getObjectId("_id"),eventsToProcess.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
    }
    
    private void processExtensionStatus(Document eventsToProcess){
        ExtensionStatus extStatus = new ExtensionStatusDAO().createExtensionStatusObject(eventsToProcess);
        try{
            this.processExtensionStatusEvents(extStatus);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processDialBegin", ex.getMessage());
        }
        //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsToProcess.getObjectId("_id"),eventsToProcess.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
    }
    
    private void processNewChannel(Document eventsToProcess){
        NewChannelModel newChannel = new NewChannelDAO().createNewChannelObject(eventsToProcess);
        //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsToProcess.getObjectId("_id"),eventsToProcess.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
    }
    
    private void processCoreShowChannel(Document eventsToProcess){
        CoreShowChannelModel coreShowChannel = new CoreShowChannelDAO().createCoreShowChannelObject(eventsToProcess);
        try{
            this.processCoreShowChannelEvents(coreShowChannel);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processCoreShowChannel", ex.getMessage());
        }
        //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsToProcess.getObjectId("_id"),eventsToProcess.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
    }
    
    private void processCoreShowChannelsComplete(Document eventsToProcess){
        CoreShowChannelsCompleteModel coreShowChannelComplete = new CoreShowChannelsCompleteDAO().createCoreShowChannelCompleteObject(eventsToProcess);
        try{
            this.processCoreShowChannelsCompleteEvents(coreShowChannelComplete);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processCoreShowChannelsComplete", ex.getMessage());
        }
        //retrieveEventsDb.moveProcessedEventsToAnotherCollection(eventsToProcess.getObjectId("_id"),eventsToProcess.getDate("eventtime").getTime(), "events_".concat(serverNamePrefix), mongoConn);
    }
    
    private void processPeerStatus(Document eventsToProcess){
        try{
        if(eventsToProcess.containsKey("address") || eventsToProcess.containsKey("cause")){
            PeerStatusModel peerStatus = new PeerStatusDAO().createPeerStatusObject(eventsToProcess);
            
                this.processPeerStatusEvents(peerStatus);
            
        }
        }catch(Exception e){
                e.printStackTrace();
            }
    }
    
    private void processNewStateEvent(NewStateModel newState){
        /*
            1) Only newState containing trunk is processed, rest is ignored
        */
        String phoneNumber = null;
        String uniqueID = newState.getUniqueID();
        String docID = null;
        ArrayList<String> reasonArr = new ArrayList<>();
        String mmYYYY = utilObj.getMMYYYYForJavaDate(newState.getEventTime().getTime());
        boolean updateOrignateReqIDInCalls = false;
        String channel = newState.getChannel();
        Date eventTime = newState.getEventTime();
        if(serverDetails.getServerType().equals("euprravox"))
            phoneNumber = newState.getCallerIdNum();
        else
            phoneNumber = newState.getCallerIdName();
        
        Trunk trunkUsed = new CdrDAO().getTrunkUsedInCall(channel, null, serverDetails.getTrunk());
        if(trunkUsed==null)
            return;
        /*
            1) Checked if callerIDName is a valid ObjectID. 
            2) Will be valid only for MissedCallCB and clickAndCall from CRM
            3) If callID is found using originateReqID then data is pushed inside that call record.
            4) Else a flag is updated specifying originateReqID has to be updated in calls.
        */
        if(processAMIDB.checkIfObjectIdIsValid(newState.getCallerIdName())){
            OriginateRequestModel detailsFromOriginate = new OriginateCallDao().getDetailsFromOriginateCollForID(newState.getCallerIdName(), serverNamePrefix, mongoConn);
            if(detailsFromOriginate!=null){
                docID = processAMIDB.getCallDocIDUsingOriginateReqID(newState.getCallerIdName(), newState.getEventTime(),mmYYYY, serverNamePrefix, mongoConn);
                if(docID!=null)
                    reasonArr.add("Found originateRequestID "+newState.getCallerIdName()+" in document. ");
                else{
                    updateOrignateReqIDInCalls = true;
                    reasonArr.add("Could Not Find originateReqID "+newState.getCallerIdName()+" in calls in unclosed state. ");
                }
            }
        }
        if(docID==null){
            reasonArr.add("Could Not find callID using originateReqID "+newState.getCallerIdName());
            docID = processAMIDB.getDocIDByQueryingWithChannelAndDestination(channel, null, eventTime, true,mmYYYY, serverNamePrefix, mongoConn);
        }
        if(docID==null){
            reasonArr.add("Could Not Find callID using channel "+channel);
            docID = processAMIDB.getDocIdForPhoneNumberUsingCallDirection(newState.getConnectedLineNum(), newState.getEventTime(), mmYYYY, serverNamePrefix, mongoConn);
        }
        if(docID==null){
            docID = processAMIDB.insertNewCall(uniqueID,"externalcall",eventTime,"NewState", mmYYYY,serverNamePrefix,false,mongoConn);
            if(updateOrignateReqIDInCalls)
                processAMIDB.updateOriginateReqIdInCalls(docID,newState.getCallerIdName(), mmYYYY, serverNamePrefix, mongoConn);
            else
                processAMIDB.updatePhoneNumberInDoc(docID, phoneNumber, mmYYYY, serverNamePrefix, mongoConn);
            reasonArr.add("Could Not find callID using phoneNumber "+newState.getConnectedLineNum());
            reasonArr.add("A New CallDoc is Created ");
        }
        processAMIDB.pushDataIntoArray(docID,"NewState",newState.getDocument(),reasonArr, mmYYYY,serverNamePrefix,mongoConn);
        processAMIDB.pushAllChannelsFoundInCalls(docID, channel, mmYYYY, serverNamePrefix, mongoConn);
    }
    
    private void processDialBeginEvents(DialBeginModel dialBegin){
                
        DialBeginDAO dialBeginDao = new DialBeginDAO();
        String channel = dialBegin.getChannel();
        String destination = dialBegin.getDestination();
        String connectedLineNumber = dialBegin.getConnectedLineNum();
        String uniqueID = dialBegin.getUniqueID();
        String destUniqueID = dialBegin.getDestUniqueID();
        String channelToPush = null;
        
        String phoneNumber = null;
        String extension = null;
        String hopperExtension = null;
        String docID = null;
        String mmYYYY = utilObj.getMMYYYYForJavaDate(dialBegin.getEventTime().getTime());
        ArrayList<String> reasonArr = new ArrayList<>();
        
        Trunk trunkUsed = new CdrDAO().getTrunkUsedInCall(channel,destination,serverDetails.getTrunk());
        String originateReqID = null;
        String[] callDirectionArr = null;
        OriginateRequestModel detailsFromOriginate = null;
        String campaignID = null;
        String extensionOrFollowMe = null;
        Date eventTime = dialBegin.getEventTime();
        String trunkVal = "";
        String valToUpdate = null;
        String trunk = null;
        
        /*
            1) First check if dialBegin belongs to transferedCall
            2) Found by checking channel and destination transferChannel Arr
            3) If docID is found then data is pushed and transfer details is updated
        */
        docID = processAMIDB.checkIfTargetChannelExistsInDialBegin(channel, destination, dialBegin.getEventTime(),mmYYYY, serverNamePrefix, mongoConn);
        if(docID!=null){
            String transferedTo = dialBegin.getDialString();
            reasonArr.add("Channel "+channel+" or Destination "+destination+" found is transferedChannel Array");
            processAMIDB.pushDataIntoArray(docID,"DialBegin",dialBegin.getDocument(),reasonArr, mmYYYY,serverNamePrefix, mongoConn);
            new ProcessForReportsDAO().updateCurrentExtension(docID, transferedTo, mmYYYY, serverNamePrefix, mongoConn);
            processAMIDB.updateTransferedToInTransferDetails(docID, transferedTo, mmYYYY, serverNamePrefix, mongoConn);
            processAMIDB.pushAllChannelsFoundInCalls(docID, channel, mmYYYY, serverNamePrefix, mongoConn);
            processAMIDB.pushAllChannelsFoundInCalls(docID, destination, mmYYYY, serverNamePrefix, mongoConn);
            String campaignIDFromCurrentCalls = new ProcessForReportsDB().getCampaignID(docID, mmYYYY, serverNamePrefix, mongoConn);
            CampaignModel campaignDetails = new CampaignDB().getCampaignDetailsUsingID(campaignIDFromCurrentCalls);
            if(campaignDetails!=null && campaignDetails.getCampaignSource().equals("apicampaign"))
            {
                new CtiDAO("incoming", transferedTo, dialBegin.getCallerIDName(), null,campaignIDFromCurrentCalls,serverNamePrefix,
                    serverDetails.getServerID(),dialBegin.getUniqueID(), docID, dialBegin.getEventTime(), null, mongoConn,null,null,null).createPopup();
            }
            return;
        }
        
        //Added By Venky for Follow Me Capture
        if(trunkUsed!=null || channel.contains(serverDetails.getQueueContext()) ){
            originateReqID = dialBegin.getConnectedLineName();
            if(processAMIDB.checkIfObjectIdIsValid(originateReqID)){
                /*
                    1) Checking if valid objectId
                    2) Considering callDirection to be outgoing, because will get valid objectID only if
                        MissedCall CB and clickAndDial From CRM
                    3) Get details from originateReq
                */
                callDirectionArr = new String[]{"outgoing","Found ConectedLineName to contain valid ObjectID "+originateReqID,""};
                detailsFromOriginate = new OriginateCallDao().getDetailsFromOriginateCollForID(originateReqID, serverNamePrefix, mongoConn);
            }
            else{
                /*
                    1) Not valid ObjectID
                    2) First trying to find phoneNumber in calls. If found and connectedLineNum contains FMGL
                        then considered follow me incoming. Considered so because, when it dials to the followMe number,
                        dialBegin pattern represents outgoing call. So considered as such
                    3) If not found, then call direction is identified
                */
                docID = processAMIDB.getDocIdForPhoneNumberUsingCallDirection(dialBegin.getCallerIDNum(),dialBegin.getEventTime(), mmYYYY, serverNamePrefix, mongoConn);
                if(docID!=null && dialBegin.getConnectedLineNum().contains("FMGL")){
                    callDirectionArr = new String[]{"incoming","Found ConectedLineNum to contain "+dialBegin.getConnectedLineNum()+
                        " FMGL and it was identified as incoming in doc containing ID "+docID};
                    channelToPush = dialBegin.getDestination();
                }
                else{
                    callDirectionArr = new CdrDAO().getCallDirection(channel,destination, null, trunkUsed, serverDetails);
                    reasonArr.add("Got Call direction "+callDirectionArr[0]+" with channel "+channel+" ,destination "+destination+",trunkused "+trunk);
                }
            }
            //End Follow Me Capture
        //Commented By Venky Changes for FollowMe Capture
        /*if(trunkUsed!=null || channel.contains(serverDetails.getQueueContext()) || channel.contains(serverDetails.getTransferContext())){
            if(trunkUsed!=null)
                trunkVal = trunkUsed.getTrunkValue();
            originateReqID = dialBegin.getConnectedLineName();
            if(processAMIDB.checkIfObjectIdIsValid(originateReqID)){
                /*
                    1) Checking if valid objectId
                    2) Considering callDirection to be outgoing, because will get valid objectID only if
                        MissedCall CB and clickAndDial From CRM
                    3) Get details from originateReq
                */
               /* callDirectionArr = new String[]{"outgoing","Found ConectedLineName to contain valid ObjectID "+originateReqID,""};
                detailsFromOriginate = new OriginateCallDao().getDetailsFromOriginateCollForID(originateReqID, serverNamePrefix, mongoConn);
                reasonArr.add("OriginateReqID "+originateReqID+" is found as valid ObjectID. So considered outgoing call");
            }
            else{
                /*
                    1) Not valid ObjectID
                    2) First trying to find phoneNumber in calls. If found and connectedLineNum contains FMGL
                        then considered follow me incoming. Considered so because, when it dials to the followMe number,
                        dialBegin pattern represents outgoing call. So considered as such
                    3) If not found, then call direction is identified
                */
                /*docID = processAMIDB.getDocIdForPhoneNumberUsingCallDirection(dialBegin.getCallerIDNum(),dialBegin.getEventTime(), mmYYYY, serverNamePrefix, mongoConn);
                if(docID!=null && dialBegin.getConnectedLineNum().contains("FMGL")){
                    callDirectionArr = new String[]{"incoming","Found ConectedLineNum to contain "+dialBegin.getConnectedLineNum()+
                        " FMGL and it was identified as incoming in doc containing ID "+docID};
                    reasonArr.add("OriginateReqID "+originateReqID+" is not found as valid ObjectID "
                        + " and connectedLineNum "+dialBegin.getConnectedLineNum()+" contains FMGL and a valid callID "
                        + " was found with callDirection incoming and phoneNumber "+dialBegin.getCallerIDNum()+". So "
                        + " considered incoming");
                }
                else if(channel.contains(serverDetails.getTransferContext())){
                    docID = processAMIDB.getCallIDUsingExtensionForAttendTransferCall(dialBegin.getCallerIDNum(), mmYYYY, serverNamePrefix, mongoConn);
                    if(docID!=null){
                        reasonArr.add("Channel Contains Transfer Context "+serverDetails.getTransferContext());
                        processAMIDB.pushDataIntoArray(docID, "DialBegin",dialBegin.getDocument(), reasonArr, mmYYYY, serverNamePrefix, mongoConn);
                        processAMIDB.pushAllChannelsFoundInCalls(docID, channel, mmYYYY, serverNamePrefix, mongoConn);
                        processAMIDB.pushAllChannelsFoundInCalls(docID, destination, mmYYYY, serverNamePrefix, mongoConn);
                        processAMIDB.pushTransferDetails(docID, dialBegin.getCallerIDNum(), dialBegin.getDialString(), mmYYYY, serverNamePrefix, mongoConn);
                        processAMIDB.pushExtensionInCall(docID, dialBegin.getDialString(), mmYYYY, serverNamePrefix, mongoConn);
                        return;
                    }
                }
                else{
                    callDirectionArr = new CdrDAO().getCallDirection(channel,destination, null, trunkUsed, serverDetails);
                    reasonArr.add("Got Call direction "+callDirectionArr[0]+" with channel "+channel+" destination "+destination+" and trunkUsed "+trunkVal);
                }
            }*/
            switch(callDirectionArr[0]){
                case "incoming":
                    if(serverDetails.getServerType().equals("euprravox"))
                        phoneNumber = dialBegin.getCallerIDNum();
                    else
                        phoneNumber = dialBegin.getCallerIDName();
                    
                    if(destination.contains("FMPR") || destination.contains("FMGL")){
                        extensionOrFollowMe = dialBeginDao.getExtensionOrFollowNumberFromDestination(destination);
                    }
                    else if(dialBegin.getConnectedLineNum().contains("FMGL")){
                        extensionOrFollowMe = dialBeginDao.getExtensionOrFollowNumberUsingConnectedLineNum(dialBegin.getConnectedLineNum());
                    }
                    if(extensionOrFollowMe!=null){
                        UserModel user = new UserDAO().checkExtensionExistInUsers(extensionOrFollowMe, mongoConn);
                        if(user!=null)
                            extension = user.getExtension();
                        
                        //Commented By Venky
                        /*else if(extensionOrFollowMe.length()==4)
                            extension=extensionOrFollowMe;*/
                        
                    }
                    else
                        extension = dialBeginDao.getExtensionFromChannelOrDestination(destination);
                    
                    docID = processAMIDB.getDocIDByQueryingWithChannelAndDestination(channel, destination, eventTime, true,mmYYYY, serverNamePrefix, mongoConn);
                    if(docID==null){
                        docID = processAMIDB.getDocIdForPhoneNumber(phoneNumber, eventTime,mmYYYY,serverNamePrefix,mongoConn);
                        reasonArr.add("Could Not find callID using channel "+channel+" or destination "+destination);
                    }
                    
                    if(docID==null){
                        docID = processAMIDB.insertNewCall(uniqueID, "externalcall", eventTime, "DialBegin", mmYYYY, serverNamePrefix, false, mongoConn);
                        reasonArr.add("Could Not find callID using phoneNumber "+phoneNumber+" with callDirection incoming ");
                        reasonArr.add("Since no record could be found, new call is inserted");
                    }
                    
                    //Added By Venky insert to DB
                    if(docID!=null){
                        reasonArr.add("Found Doc by using channel "+channel+" or destination "+destination+
                            " or by using phoneNumber "+phoneNumber);
                        processAMIDB.pushDataIntoArray(docID,"DialBegin",dialBegin.getDocument(),reasonArr, mmYYYY,serverNamePrefix, mongoConn);
                        processAMIDB.updateCallType(docID, "externalcall", mmYYYY, serverNamePrefix, mongoConn);
                        processAMIDB.updateCallDirection(docID, callDirectionArr[0], mmYYYY, serverNamePrefix, mongoConn);
                        processAMIDB.updatePhoneNumberInDoc(docID, phoneNumber, mmYYYY, serverNamePrefix, mongoConn);
                        processAMIDB.pushAllChannelsFoundInCalls(docID, channel, mmYYYY, serverNamePrefix, mongoConn);
                        processAMIDB.pushAllChannelsFoundInCalls(docID, destination, mmYYYY, serverNamePrefix, mongoConn);
                        
                        new ProcessForReportsDAO(mongoConn, dialBegin.getEventTime(), "Extension Ringing", docID, serverNamePrefix, 
                            "incoming", null,extension,null, mmYYYY,phoneNumber,serverDetails.getServerID(),uniqueID,null).run();
                    }
                    if(docID==null){
                        processAMIDB.insertIntoCurrentEvents(dialBegin.getDocument(), mmYYYY,serverNamePrefix,mongoConn);
                    }
                    break;
                case "outgoing":
                    extension = (detailsFromOriginate==null)?dialBeginDao.getExtensionFromChannelOrDestination(dialBegin.getChannel()):detailsFromOriginate.getUserExtension();
                    phoneNumber = (detailsFromOriginate==null)?dialBeginDao.getPhoneNumberFromDialString(dialBegin.getDialString()):detailsFromOriginate.getPhoneNumber();
                    
                    if(detailsFromOriginate==null){
                        /*
                            1) If detailsFromOriginate is null, then find callID using channel or destination.
                            2) If no docID is retured for the channel or destination then new call record is inserted
                        */
                        docID = processAMIDB.getDocIDByQueryingWithChannelAndDestination(channel, destination, eventTime, true,mmYYYY, serverNamePrefix, mongoConn);
                        if(docID==null){
                            docID = processAMIDB.insertNewCall(uniqueID,"externalcall",dialBegin.getEventTime(),"DialBegin", mmYYYY, serverNamePrefix, false,mongoConn);
                            reasonArr.add("Could not find callID using channel "+channel+" or destination "+destination);
                            reasonArr.add("Since no callID could be found and detailsFromOriginate is null, a new call record is created");
                        }
                        
                        //Added By Venky
                        if(docID!=null){
                            reasonArr.add("Found channel "+channel+" or destination "+destination);
                            processAMIDB.pushDataIntoArray(docID, "DialBegin", dialBegin.getDocument(),reasonArr, mmYYYY,serverNamePrefix,mongoConn);
                            reasonArr.add("updating Call Direction to "+callDirectionArr[0]+",docID="+docID+",mmYYYY="+mmYYYY+",serverNamePrefix="+serverNamePrefix);
                            processAMIDB.updateCallDirection(docID,callDirectionArr[0], mmYYYY,serverNamePrefix,mongoConn);
                            processAMIDB.updateCallType(docID, "externalcall", mmYYYY, serverNamePrefix, mongoConn);
                            if(phoneNumber!=null){
                                processAMIDB.updatePhoneNumberInDoc(docID, phoneNumber, mmYYYY, serverNamePrefix, mongoConn);
                            }
                            processAMIDB.pushAllChannelsFoundInCalls(docID, channel, mmYYYY, serverNamePrefix, mongoConn);
                            processAMIDB.pushAllChannelsFoundInCalls(docID, destination, mmYYYY, serverNamePrefix, mongoConn);
                        }
                    }
                    else{
                        /*
                            1) If detailsFromOriginate IS NOT NULL
                            2) Tring to find callID using originateReqID
                            3) If docID is not found then new call record is inserted and originateReqID is updated in it.
                        */
                        //campaignID = detailsFromOriginate.getCampaignID(); 
                        //Added By Venky
                        campaignID = detailsFromOriginate.getCampaignID();
                        docID = processAMIDB.getCallDocIDUsingOriginateReqID(originateReqID,eventTime, mmYYYY, serverNamePrefix, mongoConn);
                        if(docID!=null){
                            reasonArr.add("Found connectedLineName "+dialBegin.getConnectedLineName()+" was found in document");
                        }
                        else{
                            docID = processAMIDB.insertNewCall(uniqueID, detailsFromOriginate.getTypeOfDialer(), dialBegin.getEventTime(),
                                "DialBegin", mmYYYY, serverNamePrefix, false, mongoConn);
                            processAMIDB.updateOriginateReqIdInCalls(docID, originateReqID, mmYYYY, serverNamePrefix, mongoConn);
                            
                        }
                        processAMIDB.pushDataIntoArray(docID, "DialBegin", dialBegin.getDocument(), reasonArr, mmYYYY, serverNamePrefix, mongoConn);
                        //processAMIDB.updateDerivedChannelIncalls(docID, channel, mmYYYY, serverNamePrefix, mongoConn);
                        processAMIDB.updateCallDirection(docID, "outgoing", mmYYYY, serverNamePrefix, mongoConn);
                        processAMIDB.updatePhoneNumberInDoc(docID, phoneNumber, mmYYYY, serverNamePrefix, mongoConn);
                        processAMIDB.pushAllChannelsFoundInCalls(docID, channel, mmYYYY, serverNamePrefix, mongoConn);
                        processAMIDB.pushAllChannelsFoundInCalls(docID, destination, mmYYYY, serverNamePrefix, mongoConn);
                        //Commented By Venky
                        /*docID = processAMIDB.getCallDocIDUsingOriginateReqID(originateReqID,eventTime, mmYYYY, serverNamePrefix, mongoConn);
                        if(docID!=null){
                            reasonArr.add("Found callID using originateReqID "+dialBegin.getConnectedLineName());
                        }
                        else{
                            docID = processAMIDB.insertNewCall(uniqueID, detailsFromOriginate.getTypeOfDialer(), dialBegin.getEventTime(),
                                "DialBegin", mmYYYY, serverNamePrefix, false, mongoConn);
                            processAMIDB.updateOriginateReqIdInCalls(docID, originateReqID, mmYYYY, serverNamePrefix, mongoConn);
                            reasonArr.add("Unable to find callID using originateReqID "+dialBegin.getConnectedLineName()+". So "
                                + " creating new call record");
                        }
                        CampaignModel campaign = new CampaignDAO().getCampaignDetailsForID(detailsFromOriginate.getCampaignID());
                        if(campaign.getDialMethod().equalsIgnoreCase("MissedCall") || campaign.isCallBackCampaign()){
                            processAMIDB.updateBCCBFlag(docID, mmYYYY, serverNamePrefix, mongoConn);
                        }*/
                    }
                    new ProcessForReportsDAO(mongoConn, dialBegin.getEventTime(), "Client Phone Ringing", docID, 
                        serverNamePrefix,"outgoing", null,extension,null, mmYYYY,phoneNumber,
                        serverDetails.getServerID(),uniqueID,campaignID).run();
                    //valToUpdate = "Client Phone Ringing";
                    break;
            }
            /*
                1) Common to both call direction
                2) Push the data inside call record, update call direction, push the channel and destination found 
                    in call, update phoneNumber and call the function for processing reports
            */
            //Commented By Venky
            /*if(docID!=null){
                processAMIDB.pushDataIntoArray(docID, "DialBegin", dialBegin.getDocument(),reasonArr, mmYYYY,serverNamePrefix,mongoConn);
                processAMIDB.updateCallDirection(docID,callDirectionArr[0], mmYYYY,serverNamePrefix,mongoConn);
                processAMIDB.updateCallType(docID, "External Call", mmYYYY, serverNamePrefix, mongoConn);
                processAMIDB.pushAllChannelsFoundInCalls(docID, channel, mmYYYY, serverNamePrefix, mongoConn);
                processAMIDB.pushAllChannelsFoundInCalls(docID, destination, mmYYYY, serverNamePrefix, mongoConn);
                processAMIDB.pushExtensionInCall(docID, extension, mmYYYY, serverNamePrefix, mongoConn);
                if(phoneNumber!=null)
                    processAMIDB.updatePhoneNumberInDoc(docID, phoneNumber, mmYYYY, serverNamePrefix, mongoConn);
                
                new ProcessForReportsDAO(mongoConn, eventTime, valToUpdate, docID, serverNamePrefix, 
                    callDirectionArr[0], null, extension, null, mmYYYY, phoneNumber, serverDetails.getServerID(), uniqueID, 
                    campaignID).run();
            }
            else
                processAMIDB.insertIntoCurrentEvents(dialBegin.getDocument(), mmYYYY,serverNamePrefix,mongoConn);*/
        }
    }
    
    private void processDialEndEvents(DialEndModel dialEnd){
        /*
        1) Check For UniqueID in DialBegin
        2) If Not Found Check For UniqueID in NewState
        3) If found in any one of the above, push data to matching Doc
        */
        this.addCallClosedInQry = false;
        String docID = null;
        String callStatus = "Missed";
        ArrayList<String> reasonArr = new ArrayList<>();
        String mmYYYY = utilObj.getMMYYYYForJavaDate(dialEnd.getEventTime().getTime());
        String channel = dialEnd.getChannel();
        
        docID = processAMIDB.getDocIDByQueryingWithChannelAndDestination(channel, null, dialEnd.getEventTime(), false,mmYYYY, serverNamePrefix, mongoConn);
        if(docID!=null){
            reasonArr.add("Found callID using channel "+channel);
            processAMIDB.pushDataIntoArray(docID, "DialEnd", dialEnd.getDocument(),reasonArr, mmYYYY,serverNamePrefix,mongoConn);
            
            if(dialEnd.getDialStatus().contains("CONGESTION") || dialEnd.getDialStatus().contains("CHANUNAVAIL")){
                callStatus = "Congestion";
            }
            else{
                String callDirection = processAMIDB.getCallDirectionForId(docID, mmYYYY, serverNamePrefix, mongoConn);
                if(callDirection!=null && callDirection.equals("outgoing") && callStatus.equals("Missed"))
                    callStatus = "No Answer";
            }
            new ProcessForReportsDAO(mongoConn, dialEnd.getEventTime(), callStatus, docID, serverNamePrefix, "End", null,null,null, mmYYYY,
                null,serverDetails.getServerID(),dialEnd.getUniqueID(),null).run();
        }
        else
            processAMIDB.insertIntoCurrentEvents(dialEnd.getDocument(), mmYYYY,serverNamePrefix,mongoConn);
    }
    
    private void processCdrEvents(CdrModel cdr){
        String docID = null;
        ArrayList<String> reasonArr = new ArrayList<>();
        String uniqueID = cdr.getUniqueID();
        String callStatus = null;
        addCallClosedInQry = false;
        String originateReqID = null;
        String phoneNumber = null;
        String extension = null;
        String mmYYYY = utilObj.getMMYYYYForJavaDate(cdr.getEventTime().getTime());
        OriginateRequestModel detailsFromOriginate = null;
        String channel = cdr.getChannel();
        String destinationChannel = cdr.getDestinationChannel();
        
        Trunk trunkUsed = new CdrDAO().getTrunkUsedInCall(channel,destinationChannel, serverDetails.getTrunk());
        
        docID = processAMIDB.getDocIDByQueryingWithChannelAndDestination(channel, destinationChannel, cdr.getEventTime(), false,mmYYYY, serverNamePrefix, mongoConn);
        
        /*
            1) Checking if cdr belong to missedCallCB. Since uniqueID won't match, channel and destinationChannel is used
                to find call document
            2) Since now all calls are identified by using channel and destinationChannel, originateReqID is retrieved
            3) If originateReqID is not null, then details is obtained from originateCall collection
            4) Campaign details is obtained from details obtained from originateCall collection
            5) If trunk in CDR is not null, then cdr is segregated whether its Agent or Customer cdr and retry or 
                MissedCallCB closing is done
            6) If lastApplication is "Congestion" and trunk and details from originate collection is not null, then 
                call is closed
        */
        
        if(docID!=null){
            reasonArr.add("Found channel "+cdr.getChannel()+" in derivedChannel array");
            originateReqID = processAMIDB.getOriginateReqIDUsingCallID(docID, mmYYYY, serverNamePrefix, mongoConn);
            if(originateReqID!=null){
                detailsFromOriginate = new OriginateCallDao().getDetailsFromOriginateCollForID(originateReqID, serverNamePrefix, mongoConn);
                if(detailsFromOriginate!=null){
                    CampaignModel campaign = new CampaignDAO().getCampaignDetailsForID(detailsFromOriginate.getCampaignID());
                    if(campaign.getDialMethod().equalsIgnoreCase("missedcall") || campaign.isCallBackCampaign()){
                        if(trunkUsed!=null)
                            processAMIDB.segregateAndProcessWithTypeOfCDR(cdr, campaign, originateReqID, detailsFromOriginate, mongoConn);
                        if(cdr.getLastApplication().equals("Congestion") && trunkUsed!=null && detailsFromOriginate!=null)
                            processAMIDB.updateCallClosed(docID, mmYYYY, serverNamePrefix, mongoConn);
                    }
                }
            }
        }
        
        /*
            1) This is used to check if the CDR belongs to a missed call given by the customer.
            2) In some cases, we get newState, so first we check if there is a matching document.
                If not found then a new entry is made in calls.
            3) CallType, callDirection is updated and call is closed.
            4) Cdr is pushed into document
        */
        if(new MissedCallDAO().checkIfMissedCallCdr(cdr, mmYYYY, serverDetails.getServerID(), serverNamePrefix, mongoConn)){
            if(docID==null)
                docID = processAMIDB.insertNewCall(uniqueID,"externalcall",cdr.getEventTime(),"Cdr", mmYYYY, serverNamePrefix, true,mongoConn);
            reasonArr.add("Found DID of MissedCall campaign matching with the destination "+cdr.getDestination());
            processAMIDB.pushDataIntoArray(docID, "Cdr", cdr.getDocument(), reasonArr, mmYYYY, serverNamePrefix, mongoConn);
            processAMIDB.updateCallType(docID, "externalcall", mmYYYY, serverNamePrefix, mongoConn);
            processAMIDB.updateCallDirection(docID, "missedCall", mmYYYY, serverNamePrefix, mongoConn);
            processAMIDB.updateCallClosed(docID, mmYYYY, serverNamePrefix,mongoConn);
        }

        if(docID!=null){
            reasonArr.add("Found callID using channel "+channel+" or detinationChannel "+destinationChannel);
            processAMIDB.pushDataIntoArray(docID, "Cdr", cdr.getDocument(),reasonArr, mmYYYY,serverNamePrefix,mongoConn);
            /*
                1) This is used in case of IVR HangUp.
                2) We check if destinationContext contains ivr context.
                3) If so, then call direction is updated as incoming and call is closed
            */
            //Added for Curl Data
            /*
                Changed By Bharath on 23/04/2016
                Added additional checking of campaignDetails to check if it not null before processing API Campaign
            */
            String recordingFilename = null;
            String campaignIDFromCurrentCalls = new ProcessForReportsDB().getCampaignID(docID, mmYYYY, serverNamePrefix, mongoConn);
            CampaignModel campaignDetails = new CampaignDB().getCampaignDetailsUsingID(campaignIDFromCurrentCalls);
            if(campaignDetails!=null && campaignDetails.getCampaignSource().equals("apicampaign"))
            {
                if(cdr.getUserField()!=null && cdr.getUserField().length()>0){
                    recordingFilename = cdr.getUserField();
                    recordingFilename = recordingFilename.replace("audio:", "");
                    recordingFilename = serverDetails.getCurrentRecordingLocation().concat(recordingFilename);
                }
                new CurlDataForAPI(campaignDetails.getCdrURL(), "cdr", null, docID, phoneNumber,callStatus, null, 
                        cdr.getSource(), cdr.getDestination(), cdr.getChannel(),cdr.getDestinationChannel(), cdr.getDestinationContext(),
                        cdr.getUniqueID(), cdr.getStartTime(), cdr.getAnswerTime(), cdr.getEndTime(), cdr.getUserField(), 
                        cdr.getDisposition(), cdr.getDuration(), cdr.getBillableSeconds(),null,null,null,null).curl();
                if(recordingFilename!=null){
                    String callDirection = processAMIDB.getCallDirectionForId(docID, mmYYYY, serverNamePrefix, mongoConn);
                    if(callDirection.equals("incoming"))
                        extension = cdr.getDestination();
                    else
                        extension = cdr.getSource();
                    new CurlDataForAPI(campaignDetails.getResourceURL(), "cdrrecording", extension, docID, null, "recording", null,
                        null, null, null, null, null, null, null, null, null, null, null, 0, 0, null, recordingFilename,null,null).curl();
                }
            }
            //End Curl Data
            if(serverDetails.getIvrContext()!=null)
               if(cdr.getDestinationContext().contains(serverDetails.getIvrContext()) && serverDetails.getIvrContext().length()>0){
                    processAMIDB.updateCallDirection(docID, "incoming", mmYYYY, serverNamePrefix, mongoConn);
                    processAMIDB.updateCallClosed(docID, mmYYYY, serverNamePrefix,mongoConn);
                    callStatus = "IVR Hangup";
            }
            /*
                1) This is used when it is DID direct call and the user is already busy.
                2) In such scenario we check if destination context contains knownIncomingContext
                3) If so callDirection is updated and call is closed
            */
            if(serverDetails.getKnownIncomingContext()!=null)
                if(cdr.getDestinationContext().contains(serverDetails.getKnownIncomingContext()) && 
                        serverDetails.getKnownIncomingContext().length()>0){
                    processAMIDB.updateCallDirection(docID, "incoming", mmYYYY, serverNamePrefix, mongoConn);
                    processAMIDB.updateCallClosed(docID, mmYYYY, serverNamePrefix,mongoConn);
            }
            new ProcessForReportsDAO(mongoConn, null, callStatus, docID, serverNamePrefix,"Cdr", null,null,
                cdr.getUserField(), mmYYYY,null,serverDetails.getServerID(),cdr.getUniqueID(),null).run();
        }
        else
            processAMIDB.insertIntoCurrentEvents(cdr.getDocument(), mmYYYY,serverNamePrefix,mongoConn);
        new CdrDAO().processCdr(serverDetails, cdr, new util(), mongoConn);
    }
    
    private void processBridgeEvents(BridgeModel bridge){
        /*
            1) Check using channel1 and channel2 in channelFoundInCall array
        */
        String docID = null;
        ArrayList<String> reasonArr = new ArrayList<>();
        String mmYYYY = utilObj.getMMYYYYForJavaDate(bridge.getEventTime().getTime());
        
        docID = processAMIDB.getDocIDByQueryingWithChannelAndDestination(bridge.getChannel1(), bridge.getChannel2(), bridge.getEventTime(), true,mmYYYY, serverNamePrefix, mongoConn);
        if(docID!=null){
            reasonArr.add("Found callID using channel1 "+bridge.getChannel1()+" or by using channel2 "+bridge.getChannel2());
            processAMIDB.pushDataIntoArray(docID, "Bridge", bridge.getDocument(),reasonArr, mmYYYY,serverNamePrefix,mongoConn);
            if(bridge.getBridgeState().equalsIgnoreCase("Link")){
                new ProcessForReportsDAO(mongoConn, bridge.getEventTime(), "Answered", docID, serverNamePrefix,"Bridge"
                    ,null,null,null, mmYYYY,null,serverDetails.getServerID(),bridge.getUniqueID1(),null).run();
            }
        }
        else
            processAMIDB.insertIntoCurrentEvents(bridge.getDocument(), mmYYYY,serverNamePrefix,mongoConn);
    }
    
    private void processJoinEvents(JoinModel join){
        /*
            1) Check for channel in channelFoundInCall array
        */
        String docID = null;
        ArrayList<String> reasonArr = new ArrayList<>();
        String mmYYYY = utilObj.getMMYYYYForJavaDate(join.getEventTime().getTime());
        String uniqueID = join.getUniqueID();
        
        docID = processAMIDB.getDocIDByQueryingWithChannelAndDestination(join.getChannel(), null, join.getEventTime(), true,mmYYYY, serverNamePrefix, mongoConn);
        if(docID!=null){
            reasonArr.add("Found callID using channel "+join.getChannel());
            processAMIDB.pushDataIntoArray(docID, "Join", join.getDocument(),reasonArr, mmYYYY,serverNamePrefix,mongoConn);
            processAMIDB.updateCallDirection(docID, "incoming", mmYYYY, serverNamePrefix, mongoConn);
            processAMIDB.pushAllChannelsFoundInCalls(docID, join.getChannel(), mmYYYY, serverNamePrefix, mongoConn);
            new ProcessForReportsDAO(mongoConn, join.getEventTime(), "In Queue", docID, serverNamePrefix,"Join"
                ,join.getQueue(),null,null, mmYYYY,join.getCallerIdNum(),serverDetails.getServerID(),join.getUniqueID(),null).run();
        }
        else
            processAMIDB.insertIntoCurrentEvents(join.getDocument(), mmYYYY,serverNamePrefix,mongoConn);
    }
    
    private void processLeaveEvents(LeaveModel leave){
        /*
            1) Check for channel in channelFoundInCall array
        */
        String docID = null;
        ArrayList<String> reasonArr = new ArrayList<>();
        String mmYYYY = utilObj.getMMYYYYForJavaDate(leave.getEventTime().getTime());
        
        docID = processAMIDB.getDocIDByQueryingWithChannelAndDestination(leave.getChannel(), null, leave.getEventTime(), true,mmYYYY, serverNamePrefix, mongoConn);
        if(docID!=null){
            reasonArr.add("Found callID using channel "+leave.getChannel());
            processAMIDB.pushDataIntoArray(docID, "Leave",leave.getDocument(),reasonArr, mmYYYY,serverNamePrefix,mongoConn);
            new ProcessForReportsDAO(mongoConn, leave.getEventTime(), "Abandon", docID, serverNamePrefix,"Leave",null
                ,null,null, mmYYYY,null,serverDetails.getServerID(),leave.getUniqueID(),null).run();
        }
        if(docID==null)
            processAMIDB.insertIntoCurrentEvents(leave.getDocument(), mmYYYY,serverNamePrefix,mongoConn);
    }
    
    private void processDTMFEvents(DTMFModel dtmf){
        /*
            1) Check for channel in channelFoundInCall array
        */
        String docID = null;
        ArrayList<String> reasonArr = new ArrayList<>();
        String mmYYYY = utilObj.getMMYYYYForJavaDate(dtmf.getEventTime().getTime());
        String uniqueID = dtmf.getUniqueID();
        docID = processAMIDB.getDocIDByQueryingWithChannelAndDestination(dtmf.getChannel(), null, dtmf.getEventTime(), true,mmYYYY, serverNamePrefix, mongoConn);
        if(docID!=null){
            reasonArr.add("Found callID using channel "+dtmf.getChannel());
            processAMIDB.pushDataIntoArray(docID, "DTMF", dtmf.getDocument(),reasonArr, mmYYYY,serverNamePrefix,mongoConn);
        }
        else
            processAMIDB.insertIntoCurrentEvents(dtmf.getDocument(), mmYYYY,serverNamePrefix,mongoConn);
    }
    
    private void processHangUpEvents(HangupModel hangUp){
        
        String docID = null;
        ArrayList<String> reasonArr = new ArrayList<>();
        String mmYYYY = utilObj.getMMYYYYForJavaDate(hangUp.getEventTime().getTime());
        String uniqueID = hangUp.getUniqueID();
        OriginateRequestModel detailsFromOriginate = null;
        UserModel user = null;
        Trunk trunkUsed = null;
        addCallClosedInQry = false;
        String phoneNumber = null;
        boolean validObjectID = false;
        
        String channel = hangUp.getChannel();
        trunkUsed = new CdrDAO().getTrunkUsedInCall(channel, null, serverDetails.getTrunk());
        
        /*
            1) First its is checked whether callerIDName has valid ObjectID
            2) Will be valid only if hangUp of MissedCallCB or clickAndCall
            3) If valid, details are obtained from originateCall collection
            4) User details are retrieved
        */
        if(processAMIDB.checkIfObjectIdIsValid(hangUp.getCallerIdName())){
            validObjectID = true;
            detailsFromOriginate = new OriginateCallDao().getDetailsFromOriginateCollForID(hangUp.getCallerIdName(), serverNamePrefix, mongoConn);
            if(detailsFromOriginate!=null)
                user = new UserDAO().checkExtensionExistInUsers(detailsFromOriginate.getUserExtension(), mongoConn);
        }
        
        /*
            1) Checking if hangUp cause equals CallRejected or causeCode should not be equal to 16 and must contains a 
                valid objectID
            2) Details retrieved from originateCall should not be null
            3) Trunk should not be null and channel must contain client phoneNumber. If so, then considered client side hangUp.
                This is used to update retry for customer(Customer rejected the call directly)
            4) Trunk must be null and channel mush contain SIP/extensionNumber. If so 
                considered agent side hangUp. This is used to update agent retry. If there is no followMeNumber and if 
                agents cut the call, retry is updated here
            5) Below If block is only to update retry for MissedCallCB and campaignType must be missedCall
        */
        
        
        String whichSideEvent = null;   
        /*if((hangUp.getCauseTxt().equals("Call Rejected") || !hangUp.getCause().equals("16")) && validObjectID){
            if(detailsFromOriginate==null)
                return;
            
            CampaignModel campaignDetails = new CampaignDAO().getCampaignDetailsForID(detailsFromOriginate.getCampaignID());
            if(campaignDetails.getDialMethod().equalsIgnoreCase("missedCall")){
                if(trunkUsed!=null && channel.contains(detailsFromOriginate.getPhoneNumber())
                    && serverDetails.getServerType().equalsIgnoreCase("Elastix"))
                    whichSideEvent = "Customer";
                else if(trunkUsed!=null && hangUp.getCallerIdNum().contains(detailsFromOriginate.getPhoneNumber()) 
                    && serverDetails.getServerType().equalsIgnoreCase("Euprravox"))
                    whichSideEvent = "Customer";
                else if(trunkUsed==null && (channel.contains("SIP/".concat(detailsFromOriginate.getUserExtension())) || 
                    channel.contains("IAX2/".concat(detailsFromOriginate.getUserExtension()))) && 
                        serverDetails.getServerType().equalsIgnoreCase("Elastix"))
                    whichSideEvent = "Agent";
                
                new SMSDao().insertMissedCallsmsData(detailsFromOriginate.getPhoneNumber(),whichSideEvent,campaignDetails,"NotAnswered",serverNamePrefix);
                new MissedCallDAO().updateRetriesAndStatusInMissedCall(campaignDetails,whichSideEvent,hangUp.getCallerIdName(),detailsFromOriginate,mongoConn);
            }
        }*/
        if((hangUp.getCauseTxt().equals("Call Rejected") || !hangUp.getCause().equals("16")) && validObjectID){
            if(detailsFromOriginate==null)
                return;
            CampaignModel campaignDetails = new CampaignDAO().getCampaignDetailsForID(detailsFromOriginate.getCampaignID());
            if(campaignDetails.getDialMethod().equalsIgnoreCase("missedCall")){
                if(trunkUsed!=null && channel.contains(detailsFromOriginate.getPhoneNumber())
                    && serverDetails.getServerType().equalsIgnoreCase("Elastix"))
                    whichSideEvent = "Customer";
                else if(trunkUsed!=null && hangUp.getCallerIdNum().contains(detailsFromOriginate.getPhoneNumber()) 
                    && serverDetails.getServerType().equalsIgnoreCase("Euprravox"))
                    whichSideEvent = "Customer";
                else if(trunkUsed==null && (channel.contains("SIP/".concat(detailsFromOriginate.getUserExtension())) || 
                    channel.contains("IAX2/".concat(detailsFromOriginate.getUserExtension()))) && 
                        serverDetails.getServerType().equalsIgnoreCase("Elastix"))
                    whichSideEvent = "Agent";
                
                new SMSDao().insertMissedCallsmsData(detailsFromOriginate.getPhoneNumber(),whichSideEvent,campaignDetails,"NotAnswered",serverNamePrefix);
                new MissedCallDAO().updateRetriesAndStatusInMissedCall(campaignDetails,whichSideEvent,hangUp.getCallerIdName(),detailsFromOriginate,mongoConn);
            }
        }
        
        if(detailsFromOriginate!=null)
            if(serverDetails.getServerType().equalsIgnoreCase("Euprravox"))
            {
                String followNumber = processAMIDB.getFollowMenumberusingExtension(detailsFromOriginate.getUserExtension(), mongoConn);
                CampaignModel campaignDetails = new CampaignDAO().getCampaignDetailsForID(detailsFromOriginate.getCampaignID());
                if(campaignDetails.getDialMethod().equalsIgnoreCase("missedCall")){
                    if(hangUp.getCallerIdNum().contains(detailsFromOriginate.getPhoneNumber()))
                    {
                        whichSideEvent = "Customer";
                        boolean bridgeEventExist = processAMIDB.checkBridgeEventForClientNumberFromHangUp(detailsFromOriginate.getPhoneNumber(), mmYYYY, serverNamePrefix);
                        if(bridgeEventExist)
                            new SMSDao().insertMissedCallsmsData(detailsFromOriginate.getPhoneNumber(),whichSideEvent,campaignDetails,"callAnswered",serverNamePrefix);
                        else
                            new SMSDao().insertMissedCallsmsData(detailsFromOriginate.getPhoneNumber(),whichSideEvent,campaignDetails,"NotAnswered",serverNamePrefix);
                    }
                    else if(followNumber!=null && hangUp.getCallerIdNum().contains(followNumber))
                        whichSideEvent = "Agent";
                    
                    if(whichSideEvent.equals("Agent") && trunkUsed!=null)
                    {
                       new SMSDao().insertMissedCallsmsData(detailsFromOriginate.getPhoneNumber(),whichSideEvent,campaignDetails,"NotAnswered",serverNamePrefix);
                       new MissedCallDAO().updateRetriesAndStatusInMissedCall(campaignDetails,whichSideEvent,hangUp.getCallerIdName(),detailsFromOriginate,mongoConn);
                    }
                }
                /*ArrayList<CampaignModel> allMissedCallCampaigns = processAMIDB.getAllMissedCallCampaigns(mongoConn);
                for(CampaignModel campaignDetails:allMissedCallCampaigns)
                {
                    new MissedCallDB().updateMissedCallReqStatus(campaignDetails.getListID(),hangUp.getCallerIdName(),null, "Completed","Euprravox hangup",mongoConn);
                }*/
            }
        
        docID = processAMIDB.getDocIDByQueryingWithChannelAndDestination(channel, null, hangUp.getEventTime(), false,mmYYYY, serverNamePrefix, mongoConn);
        if(docID!=null){
            reasonArr.add("Found callID using channel "+channel);
            processAMIDB.pushDataIntoArray(docID, "HangUp", hangUp.getDocument(),reasonArr, mmYYYY,serverNamePrefix,mongoConn);
            /*String campaignID = new ProcessForReportsDB().getCampaignID(docID, mmYYYY, serverNamePrefix, mongoConn);
                if(campaignID!=null){
                    CampaignModel apiCampaign = new CampaignDB().getCampaignDetailsUsingID(campaignID);
                    String extension = new ProcessForReportsDAO().getCurrentExtensionFromDoc(docID, mmYYYY, serverNamePrefix, mongoConn);
                    if(apiCampaign.getCampaignSource().equals("apicampaign"))
                        new CurlDataForAPI(apiCampaign.getResourceURL(), "hangup", extension, docID, null, "HangUp",
                            null, null, null, null, null, null, null, null, null, null, null, null, 0, 0,hangUp.getCause(),null,null,null).curl();
            }*/
            /*
                1) If trunkUsed is not null and channel start with the trunk identified, then considered as trunk hangUp
                2) If user is not null and user has a followMe, then we check if the hangUp is for SIP extension and not
                    for customer phoneNumber. If it is for SIP, then we do not close the call
            */
            if(trunkUsed!=null && channel.startsWith(trunkUsed.getTrunkValue())){
                if(user!=null && user.isIsFollowMe() && serverDetails.getServerType().equalsIgnoreCase("Elastix")){
                    if(!hangUp.getCallerIdNum().contains(user.getFollowNumber()))
                        return;
                }
                //processAMIDB.checkIfCallToBeClosed(docID, uniqueID, mmYYYY, serverNamePrefix, hangUp.getEventTime(),mongoConn);
                processAMIDB.updateCallClosed(docID, mmYYYY, serverNamePrefix,mongoConn);
                String campaignID = new ProcessForReportsDB().getCampaignID(docID, mmYYYY, serverNamePrefix, mongoConn);
                if(campaignID!=null){
                    CampaignModel campaign = new CampaignDB().getCampaignDetailsUsingID(campaignID);
                    String extension = new ProcessForReportsDAO().getCurrentExtensionFromDoc(docID, mmYYYY, serverNamePrefix, mongoConn);
                    if(campaign.getCampaignSource().equals("apicampaign"))
                        new CurlDataForAPI(campaign.getResourceURL(), "hangup", extension, docID, null, "HangUp",
                            null, null, null, null, null, null, null, null, null, null, null, null, 0, 0,hangUp.getCause(),null,null,null).curl();
                }
                new ProcessForReportsDAO(mongoConn, hangUp.getEventTime(), hangUp.getCauseTxt(), docID, serverNamePrefix,
                    "Hangup",null,null,null, mmYYYY,null,serverDetails.getServerID(),hangUp.getUniqueID(),null).run();
            }
            
            
            /*
                Added By Bharath on 17/04/2016
                Added to integrate both BCCB and MCCB into one
                First it is check whether it is BCCB Call
                If it is not BCCB call, then MCCB is triggered
                BCCB - Incoming campaign with "IscallBackCampaign" option ticked. 
                Incoming call which is hops to the last sim and gets disconnected because all other sim
                are busy is treated as BCCB
                MCCB - Customer directly contacts to a number and call gets disconnected
                
                BCCB Current understanding - Will work only if queue exists and is elastix
                Because we are checking if the call is a incoming call. Call direction is identified
                only in DialBegin or in Join. If no queue is there we do not get such events, to identify
                call direction. So it goes into MCCB logic
            
            */
            /*
                1) Checking if hangUp cause equals CallRejected or causeCode should not be equal to 16 and must contains a 
                    valid objectID
                2) Details retrieved from originateCall should not be null
                3) Trunk should not be null and channel must contain client phoneNumber. If so, then considered client side hangUp.
                    This is used to update retry for customer(Customer rejected the call directly)
                4) Trunk must be null and channel mush contain SIP/extensionNumber. If so 
                    considered agent side hangUp. This is used to update agent retry. If there is no followMeNumber and if 
                    agents cut the call, retry is updated here
                5) Below If block is only to update retry for MissedCallCB and campaignType must be missedCall
            */
            
            
            
            if(!new ProcessForReportsDAO().checkForBCCBAndInsert(docID,mmYYYY,serverNamePrefix,mongoConn)){
                if(new MissedCallDAO().checkIfMissedCallHangUp(hangUp, mmYYYY, serverDetails.getServerID(), serverNamePrefix, mongoConn)){
                    //if(docID==null)
                    //  docID = processAMIDB.insertNewCall(uniqueID,"externalcall",hangUp.getEventTime(),"HangUp", mmYYYY, serverNamePrefix, true,mongoConn);
                    docID = processAMIDB.getDocIDByQueryingWithChannelAndDestination(channel, null, hangUp.getEventTime(), false,mmYYYY, serverNamePrefix, mongoConn);
                    reasonArr.add("Found trunk of MissedCall campaign matching with the channel "+hangUp.getChannel());
                    processAMIDB.pushDataIntoArray(docID, "HangUp", hangUp.getDocument(), reasonArr, mmYYYY, serverNamePrefix, mongoConn);
                    processAMIDB.updateCallType(docID, "externalcall", mmYYYY, serverNamePrefix, mongoConn);
                    processAMIDB.updateCallDirection(docID, "missedCall", mmYYYY, serverNamePrefix, mongoConn);
                    processAMIDB.updateCallClosed(docID, mmYYYY, serverNamePrefix, mongoConn);
                }
            }
        }
        if(docID==null)
            processAMIDB.insertIntoCurrentEvents(hangUp.getDocument(), mmYYYY,serverNamePrefix,mongoConn);
    }
    
    private void processTransferEvents(TransferModel transfer){
        /*
            1) Check for channel in channelFoundInCall array
        */
        String docID = null;
        String transferedFrom = null;
        ArrayList<String> reasonArr = new ArrayList<>();
        String mmYYYY = utilObj.getMMYYYYForJavaDate(transfer.getEventTime().getTime());
        
        docID = processAMIDB.getDocIDByQueryingWithChannelAndDestination(transfer.getChannel(), null, transfer.getEventTime(), true,mmYYYY, serverNamePrefix, mongoConn);
        if(docID!=null){
            reasonArr.add("Found callID using channel "+transfer.getChannel());
            processAMIDB.pushDataIntoArray(docID, "Transfer",transfer.getDocument(),reasonArr, mmYYYY,serverNamePrefix,mongoConn);
            transferedFrom = new DialBeginDAO().getExtensionFromChannelOrDestination(transfer.getChannel());
            
            //Added By Venky For Transfer API
            String campaignID = new ProcessForReportsDB().getCampaignID(docID, mmYYYY, serverNamePrefix, mongoConn);
                if(campaignID!=null){
                    CampaignModel campaign = new CampaignDB().getCampaignDetailsUsingID(campaignID);
                    if(campaign.getCampaignSource().equals("apicampaign"))
                        if(transferedFrom!=null && transfer.getTransferExten()!=null)
                            new CurlDataForAPI(campaign.getResourceURL(), "transfer", null, docID, null, "Transfer",
                                null, null, null, null, null, null, null, null, null, null, null, null, 0, 0, null, null,
                                transferedFrom,transfer.getTransferExten()).curl();
            }
            processAMIDB.pushTransferChannelInCalls(docID, transfer.getTargetChannel(), mmYYYY, serverNamePrefix, mongoConn);
            /*
                1) If transfer type is attended,then move events from processed collection into live collection for
                    processing again
            */
            if(transfer.getTranferType().equals("Attended")){
                processAMIDB.moveEventsFromProcessedToLiveCollection(transfer.getTargetChannel(), transfer.getEventTime(), serverNamePrefix, mongoConn);
            }
            
            new ProcessForReportsDAO(mongoConn, transfer.getEventTime(), transferedFrom, docID, serverNamePrefix,"Transfer",null,null,null, 
                mmYYYY,null,serverDetails.getServerID(),transfer.getUniqueID(),null).run();
        }
        else
            processAMIDB.insertIntoCurrentEvents(transfer.getDocument(), mmYYYY,serverNamePrefix,mongoConn);
    }
    
    private void processHoldEvents(HoldModel hold){
        /*
            1) Check for channel in channelFoundInCall array
        */
        String docID = null;
        ArrayList<String> reasonArr = new ArrayList<>();
        String mmYYYY = utilObj.getMMYYYYForJavaDate(hold.getEventTime().getTime());
        String uniqueID = hold.getUniqueID();
        docID = processAMIDB.getDocIDByQueryingWithChannelAndDestination(hold.getChannel(), null, hold.getEventTime(), true,mmYYYY, serverNamePrefix, mongoConn);
        if(docID!=null){
            reasonArr.add("Found callID using channel "+hold.getChannel());
            processAMIDB.pushDataIntoArray(docID, "Hold", hold.getDocument(),reasonArr, mmYYYY,serverNamePrefix,mongoConn);
        }
        else
            processAMIDB.insertIntoCurrentEvents(hold.getDocument(), mmYYYY,serverNamePrefix,mongoConn);
    }
    
    private void processQueueCallerAbandonEvents(QueueCallerAbandonModel queueCallerAbandon){
        /*
            1) Check for uniqueID in newState document
        */
        String docID = null;
        ArrayList<String> reasonArr = new ArrayList<>();
        String mmYYYY = utilObj.getMMYYYYForJavaDate(queueCallerAbandon.getEventTime().getTime());
        String uniqueID = queueCallerAbandon.getUniqueID();
        
        docID = processAMIDB.checkForUniqueIDInNewState(uniqueID, null, queueCallerAbandon.getEventTime(),mmYYYY, serverNamePrefix, mongoConn);
        if(docID!=null){
            reasonArr.add("Found uniqueID "+queueCallerAbandon.getUniqueID()+" in NewStateArr");
            processAMIDB.pushDataIntoArray(docID, "QueueCallerAbandon", queueCallerAbandon.getDocument(),reasonArr, mmYYYY,serverNamePrefix,mongoConn);
        }
        else
            processAMIDB.insertIntoCurrentEvents(queueCallerAbandon.getDocument(), mmYYYY,serverNamePrefix,mongoConn);
    }
    
    private void processExtensionStatusEvents(ExtensionStatus extStatus){
        processAMIDB.updateUserExtensionStatus(extStatus.getExten(),extStatus.getStatus(),serverDetails.getServerID(), mongoConn);
        AdditionalSettings settings = new AdditionalSettingsDao().getCurrentSettings();
        new CurlDataToURL(settings.getPopupURL(),extStatus.getExten(), "XYZUPDATEEXTENSIONSTATUS","popup","","","","").run();
        //new CurlDataToURL(extStatus.getExten(), "XYZUPDATEEXTENSIONSTATUS","livemonitoring").run();
    }
    
    
    private void processCoreShowChannelEvents(CoreShowChannelModel coreShowChannel){
        String docID = processAMIDB.getDocIDToAppendCoreShowChannel(serverNamePrefix, mongoConn);
        if(docID==null){
            docID = processAMIDB.insertNewCoreShowChannelDoc(serverNamePrefix, utilObj, mongoConn);
        }
        try{
            processAMIDB.pushChannelsInCoreShowCollection(docID, coreShowChannel.getChannel(), serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessEvents", "processCoreShowChannel", ex.getMessage());
        }
    }
    
    private void processCoreShowChannelsCompleteEvents(CoreShowChannelsCompleteModel coreShowChannelsComplete){
        String mmYYYY = utilObj.getMMYYYYForJavaDate(coreShowChannelsComplete.getEventTime().getTime());
        String docID = processAMIDB.getDocIDToAppendCoreShowChannel(serverNamePrefix, mongoConn);
        if(docID!=null){
            processAMIDB.updateEventCompletionInCoreShowChannel(docID, serverNamePrefix, mongoConn);
        }
        new ProcessForReportsDAO(mongoConn, coreShowChannelsComplete.getEventTime(), null, docID, serverNamePrefix, "CoreShowChannelsComplete", null, null, null, mmYYYY, 
                null, serverDetails.getServerID(), null, null).run();
    }
    
    public void processPeerStatusEvents(PeerStatusModel peerStatus){
        String ipAddress = null;
        String extension = new PeerStatusDAO().getExtensionFromPeer(peerStatus.getPeer());
        if(peerStatus.getAddress()!=null)
            ipAddress = new PeerStatusDAO().getRegisteredIpAddress(peerStatus.getAddress());
        new UserDAO().updatePeerStatusAndRegisteredAddressInUsersUsingExtension(extension, ipAddress, peerStatus.getPeerStatus(), mongoConn);
    }
}
