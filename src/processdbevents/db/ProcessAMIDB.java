/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.db;

import campaignconfig.db.CampaignDB;
import campaignconfig.model.CampaignModel;
import campaignconfig.model.UserModel;
import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.phonebridgecti.dao.UserDAO;
import singleton.db.DBClass;
import phonebridge.util.LogClass;
import phonebridge.util.util;
import java.util.ArrayList;
import java.util.Date;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import phonebridge.originate.OriginateRequestModel;
import phonebridge.missedcall.dao.MissedCallDAO;
import phonebridge.missedcall.db.MissedCallDB;
import phonebridge.missedcall.model.MissedCallRequestModel;
import processdbevents.model.CdrModel;

/**
 *
 * @author bharath
 */
public class ProcessAMIDB {
    private static final int hoursToMinus = 1;
    private static final int minsToMinus = 30;
    
    public String insertNewCall(String uniqueID,String callType,Date callDateTime,String insertedEvent,String mmYYYY,
        String serverNamePrefix,boolean isClosed,MongoClient mongoConn){
        String docID = null;
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY,serverNamePrefix, mongoConn);
            Document indexQry = new Document("eventDateTime", 1);
            collection.createIndex(indexQry);
            indexQry = new Document("isClosed", 1);
            collection.createIndex(indexQry);
            indexQry = new Document("channelsFoundInCall", 1);
            collection.createIndex(indexQry);
            
