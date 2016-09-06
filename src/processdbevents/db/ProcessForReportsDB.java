/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.db;

import campaignconfig.db.CampaignDB;
import campaignconfig.model.CampaignModel;
import campaignconfig.model.UserModel;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.phonebridgecti.dao.CampaignDAO;
import com.phonebridgecti.dao.CtiDAO;
import singleton.db.DBClass;
import phonebridge.util.LogClass;
import phonebridge.util.util;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import phonebridge.missedcall.db.MissedCallDB;
import processdbevents.dao.BridgeDAO;
import processdbevents.dao.JoinDAO;
import processdbevents.dao.NewStateDAO;
import processdbevents.model.BridgeModel;
import processdbevents.model.CdrModel;
import processdbevents.model.NewStateModel;

/**
 *
 * @author bharath
 */
public class ProcessForReportsDB {
    public void updateEventTime(String docID,String fieldName,Date eventTime,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document(fieldName, eventTime);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updateEventTime", mexc.getMessage());
        }
    }
    
    public void updateQueueNumber(String docID,String queueNumber,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("queueNumber", queueNumber);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updateQueueNumber", mexc.getMessage());
        }
    }
    
    public void updateTypeOfIncoming(String docID,String typeOfIncoming,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            whereQry.append("typeOfIncoming", null);
            Document updateQry = new Document("typeOfIncoming", typeOfIncoming);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updateTypeOfIncoming", mexc.getMessage());
        }
    }
    
    public void updateIfTransfered(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("isTransfered", true);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updateIfTransfered", mexc.getMessage());
        }
    }
    
    public void updateTranserDetails(String docID,String fromExtension,Date transferTime,String mmYYYY,String serverNamePrefix,
        MongoClient mongoConn){
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document transferDet = new Document("transferedFrom", fromExtension);
            transferDet.append("transferedTo", null);
            transferDet.append("transferTime", transferTime);
            Document updateQry = new Document("$push", new Document("TransferDetails", transferDet));
            collection.updateOne(whereQry, updateQry);
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updateTranserDetails", mexc.getMessage());
        }
    }
    
    public void updateQueueWaitTime(String docID,Date timeToCalculateWith,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            Date queueJoinTime = new JoinDAO().getQueueJoinTime(docID,mmYYYY, serverNamePrefix, mongoConn);
            if(queueJoinTime!=null){
                //Long callAnsweredTime = timeToCalculateWith.getTime();
                double secondsInQueue = new util().returnDiffBtwDateInSecsOrMinsOrHrs(timeToCalculateWith, queueJoinTime, "secs");//(callAnsweredTime - queueJoinTime)/1000;
                MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
                Document whereQry = new Document("_id", new ObjectId(docID));
                whereQry.append("queueWaitTime", null);
                whereQry.append("callDirection", "incoming");
                Document updateQry = new Document("$set", new Document("queueWaitTime", secondsInQueue));
                collection.updateOne(whereQry,updateQry);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updateQueueWaitTime", mexc.getMessage());
        }
    }
    
    public void updateExtension(String docID,String extensionToAppend,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("extension",extensionToAppend);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "appendExtension", mexc.getMessage());
        }
    }
    
    public void updateCurrentExtension(String docID,String extensionToAppend,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("currentExtension",extensionToAppend);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "appendExtension", mexc.getMessage());
        }
    }
    
    public void pushHoppingDetails(String docID,String extension,Date eventTime,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("extension", extension);
            updateQry.append("hoppedTime", eventTime);
            collection.updateOne(whereQry, new Document("$push", new Document("hoppingDetails",updateQry)));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "pushHoppingDetails", mexc.getMessage());
        }
    }
    
    public void updateCallStatus(String docID,String callStatus,String checkForCondition,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            switch(checkForCondition){
                case "End":
                    whereQry.append("callStatus", new Document("$ne", "Answered"));
                    break;
                case "Leave":
                    whereQry.append("callStatus", "In Queue");
                    break;
                case "Cdr":
                    whereQry.append("callStatus", null);
                    break;
                case "HangUp":
                    whereQry.append("callStatus", null);
                    break;
            }
            Document updateQry = new Document("callStatus", callStatus);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updateCallStatus", mexc.getMessage());
        }
    }
    
    public void updateHangUpReason(String docID,String hangUpReason,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("hangUpReason", hangUpReason);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updateHangUpReason", mexc.getMessage());
        }
    }
    
    private ArrayList<String> getDistinctBridgeIdForDocID(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        ArrayList<String> bridgeUniqueID = new ArrayList<>();
        try{
            Document whereQry = new Document();
            whereQry.append("_id", new ObjectId(docID));
            whereQry.append("Bridge", new Document("$elemMatch", new Document("bridgeState", "Link")));
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            Document query = new Document("distinct", mmYYYY.concat("_").concat(serverNamePrefix.concat("_currentCalls")));
            query.append("key", "Bridge.uniqueID1");
            query.append("query", whereQry);
            Document resultDoc = mongoDB.executeCommand(query);
            ArrayList<String> retrievedVal = (ArrayList) resultDoc.get("values");
            for(String nextVal : retrievedVal){
                bridgeUniqueID.add(nextVal);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "getDistinctBridgeIdForDocID", mexc.getMessage());
        }
        return bridgeUniqueID;
    }
    
    private String getRecordingFileNamesForCall(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String recordingFileNames = null;
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document selectQry = new Document("recordings", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                recordingFileNames = dc.getString("recordings");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "getRecordingFileNamesForCall", mexc.getMessage());
        }
        return recordingFileNames;
    }
    
    public void updateRecordingFileNames(String docID,String recordingFileName,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            /*String recordings = this.getRecordingFileNamesForCall(docID,mmYYYY, serverNamePrefix, mongoConn);
            if(recordings==null){
                recordings = recordingFileName;
            }
            else{
                recordings = recordings.concat(",").concat(recordingFileName);
            }*/
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("recordings", recordingFileName);
            collection.updateOne(whereQry, new Document("$addToSet", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updateRecordingFileNames", mexc.getMessage());
        }
    }
    
    public double updateAnswerDuration(String docID,Date timeToCalculateWith,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        double answerDuration = 0;
        try{
            BridgeModel bridge = new BridgeDAO().getBridgeDataForDB(docID,mmYYYY,serverNamePrefix, mongoConn);
            if(bridge!=null){
                answerDuration = new util().returnDiffBtwDateInSecsOrMinsOrHrs(timeToCalculateWith, bridge.getEventTime(), "secs");//(callEndTime - bridgeTime)/1000;
                MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
                Document whereQry = new Document("_id", new ObjectId(docID));
                whereQry.append("answerDuration", 0);
                Document updateQry = new Document("$set", new Document("answerDuration", answerDuration));
                collection.updateOne(whereQry,updateQry);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updateAnswerDuration", mexc.getMessage());
        }
        return answerDuration;
    }
    
    private String getCallUniqueIDFromDoc(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String callUniqueID = null;
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            whereQry.append("callDirection", "incoming");
            Document selectQry = new Document("callUniqueID", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                callUniqueID = dc.getString("callUniqueID");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "getCallUniqueIDFromDoc", mexc.getMessage());
        }
        return callUniqueID;
    }
    
    public Date updateIvrEntryTime(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        Date ivrEntry = null;
        try{
            String uniqueID = this.getCallUniqueIDFromDoc(docID,mmYYYY, serverNamePrefix, mongoConn);
            if(uniqueID!=null){
                NewStateModel newState = new NewStateDAO().getNewStateDataFromDB(docID, uniqueID,mmYYYY,serverNamePrefix, mongoConn);
                MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
                Document whereQry = new Document("_id", new ObjectId(docID));
                whereQry.append("callDirection", "incoming");
                whereQry.append("ivrEntryTime", null);
                Document updateQry = new Document("ivrEntryTime", newState.getEventTime());
                collection.updateOne(whereQry, new Document("$set", updateQry));
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updateIvrEntryTime", mexc.getMessage());
        }
        return ivrEntry;
    }
    
    private ObjectId checkIfDocExistsForQueue(String queueNumber,MongoClient mongoConn){
        ObjectId queueDocID = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("currentqueuestatus");
            Document whereQry = new Document("queueNumber",queueNumber);
            Document selectQry = new Document("_id",1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                queueDocID = dc.getObjectId("_id");
            }
            else{
                queueDocID = new ObjectId();
                Document insertDoc = new Document("_id", queueDocID);
                insertDoc.append("queueNumber", queueNumber);
                insertDoc.append("deleted", 0);
                insertDoc.append("noOfCallsInQueue", 0);
                collection.insertOne(insertDoc);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "checkIfDocExistsForQueue", mexc.getMessage());
        }
        return queueDocID;
    }
    
    public String getQueueNumberFromDoc(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String queueNumber = null;
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            whereQry.append("callDirection", "incoming");
            Document selectQry = new Document("queueNumber", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                queueNumber = dc.getString("queueNumber");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "getQueueNumberFromDoc", mexc.getMessage());
        }
        return queueNumber;
    }
    
    public void updateCntInQueue(String docID,String queueNumber,String incOrDec,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            if(queueNumber==null){
                queueNumber = this.getQueueNumberFromDoc(docID,mmYYYY,serverNamePrefix, mongoConn);
            }
            if(queueNumber!=null){
                ObjectId queueDocID = this.checkIfDocExistsForQueue(queueNumber, mongoConn);
                MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
                MongoCollection collection = mongoDB.getCollection("currentqueuestatus");
                Document whereQry = new Document("_id", queueDocID);
                Document updateQry = new Document();
                switch(incOrDec){
                    case "inc":
                        updateQry.append("noOfCallsInQueue", 1);
                        break;
                    case "dec":
                        updateQry.append("noOfCallsInQueue", -1);
                        break;
                }
                collection.updateOne(whereQry, new Document("$inc", updateQry));
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updateCntInQueue", mexc.getMessage());
        }
    }
    
    public void updateTypeOfIncomingInHangup(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            whereQry.append("callDirection", "incoming");
            whereQry.append("Join", new Document("$size",0));
            MongoCursor cursor = collection.find(whereQry).iterator();
            if(cursor.hasNext()){
                this.updateTypeOfIncoming(docID, "DID Direct",mmYYYY,serverNamePrefix, mongoConn);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updateTypeOfIncomingInHangup", mexc.getMessage());
        }
    }
    
    public String getExtensionFromDoc(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String extension = null;
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document selectQry = new Document("extension", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                extension = dc.getString("extension");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "getExtensionFromDoc", mexc.getMessage());
        }
        return extension;
    }
    
    public String getCurrentExtensionFromDoc(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String extension = null;
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document selectQry = new Document("currentExtension", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                extension = dc.getString("currentExtension");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "getCurrentExtensionFromDoc", mexc.getMessage());
        }
        return extension;
    }
    
    public void updatePopupStatus(String extension,String serverID,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("users");
            Document whereQry = new Document("serverID", new ObjectId(serverID));
            whereQry.append("userExtension", extension);
            Document updateQry = new Document("popupStatus", "ACW");
            updateQry.append("popupStatusUpdateTime", new util().generateNewDateInYYYYMMDDFormat("UTC"));
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updatePopupStatus", mexc.getMessage());
        }
    }
    
    
    
    public void updateCallConnectTime(String docID,Date connectTime,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            whereQry.append("callConnectTime", null);
            Document updateQry = new Document("callConnectTime", connectTime);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updateCallConnectTime", mexc.getMessage());
        }
    }
    
    public Date getCallStartTime(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        Date callStartTime = null;
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document selectQry = new Document("eventDateTime", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                callStartTime = dc.getDate("eventDateTime");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "getCallStartTime", mexc.getMessage());
        }
        return callStartTime;
    }
    
    public void updateCallConnectTimeInSecs(String docID,Date connectTime,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            Date callStartTime = this.getCallStartTime(docID, mmYYYY, serverNamePrefix, mongoConn);
            if(callStartTime!=null){
                double connectTimeSecs = new util().returnDiffBtwDateInSecsOrMinsOrHrs(connectTime, callStartTime, "secs");
                MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
                Document whereQry = new Document("_id", new ObjectId(docID));
                whereQry.append("callConnectTimeInSecs", 0);
                Document updateQry = new Document("callConnectTimeInSecs",connectTimeSecs);
                collection.updateOne(whereQry, new Document("$set", updateQry));
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updateCallConnectTimeInSecs", mexc.getMessage());
        }
    }
    
    public String getCampaignID(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String campaignID = null;
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document selectQry = new Document("campaignID", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                campaignID = dc.getObjectId("campaignID").toString();
            }
        }
        catch(MongoException | NullPointerException mexc){
            LogClass.logMsg("ProcessForReportsDB", "getCampaignID", mexc.getMessage());
        }
        return campaignID;
    }
    
    public void updateUserNameAndID(String docID,UserModel userDetails,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document();
            try{
                updateQry.append("userName", userDetails.getName());
            }
            catch(NullPointerException nexc){
                LogClass.logMsg("ProcessForReportsDB", "updateUserNameAndID", nexc.getMessage());
            }
            try{
                updateQry.append("userID", new ObjectId(userDetails.getUserID()));
            }
            catch(NullPointerException nexc){
                LogClass.logMsg("ProcessForReportsDB", "updateUserNameAndID", nexc.getMessage());
            }
            try{
                updateQry.append("branchName", userDetails.getBranchName());
            }
            catch(NullPointerException nexc){
                LogClass.logMsg("ProcessForReportsDB", "updateUserNameAndID", nexc.getMessage());
            }
            try{
                updateQry.append("branchID", new ObjectId(userDetails.getBranchID()));
            }
            catch(Exception ex){
                LogClass.logMsg("ProcessForReportsDB", "updateUserNameAndID", ex.getMessage());
            }
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "updateUserNameAndID", mexc.getMessage());
        }
    }
    
    /*
        Get all channels of open calls
    */
    public ArrayList<String> getChannelsOfOpenCalls(String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        ArrayList<String> channelInCalls = new ArrayList<>();
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY,serverNamePrefix, mongoConn);
            Document whereQry = new Document("isClosed", false);
            Document selectQry = new Document("trunkChannelsInCall", 1);
            MongoCursor<Document> cursor = collection.find(whereQry).iterator();
            while(cursor.hasNext()){
                Document dc = cursor.next();
                //channelInCalls.add(dc.getString("trunkChannelsInCall"));
                ArrayList<String> channelsFromCall = (ArrayList<String>) dc.get("trunkChannelsInCall");
                for(String eachChannel : channelInCalls){
                    channelInCalls.add(eachChannel);
                }
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "getChannelsOfOpenCalls", mexc.getMessage());
        }
        return channelInCalls;
    }
    
    /*
        Get channel array from coreShowChannel collection
    */
    public ArrayList<String> getChannelsFromCoreShowCollection(String docID,String serverNamePrefix,MongoClient mongoConn){
        ArrayList<String> channelInCoreShow = new ArrayList<>();
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix.concat("_coreShowChannel"));
            Document whereQry = new Document("_id", new ObjectId(docID));
            MongoCursor<Document> cursor = collection.find(whereQry).iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                channelInCoreShow = (ArrayList<String>) dc.get("channels");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "getChannelsFromCoreShowCollection", mexc.getMessage());
        }
        return channelInCoreShow;
    }
    
    /*
        Get callID for channel
    */
    public HashMap<String,String> getCallIDForChannel(String channel,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        HashMap<String,String> callDetails = new HashMap<>();
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY,serverNamePrefix, mongoConn);
            Document whereQry = new Document("isClosed", false);
            whereQry.append("trunkChannelsInCall", channel);
            Document selectQry = new Document("_id", 1);
            selectQry.append("typeOfDialer", 1);
            MongoCursor<Document> cursor = collection.find(whereQry).iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                callDetails.put("callID",dc.getObjectId("_id").toString());
                callDetails.put("typeOfDialer",dc.getString("typeOfDialer"));
            }
        }
        catch(MongoException mexc ){
            LogClass.logMsg("ProcessAMIDB", "getChannelsFromCoreShowCollection", mexc.getMessage());
        }
        return callDetails;
    }
    
    /*
        update isClosed to true when channel does not exists
    */
    public void updateCallClosedIfChannelNotExists(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY,serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("isClosed", true);
            updateQry.append("closedTime", new util().generateNewDateInYYYYMMDDFormat());
            updateQry.append("closedSinceChannelDidNotExist", true);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "updateCallClosedIfChannelNotExists", mexc.getMessage());
        }
    }
    
    /*
        Delete record from coreShow collection after processing
    */
    public void deleteRecordFromCoreShowCollection(String docID,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection<Document> collection = mongoDB.getCollection(serverNamePrefix.concat("_coreShowChannel"));
            Document whereQry = new Document("_id", new ObjectId(docID));
            collection.deleteOne(whereQry);
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "deleteRecordFromCoreShowCollection", mexc.getMessage());
        }
    }
    
    public boolean checkForBCCBAndInsert(String callId,String mmYYYY,String serverPrefix,MongoClient mongoConn){
        boolean bccbCall = false;
        util utilObj = new util();
        ArrayList<String> bccbLogic = new ArrayList<>();
        bccbLogic.add("Checking for callID ".concat(callId));
        MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
        MongoCollection collection = mongoDB.getCollection(utilObj.returnCollectionNameForCurrentCalls(mmYYYY, serverPrefix));
        Document whereQry = new Document("_id", new ObjectId(callId));
        whereQry.append("callDirection", "incoming");
        whereQry.append("callConnectTime", null);
        Document selectQry = new Document("campaignID", 1);
        selectQry.append("phoneNumber", 1);
        MongoCursor<Document> cursor = collection.find(whereQry).projection(selectQry).iterator();
        if(cursor.hasNext()){
            bccbLogic.add("Satisfied BCCB Condition for callDirection Incoming and callConnectTime as null ");
            String campaignID = null;
            Document dc = cursor.next();
            try{
                campaignID = dc.getObjectId("campaignID").toString();
            }
            catch(Exception ex){
                System.out.println("ERROR IN BCCB");
            }
            if(campaignID==null){
                bccbLogic.add("Could not get CampaignID in calls.");
                this.updateBCCBLogicInCalls(callId, bccbLogic, mmYYYY, serverPrefix, mongoConn);
                return bccbCall;
            }
            bccbLogic.add("Got campaignID ".concat(campaignID));
            CampaignModel campaign = new CampaignDAO().getCampaignDetailsForID(campaignID);
            if(campaign.isCallBackCampaign()){
                String phoneNumber = dc.getString("phoneNumber");
                bccbLogic.add("Campaign Obtained is CallBackCampaign with phoneNumber ".concat(phoneNumber));
                this.updateBCCBLogicInCalls(callId, bccbLogic, mmYYYY, serverPrefix, mongoConn);
                bccbCall = new MissedCallDB().checkAndInsertForMissedCall(campaign, phoneNumber, mongoConn);
            }
        }
        return bccbCall;
    }
    
    public void updateCampaignDetailsInCalls(String callId,String mmYYYY,String serverPrefix,CampaignModel campaign,
        MongoClient mongoConn){
        MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
        util utilObj = new util();
        String collectionName = utilObj.returnCollectionNameForCurrentCalls(mmYYYY, serverPrefix);
        MongoCollection collection = mongoDB.getCollection(collectionName);
        Document whereQry = new Document("_id", new ObjectId(callId));
        Document updateQry = new Document("campaignID",new ObjectId(campaign.getCampaignID()));
        updateQry.append("campaignName", campaign.getCampaignName());
        collection.updateOne(whereQry, new Document("$set", updateQry));
    }
    
    public void updateBCCBLogicInCalls(String callId,ArrayList<String> bccbLogic,String mmYYYY,String serverPrefix,MongoClient mongoConn){
        MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
        util utilObj = new util();
        String collectionName = utilObj.returnCollectionNameForCurrentCalls(mmYYYY, serverPrefix);
        MongoCollection collection = mongoDB.getCollection(collectionName);
        Document whereQry = new Document("_id", new ObjectId(callId));
        Document updateQry = new Document("bccbLogic",bccbLogic);
        collection.updateOne(whereQry, new Document("$set", updateQry));
    }
    
    public void insertCurlDetails(String url,HashMap<String,String> params,String source,String response){
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("curlDetails");
            Document insertQuery = new Document();
            insertQuery.append("url", url);
            insertQuery.append("source", source);
            insertQuery.append("response", response);
            insertQuery.append("insertedOn", new Date());
            for(Map.Entry<String,String> e:params.entrySet()){
                insertQuery.append(e.getKey(), e.getValue());
            }
            collection.insertOne(insertQuery);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void removeCurlDetails(){
        try{
            DateTime time = new DateTime().minusHours(1);
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("curlDetails");
            Document whereQuery = new Document();
            whereQuery.append("insertedOn",new Document("$lte", time.toDate()));
            collection.deleteMany(whereQuery);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