            Document insertDoc = new Document();
            insertDoc.append("eventDateTime",callDateTime);
            insertDoc.append("documentCreatedOn", new util().generateNewDateInYYYYMMDDFormat("UTC"));
            insertDoc.append("isClosed",isClosed);
            insertDoc.append("callUniqueID", uniqueID);
            insertDoc.append("phoneNumber", null);
            insertDoc.append("callDirection", null);
            insertDoc.append("callType", callType);
            insertDoc.append("insertedEvent", insertedEvent);
            insertDoc.append("NewState", new BasicDBList());
            insertDoc.append("Join", new BasicDBList());
            insertDoc.append("Leave", new BasicDBList());
            insertDoc.append("DialBegin", new BasicDBList());
            insertDoc.append("Bridge", new BasicDBList());
            insertDoc.append("DialEnd", new BasicDBList());
            insertDoc.append("HangUp", new BasicDBList());
            insertDoc.append("Cdr", new BasicDBList());
            insertDoc.append("Hold", new BasicDBList());
            insertDoc.append("DTMF", new BasicDBList());
            insertDoc.append("Transfer", new BasicDBList());
            insertDoc.append("TransferDetails", new BasicDBList());
            insertDoc.append("QueueCallerAbandon", new BasicDBList());
            insertDoc.append("channelsFoundInCall",new BasicDBList());
            insertDoc.append("transferedChannel",new BasicDBList());
            insertDoc.append("hoppingDetails", new BasicDBList());
            insertDoc.append("extensionsInCall", new BasicDBList());
            insertDoc.append("recordings", new BasicDBList());
            insertDoc.append("ivrEntryTime", null);
            insertDoc.append("queueJoinTime", null);
            insertDoc.append("queueNumber", null);
            insertDoc.append("typeOfIncoming", null);
            insertDoc.append("isTransfered", false);
            insertDoc.append("answerDuration", 0.00);
            insertDoc.append("queueWaitTime", null);
            insertDoc.append("extension", null);
            insertDoc.append("callStatus", null);
            insertDoc.append("hangUpTime", null);
            insertDoc.append("hangUpReason", null);
            insertDoc.append("wrapUpTime", null);
            insertDoc.append("wrapUpTimeInSecs", 0.00);
            insertDoc.append("callConnectTime", null);
            insertDoc.append("callConnectTimeInSecs", 0.00);
            insertDoc.append("disposition", null);
            insertDoc.append("dependant", null);
            insertDoc.append("comments", null);
            insertDoc.append("BCCBCall",false);
            collection.insertOne(insertDoc);
            docID = insertDoc.getObjectId("_id").toString();
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "insertNewCall", mexc.getMessage());
        }
        return docID;
    }
    
    /*
        1) Used to push each event document into the call record
    */
    public void pushDataIntoArray(String docID,String eventName,Document eventsAsDocument,ArrayList reasonArr,String mmYYYY,
            String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document updateDoc = new Document();
            eventsAsDocument.append("logic", reasonArr);
            updateDoc = new Document(eventName,eventsAsDocument);
            Document whereQry = new Document("_id",new ObjectId(docID));
            collection.updateOne(whereQry,new Document("$push", updateDoc));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "pushDataIntoArray", mexc.getMessage());
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessAMIDB", "pushDataIntoArray", ex.getMessage());
        }
    }
    
    /*
        1) Used to update phoneNumber in call record using docID
    */
    public void updatePhoneNumberInDoc(String docID,String phoneNumber,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document updateDoc = new Document("phoneNumber",phoneNumber);
            Document whereQry = new Document("_id",new ObjectId(docID));
            collection.updateOne(whereQry,new Document("$set", updateDoc));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "updatePhoneNumberInDoc", mexc.getMessage());
        }
    }
    
    /*
        1) Used to update callDirection in call record using docID
    */
    public void updateCallDirection(String docID,String callDirection,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY,serverNamePrefix, mongoConn);
            Document updateDoc = new Document("callDirection",callDirection);
            Document whereQry = new Document("_id",new ObjectId(docID));
            collection.updateOne(whereQry,new Document("$set", updateDoc));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "updateCallDirection", mexc.getMessage());
        }
    }
    
    /*
        1) Used to update callType in call record using docID
    */
    public void updateCallType(String docID,String callType,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY,serverNamePrefix,mongoConn);
            Document updateDoc = new Document("callType",callType);
            Document whereQry = new Document("_id",new ObjectId(docID));
            collection.updateOne(whereQry,new Document("$set", updateDoc));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "updateCallType", mexc.getMessage());
        }
    }
    
    /*
        1) Used to get call docID using phoneNumber. Used to find callID when call enteres queue
    */
    public String getDocIdForPhoneNumber(String phoneNumber,Date eventTime,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String docID = null;
        try{
            if(phoneNumber==null)
                return docID;
            MongoCollection collection = this.returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document selectQuery = new Document("_id", 1);
            Document whereQry = new Document("isClosed",false);
            whereQry.append("phoneNumber",phoneNumber);
            
            DateTime endTime = new util().convertJavaDateToMongoDate(eventTime.getTime());
            DateTime startTime = endTime.minusMinutes(minsToMinus);
            
            Document dateQry = new Document("$gte", startTime.toDate());
            dateQry.append("$lt", endTime.toDate());
            whereQry.append("eventDateTime", dateQry);
            
            Document sortQry = new Document("eventDateTime", -1);
            
            MongoCursor cursor = collection.find().filter(whereQry).projection(selectQuery).sort(sortQry).limit(1).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                docID = dc.getObjectId("_id").toString();
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "getDocIdForPhoneNumber", mexc.getMessage());
        }
        return docID;
    }
    
    /*
        1) Used to insert events for which callID could not be found
    */
    public void insertIntoCurrentEvents(Document events,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(mmYYYY.concat("_").concat(serverNamePrefix.concat("_unknownEvents")));
            collection.insertOne(events);
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "insertIntoCurrentEvents", mexc.getMessage());
        }
    }
    
    public MongoCollection returnMongoCollection(String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        MongoCollection collection = null;
        try{
            String collectionName = new util().returnCollectionNameForCurrentCalls(mmYYYY, serverNamePrefix);
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            collection = mongoDB.getCollection(collectionName);
        }
        catch(MongoException mexc){
            
        }
        return collection;
    }
    
    /*
        1) Check if a unqiueID exists in the newState array. Used for queueCallerAbandon event
    */
    public String checkForUniqueIDInNewState(String uniqueID1,String uniqueID2,Date eventTime,String mmYYYY,String serverNamePrefix,
            MongoClient mongoConn){
        String docID = null;
        try{
            util utilObj = new util();
            MongoCollection collection = this.returnMongoCollection(mmYYYY,serverNamePrefix, mongoConn);
            Document selectQry = new Document("_id", 1);
            
            DateTime jodaEndTime = utilObj.convertJavaDateToMongoDate(eventTime.getTime());
            DateTime jodaStartTime = jodaEndTime.minusHours(hoursToMinus);
            Document dateQry = new Document("$gte", jodaStartTime.toDate());
            dateQry.append("$lt", jodaEndTime.toDate());
            
            Document whereQuery = new Document("eventDateTime",dateQry);//"isClosed",false);
            Document whereQry1 = new Document();
            whereQry1.append("NewState", new Document("$elemMatch", new Document("uniqueID", uniqueID1)));
            Document whereQry2 = new Document();
            whereQry2.append("NewState", new Document("$elemMatch", new Document("uniqueID", uniqueID2)));
            
            BasicDBList orCondition = new BasicDBList();
            orCondition.add(whereQry1);
            orCondition.add(whereQry2);
            
            whereQuery.append("$or", orCondition);
            
            Document sortQry = new Document("eventDateTime", -1);
            MongoCursor cursor = collection.find(whereQuery).projection(selectQry).sort(sortQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                docID = dc.getObjectId("_id").toString();
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "checkForUniqueIDInNewState", mexc.getMessage());
        }
        return docID;
    }
    
    /*
        1) Update isClosed to true indicationg trunk HangUp
    */
    public void updateCallClosed(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY,serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("$set",new Document("isClosed", true));
            collection.updateOne(whereQry, updateQry);
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIApplicationDB", "updateCallClosed", mexc.getMessage());
        }
    }
    
    /*
        1) Used to check channel or destination from dialBegin is present in the transferChannel
    */
    public String checkIfTargetChannelExistsInDialBegin(String channel,String destination,Date eventTime,String mmYYYY,
        String serverNamePrefix,MongoClient mongoConn){
        String docID = null;
        try{
            util utilObj = new util();
            MongoCollection collection = this.returnMongoCollection(mmYYYY,serverNamePrefix, mongoConn);
            
            Document selectQry = new Document("_id", 1);
            
            Document whereQuery = new Document();//"isClosed",false);

            DateTime jodaEndTime = utilObj.convertJavaDateToMongoDate(eventTime.getTime());
            DateTime jodaStartTime = jodaEndTime.minusHours(hoursToMinus);
            Document dateQry = new Document("$gte", jodaStartTime.toDate());
            dateQry.append("$lt", jodaEndTime.toDate());
            whereQuery.append("eventDateTime", dateQry);
            
            Document whereQry1 = new Document("transferedChannel",channel);
            Document whereQry2 = new Document("transferedChannel",destination);
            
            BasicDBList orCondition = new BasicDBList();
            orCondition.add(whereQry1);
            orCondition.add(whereQry2);
            
            whereQuery.append("$or", orCondition);
            
            Document sortQry = new Document("eventDateTime", -1);
            
            MongoCursor cursor = collection.find(whereQuery).projection(selectQry).sort(sortQry).limit(1).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                docID = dc.getObjectId("_id").toString();
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "checkForUniqueIdInTranferArr", mexc.getMessage());
        }
        return docID;
    }
    
    /*
        1) Used to update extensionStatus in user after each extension status event
    */
    public void updateUserExtensionStatus(String extension,String extStatus,String serverID,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("users");
            Document whereQry = new Document("serverID", new ObjectId(serverID));
            whereQry.append("userExtension", extension);
            whereQry.append("deleted", 0);
            Document updateQry = new Document("extensionStatus", extStatus);
            updateQry.append("extStatusUpdatedOn", new util().generateNewDateInYYYYMMDDFormat("UTC"));
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "updateUserExtensionStatus", mexc.getMessage());
        }
    }
    
    public String getCallDirectionForId(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String callDirection = null;
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY,serverNamePrefix, mongoConn);
            Document whereQuery = new Document("_id",new ObjectId(docID));
            Document selectQry = new Document("callDirection", 1);
            MongoCursor cursor = collection.find(whereQuery).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                callDirection = dc.getString("callDirection");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "getCallDirectionForId", mexc.getMessage());
        }
        return callDirection;
    }
    
    public CampaignModel getMatchedMissedCallTrunk(String cdrChannel,ArrayList<CampaignModel> missedCallCampaigns){
        CampaignModel matchedCampaign = new CampaignModel();
        for(CampaignModel campaign:missedCallCampaigns)
        {
            if(cdrChannel.contains(campaign.getTrunkValue())){
                matchedCampaign = campaign;
            }
        }
        return matchedCampaign;
    }
    
    public String splitChannel(String dialEndchannel){
        String channel = null;
        try{
            String[] tempChannel = dialEndchannel.split(";");
            channel = tempChannel[0];
        }
        catch(Exception ex){
            ex.getMessage();
        }
        return channel;
    }
    
    /*
        Check if Cdr is for the call back call for the missed call given
    */
    public void segregateAndProcessWithTypeOfCDR(CdrModel cdr,CampaignModel campaignDetails,String originateReqID,
        OriginateRequestModel detailsFromOriginate,MongoClient mongoConn){
        try{
            String cdrType = null;
            //Check if number in destination matches with number from originate collection. If matched considered customer CDR
            if(cdr.getDestination().equals(detailsFromOriginate.getPrefix().concat(detailsFromOriginate.getPhoneNumber())))
                cdrType = "Customer";
            else{
                UserModel userModel = new UserDAO().checkExtensionExistInUsers(cdr.getDestination(), mongoConn);
                if(userModel!=null && (!userModel.isIsFollowMe() || userModel.getFollowNumber().equals(cdr.getDestination())))
                    cdrType = "Agent";
            }
            if(cdrType!=null)
                new MissedCallDAO().updateMissedCallStatusBasedOnCdr(campaignDetails, cdr, originateReqID, cdrType, detailsFromOriginate, mongoConn);
        }
        catch(Exception ex){
            
        }
    }
    
    public UserModel checkIfCdrBelongsToAgentOrCustomer(CdrModel cdr,String callerID,MongoClient mongoConn){
        UserModel user = null;
        try{
            String extensionOrFollowNumber = null;
            if(cdr.getDestination().contains("FMGL") || cdr.getDestination().contains("FMPR")){
                String tempDestination = cdr.getDestination();
                String[] tempDestinationArr = tempDestination.split("-");
                extensionOrFollowNumber = tempDestinationArr[tempDestinationArr.length-1];
                if(extensionOrFollowNumber.length()>=10 && extensionOrFollowNumber.contains("#"))
                    extensionOrFollowNumber = extensionOrFollowNumber.replace("#", "");
            }
            else if(cdr.getDestination().length()>=10){
                extensionOrFollowNumber = new util().stripAdditionalInfoFromNumber(cdr.getDestination());
            }
            user = new UserDAO().checkExtensionExistInUsers(extensionOrFollowNumber, mongoConn);    
        }
        catch(Exception ex){
            
        }
        return user;
    }
    
    public boolean checkIfObjectIdIsValid(String objectIDToCheck){
        boolean valid = false;
        try{
            if(ObjectId.isValid(objectIDToCheck))
                valid = true;
        }
        catch(Exception ex){
            
        }
        return valid;
    }
    
    /*
        Get callID using originate reqID which is retrieved from events if MissedCallCB and clickAndCall
    */
    public String getCallDocIDUsingOriginateReqID(String originateReqID,Date eventTime,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String callDocID = null;
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY,serverNamePrefix, mongoConn);
            Document whereQry = new Document("originateReqID", new ObjectId(originateReqID));
            
            DateTime jodaEndTime = new util().convertJavaDateToMongoDate(eventTime.getTime());
            DateTime jodaStartTime = jodaEndTime.minusHours(hoursToMinus);
            whereQry.append("eventDateTime", new Document("$gte", jodaStartTime.toDate()).append("$lte", jodaEndTime.toDate()));
            
            
            whereQry.append("isClosed", false);
            Document selectQry = new Document("_id", 1);
            Document sortQry = new Document("eventDateTime", -1);
            MongoCursor<Document> cursor = collection.find(whereQry).projection(selectQry).sort(sortQry).limit(1).iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                callDocID = dc.getObjectId("_id").toString();
            }
        }
        catch(Exception ex){
            
        }
        return callDocID;
    }
    
    /*
        1) update originateReqID in call record
    */
    public void updateOriginateReqIdInCalls(String callDocID,String originateReqID,String mmYYYY,String serverNamePrefix,
        MongoClient mongoConn){
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY,serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(callDocID));
            Document updateQry = new Document("originateReqID", new ObjectId(originateReqID));
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(Exception ex){
            
        }
    }
    
    /*
        1) Get originateReqID using callID from currentCalls
    */
    public String getOriginateReqIDUsingCallID(String callID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String originateReqID = null;
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY,serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(callID));
            Document selectQry = new Document("originateReqID", 1);
            MongoCursor<Document> cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                try{
                    originateReqID = dc.getObjectId("originateReqID").toString();
                }
                catch(NullPointerException nexc){
                    
                }
            }
        }
        catch(MongoException ex){
            
        }
        return originateReqID;
    }
    
    /*
        1) Used to find docID using phoneNumber
        2) Used in case of incomingFollowME
        3) Number should be same, callDirection should be incoming and isClosed should be false
    */
    public String getDocIdForPhoneNumberUsingCallDirection(String phoneNumber,Date eventTime,String mmYYYY,
        String serverNamePrefix,MongoClient mongoConn){
        String docID = null;
        try{
            if(phoneNumber==null)
                return docID;
            MongoCollection collection = this.returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document selectQuery = new Document("_id", 1);
            Document whereQry = new Document();
            
            whereQry.append("isClosed",false);
            whereQry.append("phoneNumber",phoneNumber);
            whereQry.append("callDirection", "incoming");
            
            DateTime jodaEndTime = new util().convertJavaDateToMongoDate(eventTime.getTime());
            DateTime jodaStartTime = jodaEndTime.minusMinutes(2);
            whereQry.append("eventDateTime", new Document("$gte", jodaStartTime.toDate()).append("$lte", jodaEndTime.toDate()));
            
            Document sortQry = new Document("eventDateTime", -1);
            MongoCursor cursor = collection.find().filter(whereQry).projection(selectQuery).sort(sortQry).limit(1).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                docID = dc.getObjectId("_id").toString();
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "getDocIdForPhoneNumber", mexc.getMessage());
        }
        return docID;
    }
    
    /*
        Used to get docID to push channel obtained from coreShowChannel
    */
    public String getDocIDToAppendCoreShowChannel(String serverNamePrefix,MongoClient mongoConn){
        String coreShowDocID = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection<Document> collection = mongoDB.getCollection(serverNamePrefix.concat("_coreShowChannel"));
            Document whereQry = new Document("eventCompleted", false);
            Document sortQry = new Document("dateEntered", -1);
            MongoCursor<Document> cursor = collection.find(whereQry).sort(sortQry).iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                coreShowDocID = dc.getObjectId("_id").toString();
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "getDocIDToAppendCoreShowChannel", mexc.getMessage());
        }
        return coreShowDocID;
    }
    
    /*
        Insert new document for coreShowChannel if no document is found
    */
    public String insertNewCoreShowChannelDoc(String serverNamePrefix,util utilObj,MongoClient mongoConn){
        ObjectId coreShowDocID = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection<Document> collection = mongoDB.getCollection(serverNamePrefix.concat("_coreShowChannel"));
            Document indexQry = new Document("eventCompleted", 1);
            collection.createIndex(indexQry);
            indexQry = new Document("dateEntered", 1);
            collection.createIndex(indexQry);
            coreShowDocID = new ObjectId();
            Document insertDoc = new Document("_id", coreShowDocID);
            insertDoc.append("eventCompleted", false);
            insertDoc.append("dateEntered", utilObj.generateNewDateInYYYYMMDDFormat());
            insertDoc.append("channels", new ArrayList<>());
            collection.insertOne(insertDoc);
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "insertNewCoreShowChannelDoc", mexc.getMessage());
        }
        return coreShowDocID.toString();
    }
    
    /*
        Used to push channel inside the array
    */
    public void pushChannelsInCoreShowCollection(String docID,String channel,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection<Document> collection = mongoDB.getCollection(serverNamePrefix.concat("_coreShowChannel"));
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("channels", channel);
            collection.updateOne(whereQry, new Document("$push", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "pushChannelsInCoreShowCollection", mexc.getMessage());
        }
    }
    
    /*
        Used to update after coreShowCompletion event
    */
    public void updateEventCompletionInCoreShowChannel(String docID,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection<Document> collection = mongoDB.getCollection(serverNamePrefix.concat("_coreShowChannel"));
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("eventCompleted", true);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessAMIDB", "updateEventCompletionInCoreShowChannel", mexc.getMessage());
        }
    }
    
    /*
        Used to push channels into current calls using docID
    */
    public void pushChannelsIntoCurrentCallsUsingCallID(String callDocID,String channelToPush,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(callDocID));
            Document updateQry = new Document("trunkChannelsInCall", channelToPush);
            collection.updateOne(whereQry, new Document("$addToSet", updateQry));
        }
        catch(MongoException ex){
            
        }
    }
    
    /*
        1) To update transfered to extension in transfer details
    */
    public void updateTransferedToInTransferDetails(String docID,String transferedTo,String mmYYYY,String serverNamePrefix,
        MongoClient mongoConn){
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            whereQry.append("TransferDetails", new Document("$elemMatch", new Document("transferedTo", null)));
            Document updateQry = new Document("TransferDetails.$.transferedTo", transferedTo);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            
        }
    }
    
    /*
        1) Used to push all channel,destination from events to query them to identify calls
    */
    public void pushAllChannelsFoundInCalls(String docID,String channelToPush,String mmYYYY,String serverNamePrefix,
        MongoClient mongoConn){
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id",new ObjectId(docID));
            if(channelToPush.contains(";")){
                String[] channelToPushArr = channelToPush.split(";");
                channelToPush = channelToPushArr[0];
            }
            Document updateQry = new Document("channelsFoundInCall", channelToPush);
            collection.updateOne(whereQry, new Document("$addToSet", updateQry));
        }
        catch(MongoException mexc){
            
        }
    }
    
    /*
        Find if the channel or destination is found in current calls in the channelsFoundInCall array
    */
    public String getDocIDByQueryingWithChannelAndDestination(String channel,String destination,Date eventTime,
        boolean addCloseCondition,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String docID = null;
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            
            Document whereQry = new Document();
            DateTime jodaEndTime = new util().convertJavaDateToMongoDate(eventTime.getTime());
            DateTime jodaStartTime = jodaEndTime.minusHours(hoursToMinus);
            whereQry.append("eventDateTime", new Document("$gte", jodaStartTime.toDate()).append("$lte", jodaEndTime.toDate()));
            
            if(channel!=null && channel.contains(";")){
                String[] channelArr = channel.split(";");
                channel = channelArr[0];
            }
            if(destination!=null && destination.contains(";")){
                String[] destinationArr = destination.split(";");
                destination = destinationArr[0];
            }
            
            Document whereQry1 = new Document("channelsFoundInCall",channel);
            Document whereQry2 = new Document("channelsFoundInCall",destination);
            
            BasicDBList orCondition = new BasicDBList();
            orCondition.add(whereQry1);
            orCondition.add(whereQry2);
            whereQry.append("$or", orCondition);
            if(addCloseCondition)
                whereQry.append("isClosed", false);
            
            Document selectQry = new Document("_id", 1);
            Document sortQry = new Document("eventDateTime", -1);
            MongoCursor<Document> cursor = collection.find(whereQry).projection(selectQry).sort(sortQry).limit(1).iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                docID = dc.getObjectId("_id").toString();
            }
        }
        catch(MongoException mexc){
            
        }
        return docID;
    }
    
    /*
        1) Used to push targetChannel in transfer event into a seperate array, to query and check when a dial begin 
            event is processed
    */
    public void pushTransferChannelInCalls(String docID,String channelToPush,String mmYYYY,String serverNamePrefix,
        MongoClient mongoConn){
        try{
            channelToPush = (channelToPush.contains("<ZOMBIE>"))?channelToPush.replace("<ZOMBIE>", ""):channelToPush;
            channelToPush = (channelToPush.contains("AsyncGoto/"))?channelToPush.replace("AsyncGoto/", ""):channelToPush;

            MongoCollection collection = this.returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("transferedChannel", channelToPush);
            collection.updateOne(whereQry, new Document("$push", updateQry));
        }
        catch(MongoException mexc){

        }
    }
    
    /*
        1) Used to move events from processed collection into live events to process them again
        2) Used only when we get a transfer with transfer type "Attended"
        3) Done by N.R.Bharath
    */
    public void moveEventsFromProcessedToLiveCollection(String targetChannel,Date eventTime,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection toCollection = mongoDB.getCollection("events_".concat(serverNamePrefix));
            MongoCollection fromCollection = mongoDB.getCollection("events_".concat(serverNamePrefix).concat("_proccessed").
                    concat("_".concat(new util().getDDMMForJavaDate(eventTime.getTime()))));
            
            Document whereQry = new Document();
            DateTime jodaEndTime = new util().convertJavaDateToMongoDate(eventTime.getTime());
            DateTime jodaStartTime = jodaEndTime.minusMinutes(minsToMinus);
            whereQry.append("eventtime", new Document("$gte", jodaStartTime.toDate()).append("$lte", jodaEndTime.toDate()));
            
            BasicDBList orCond = new BasicDBList();
            orCond.add(new Document("channel",targetChannel));
            orCond.add(new Document("destination",targetChannel));
            orCond.add(new Document("channel1",targetChannel));
            orCond.add(new Document("channel2",targetChannel));
            orCond.add(new Document("destinationchannel",targetChannel));
            
            whereQry.append("$or", orCond);
            
            MongoCursor<Document> cursor = fromCollection.find(whereQry).iterator();
            while(cursor.hasNext()){
                Document dc = cursor.next();
                try{
                    toCollection.insertOne(dc);
                }
                catch(Exception ex){
                    
                }
                try{
                    fromCollection.deleteOne(new Document("_id", dc.getObjectId("_id")));
                }
                catch(Exception ex){
                    
                }
            }
        }
        catch(Exception ex){
            
        }
    }
    
    /*
        To check if Extension Obtained from transfer dial begin has a open call in current calls
    */
    public String getCallIDUsingExtensionForAttendTransferCall(String extension,String mmYYYY,String serverNamePrefix,
        MongoClient mongoConn){
        String docID = null;
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY,serverNamePrefix, mongoConn);
            Document whereQry = new Document("extensionsInCall", extension);
            whereQry.append("isClosed", false);
            Document sortQry = new Document("eventDateTime", -1);
            Document selectQry = new Document("_id", 1);
            MongoCursor<Document> cursor = collection.find(whereQry).projection(selectQry).sort(sortQry).limit(1).iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                docID = dc.getObjectId("_id").toString();
            }
        }
        catch(Exception ex){
            
        }
        return docID;
    }
    
    /*
        Used to push transfer details if attend and transfer
    */
    public void pushTransferDetails(String docID,String transferedFrom,String transferedTo,String mmYYYY,
        String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateDoc = new Document("transferedFrom", transferedFrom);
            updateDoc.append("transferedTo", transferedTo);
            Document updateQry = new Document("TransferDetails", updateDoc);
            collection.updateOne(whereQry, new Document("$addToSet", updateQry));
        }
        catch(Exception ex){
            
        }
    }
    
    /*
        Push all extensions in call for capturing second transfer in case of attend and transfer
    */
    public void pushExtensionInCall(String docID,String extension,String mmYYYY,String serverNamePrefix,
        MongoClient mongoConn){
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("extensionsInCall", extension);
            collection.updateOne(whereQry, new Document("$addToSet", updateQry));
        }
        catch(Exception ex){
            
        }
    }
    
    /*
        Get phoneNumber for callId
    */
    public String getPhoneNumberForCallId(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String phoneNumber = null;
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document selectQry = new Document("phoneNumber", 1);
            MongoCursor<Document> cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                phoneNumber = dc.getString("phoneNumber");
            }
        }
        catch(Exception ex){
            
        }
        return phoneNumber;
    }
    
    /*
        Get phoneNumber for callId
    */
    public String updateBCCBFlag(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        String phoneNumber = null;
        try{
            MongoCollection collection = this.returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("BCCBCall", true);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(Exception ex){
            System.out.println("ERROR IN UPDATE BCCB FLAG");
        }
        return phoneNumber;
    }
    
    public ArrayList getAllMissedCallCampaigns(MongoClient mongoConn){
        ArrayList<CampaignModel> missedCallCampaigns = new ArrayList<>();
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document whereQuery = new Document();
            whereQuery.append("dialMethod", "missedCall");
            whereQuery.append("deleted", 0);
            MongoCursor cursor = collection.find(whereQuery).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                missedCallCampaigns.add(new CampaignDB().convertDBObjecttoJavaObject(dc));
            }
        }
        catch(MongoException mecx){
            LogClass.logMsg("ProcessAMIDB", "getAllMissedCallTrunks", mecx.getMessage());
        }
        return missedCallCampaigns;
    }
    
    public ArrayList<MissedCallRequestModel> getmissedCallSLABreachRequests(String listID, MissedCallDB missedCallDb, int slaTime, MongoClient mongoConn) {
                ArrayList<MissedCallRequestModel> SLADetails = new ArrayList<>();
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("lst_"+listID);
            
            Document whereQuery = new Document("slaTime",new Document("$lte",new Date()));
            whereQuery.append("clientConnectedOn", null).append("isEscalated", false);
            
            Document selectQuery = new Document();
            selectQuery.append("phoneNumber", 1);
            MongoCursor cursor = collection.find(whereQuery).projection(selectQuery).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                MissedCallRequestModel clientContactNumber = new MissedCallRequestModel();
                clientContactNumber.setPhoneNumber(dc.getString("phoneNumber"));
                SLADetails.add(clientContactNumber);
            }
        }
        catch(MongoException mecx){
            mecx.getMessage();
        }
        return SLADetails;
    }
    
    public boolean checkBridgeEventForClientNumberFromHangUp(String phoneNumber,String MMYYYY,String serverNamePrefix){
        boolean bridgeEventExist = false;
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoCollection collection = this.returnMongoCollection(MMYYYY, serverNamePrefix, mongoConn);
            Document whereQuery = new Document("Bridge.callerID2",phoneNumber);
            whereQuery.append("isClosed", false);
            MongoCursor cursor = collection.find(whereQuery).iterator();
            if(cursor.hasNext()){
                bridgeEventExist=true;
            }
        }
        catch(Exception e){
            e.getMessage();
        }
        return bridgeEventExist;
    }
    
    public String getFollowMenumberusingExtension(String extension,MongoClient mongoConn){
        String phoneNumber = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("users");
            Document whereQuery = new Document("userExtension",extension);
            whereQuery.append("deleted", 0);
            Document selectQuery = new Document("followNumber",1);
            MongoCursor cursor = collection.find(whereQuery).projection(selectQuery).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                phoneNumber = dc.getString("followNumber");
            }
        }
        catch(MongoException mecx){
            mecx.getMessage();
        }
        return phoneNumber;
    }
}
