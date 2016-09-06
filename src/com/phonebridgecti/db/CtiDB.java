/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridgecti.db;

import agentperformance.dao.AgentPerformanceDAO;
import campaignconfig.db.CampaignDB;
import campaignconfig.model.CampaignModel;
import campaignconfig.model.DependantForSave;
import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import singleton.db.DBClass;
import phonebridge.util.LogClass;
import phonebridge.util.util;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import org.bson.Document;
import org.bson.types.ObjectId;
import phonebridge.originate.OriginateCallDb;
import phonebridge.originate.OriginateRequestModel;
import processdbevents.db.ProcessAMIDB;
import processdbevents.db.ProcessForReportsDB;

/**
 *
 * @author bharath
 */
public class CtiDB {
    /*
        Insert New Popup in Collection For Each User(serverNamPrefix_extension_Popup)
    */
    public void insertPopupObjTODB(String extension,String serverNamePrefix,Document docToInsert,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(new util().returnCollectionNameForPopup(serverNamePrefix, extension));
            collection.deleteMany(new Document("extension", extension));
            collection.insertOne(docToInsert);
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "insertPopupObjTODB", mexc.getMessage());
        }
    }
    
    /*
        Update UniqueID and CallID In Popup Collection
    */
    public void updateCallAndUniqueID(String docID,String callID,String uniqueID,String extension,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("uniqueID", uniqueID);
            if(callID!=null)
                updateQry.append("callID", new ObjectId(callID));
            String[] collectionName = {new util().returnCollectionNameForPopup(serverNamePrefix, extension),serverNamePrefix.concat("_openCalls")};
            for(String collName : collectionName){
                MongoCollection collection = mongoDB.getCollection(collName);
                collection.updateOne(whereQry, new Document("$set", updateQry));
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "updateCallAndUniqueID", mexc.getMessage());
        }
    }
    
    /*
        Update Call Status In PopupCollection and OpenCalls Collection
    */
    public boolean updateCallStatus(String uniqueID,String callDocID,String callStatus,Date dateToUpdate,String extension,String serverNamePrefix,MongoClient mongoConn){
        boolean updated = false;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            Document whereQry = new Document("callID", new ObjectId(callDocID));
            Document updateQry = new Document("callStatus", callStatus);
            switch(callStatus){
                case "Dialing":
                    updateQry.append("startTime", dateToUpdate);
                    whereQry.append("callStatus", new Document("$ne", "HangUp"));
                    break;
                case "Ringing":
                    updateQry.append("startTime", dateToUpdate);
                    whereQry.append("callStatus", new Document("$ne", "HangUp"));
                    whereQry.append("callStatus", new Document("$ne", "HangUp"));
                    break;
                case "HangUp":
                    updateQry.append("endTime", dateToUpdate);
                    whereQry.append("endTime", new Document("$exists", false));
                    break;
                case "Connected":
                    updateQry.append("answerTime", dateToUpdate);
                    whereQry.append("callStatus", new Document("$ne", "HangUp"));
                    break;
            }
            String[] collectionName = {new util().returnCollectionNameForPopup(serverNamePrefix, extension),serverNamePrefix.concat("_openCalls")};
            for(String collName : collectionName){
                MongoCollection collection = mongoDB.getCollection(collName);
                UpdateResult result = collection.updateOne(whereQry, new Document("$set", updateQry));
                if(result.getModifiedCount()>0 && collName.equals(new util().returnCollectionNameForPopup(serverNamePrefix, extension)))
                    updated = true;
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "updateCallAndUniqueID", mexc.getMessage());
        }
        return updated;
    }
    
    /*
        Update CampaignID and CampaignName and CustID in Popup Collection and Live Call Collection
    */
    public void updateCmpgnIDNameTypeOfDialerAndCallType(String callDocID,HashMap<String,String> campaignInfo,String custID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            Document whereQry = new Document("_id", new ObjectId(callDocID));
            Document updateQry = new Document("campaignID", new ObjectId(campaignInfo.get("campaignID")));
            updateQry.append("campaignName", campaignInfo.get("campaignName"));
            if(custID!=null && new ProcessAMIDB().checkIfObjectIdIsValid(custID))
                updateQry.append("custID", new ObjectId(custID));
            else
                updateQry.append("custID", custID);
            updateQry.append("typeOfDialer", campaignInfo.get("typeOfDialer"));
            updateQry.append("callType", campaignInfo.get("callType"));
            String[] collectionName = {new util().returnCollectionNameForCurrentCalls(mmYYYY, serverNamePrefix),serverNamePrefix.concat("_openCalls")};
            for(String collName : collectionName){
                MongoCollection collection = mongoDB.getCollection(collName);
                collection.updateOne(whereQry, new Document("$set", updateQry));
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "updateCmpgnIDNameTypeOfDialerAndCallType", mexc.getMessage());
        }
    }
    
    /*
        Insert Details For Auto Close of Popup
    */
     public void insertOpenPopupDetailsForAutoClose(String popupID,int wrapUpTime,String extension,String uniqueID,String callDocID,
            String serverID,String serverNamePrefix,Date dateToGetMMYYYY,String custID,String campaignID,String campaignName,
            String listID,MongoClient mongoConn){
        try{
            if(dateToGetMMYYYY==null)
               dateToGetMMYYYY=new Date();
            String mmYYYY = new util().getMMYYYYForJavaDate(dateToGetMMYYYY.getTime());
            String callCollectionName = new util().returnCollectionNameForCurrentCalls(mmYYYY, serverNamePrefix);
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("openPopup");
            Document insertDoc = new Document("popupID", new ObjectId(popupID));
            insertDoc.append("autoWrapTime",wrapUpTime);
            insertDoc.append("extension", extension);
            insertDoc.append("serverNamePrefix", serverNamePrefix);
            if(custID!=null && new ProcessAMIDB().checkIfObjectIdIsValid(custID))
                insertDoc.append("custID", new ObjectId(custID));
            else
                insertDoc.append("custID", custID);
            insertDoc.append("callCollectionName", callCollectionName);
            insertDoc.append("campaignName", campaignName);
            insertDoc.append("mmYYYY", mmYYYY);
            insertDoc.append("hangUpTime", null);
            insertDoc.append("uniqueID", uniqueID);
            if(callDocID!=null)
                insertDoc.append("callDocID", new ObjectId(callDocID));
            insertDoc.append("serverID", new ObjectId(serverID));
            insertDoc.append("campaignID", new ObjectId(campaignID));
            insertDoc.append("listID", listID);
            collection.insertOne(insertDoc);
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "insertOpenPopupDetails", mexc.getMessage());
        }
    }
    
    /*
        Get AutoWrapTime From OpenPopup Collection
     */
    private int getAutoWrapTime(String uniqueID,String callDocID,MongoClient mongoConn){
        int popupAutoWrapTime = 0;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("openPopup");
            Document whereQry = new Document("callDocID", new ObjectId(callDocID));
            Document selectQry = new Document("autoWrapTime",1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                popupAutoWrapTime = dc.getInteger("autoWrapTime");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "getAutoWrapTime", mexc.getMessage());
        }
        return popupAutoWrapTime;
    }
    
    /*
        Update Hangup Time In OpenPopup for AutoWrap
    */
    public void updateHangUpTimeInOpenPopup(String uniqueID,String callDocID,Date hangUpTime,MongoClient mongoConn){
        try{
            int popupWrapTime = this.getAutoWrapTime(uniqueID, callDocID, mongoConn);
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("openPopup");
            Document whereQry = new Document("callDocID", new ObjectId(callDocID));
            whereQry.append("hangUpTime", null);
            long popupClosedAfter = hangUpTime.getTime() + (popupWrapTime*1000);
            Date toBeClosedAfter = new Date(popupClosedAfter);
            Document updateQry = new Document("hangUpTime", hangUpTime);
            updateQry.append("popupClosedAfter", popupClosedAfter);
            updateQry.append("popupClosedAfterDate", toBeClosedAfter);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "updateHangUpTimeInOpenPopup", mexc.getMessage());
        }
    }
    
    /*
        Insert For Live Monitoring
    */
    public void insertOpenCalls(String extension,Document insertDoc,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix.concat("_openCalls"));
            Document whereQry = new Document("extension", extension);
            collection.deleteOne(whereQry);
            
            insertDoc.remove("showField");
            insertDoc.remove("disposition");
            collection.insertOne(insertDoc);
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "insertOpenCalls", mexc.getMessage());
        }
    }
    
    /*
        Delete From Live Monitoring Collection
    */
    public void deleteDocFromOpenCalls(String popupID,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix.concat("_openCalls"));
            Document whereQry = new Document("_id", new ObjectId(popupID));
            collection.deleteOne(whereQry);
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "deleteDocFromOpenCalls", mexc.getMessage());
        }
    }
    
    /*
        Get Hangup time from Current Calls Collection
    */
    public Date getHangUpTimeFromDoc(String callDocID,Date eventTime,String serverNamePrefix,MongoClient mongoConn){
        Date hangUpTime = null;
        try{
            String mmYYYY = new util().getMMYYYYForJavaDate(eventTime.getTime());
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(new util().returnCollectionNameForCurrentCalls(mmYYYY, serverNamePrefix));
            Document whereQry = new Document("_id", new ObjectId(callDocID));
            Document selectQry = new Document("hangUpTime", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                hangUpTime = dc.getDate("hangUpTime");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "getHangUpTimeFromDoc", mexc.getMessage());
        }
        return hangUpTime;
    }
    
    /*
        Update WrapTime and WrapTimeSecs In Current Calls
    */
    public void updateWrapUpTime(String callDocID,Date eventTime,double wrapUpTimeInSecs ,String serverNamePrefix,MongoClient mongoConn){
        try{
            String mmYYYY = new util().getMMYYYYForJavaDate(eventTime.getTime());
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(new util().returnCollectionNameForCurrentCalls(mmYYYY, serverNamePrefix));
            Document whereQry = new Document("_id",new ObjectId(callDocID));
            whereQry.append("wrapUpTime", null);
            Document updateQry = new Document("wrapUpTime", eventTime);
            updateQry.append("wrapUpTimeInSecs", wrapUpTimeInSecs);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "updateWrapUpTime", mexc.getMessage());
        }
    }
    
    /*
        Get Popup Details Exceeded AutoWrap Time
    */
    public ArrayList<HashMap> getClosePopupDetails(String serverID,MongoClient mongoConn){
        ArrayList<HashMap> closePopupDetails = new ArrayList<>();
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("openPopup");
            long popupClosedTime = new Date().getTime();
            Document whereQry = new Document("popupClosedAfter", new Document("$lt", popupClosedTime));
            whereQry.append("serverID", new ObjectId(serverID));
            MongoCursor cursor = collection.find(whereQry).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                HashMap<String,String> toCloseDetails = new HashMap();
                toCloseDetails.put("openPopupCollID", dc.getObjectId("_id").toString());
                toCloseDetails.put("callDocID", dc.getObjectId("callDocID").toString());
                toCloseDetails.put("extension", dc.getString("extension"));
                toCloseDetails.put("serverNamePrefix", dc.getString("serverNamePrefix"));
                toCloseDetails.put("popupID", dc.getObjectId("popupID").toString());
                try{
                    if(dc.get("custID")!=null && new ProcessAMIDB().checkIfObjectIdIsValid(dc.get("custID").toString()))
                        toCloseDetails.put("custID", dc.getObjectId("custID").toString());
                    else
                        toCloseDetails.put("custID", dc.getString("custID"));
                }
                catch(NullPointerException nexc){
                    toCloseDetails.put("custID", null);
                }
                toCloseDetails.put("callCollectionName", dc.getString("callCollectionName"));
                toCloseDetails.put("campaignName", dc.getString("campaignName"));
                toCloseDetails.put("mmYYYY", dc.getString("mmYYYY"));
                toCloseDetails.put("uniqueID", dc.getString("uniqueID"));
                toCloseDetails.put("serverID", dc.getObjectId("serverID").toString());
                toCloseDetails.put("campaignID", dc.getObjectId("campaignID").toString());
                toCloseDetails.put("listID", dc.getString("listID"));
                closePopupDetails.add(toCloseDetails);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "getClosePopupDetails", mexc.getMessage());
        }
        return closePopupDetails;
    }
    
    /*
        Insert Disposition and Dependant In Current Calls
    */
    public Date insertDispoDependantInCalls(String callID,String selectedDisposition,ArrayList<DependantForSave> dependant,
        String comments,String collectionName,String extension,String campaignID,String serverNamePrefix,MongoClient mongoConn){
        Date startDate = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(collectionName);
            Document whereQry = new Document("_id",new ObjectId(callID));
            whereQry.append("disposition", null);
            Document selectQry = new Document("eventDateTime", 1);
            Document updateQry = new Document("disposition", selectedDisposition);
            updateQry.append("comments", comments);
            
            Document dispoDoc = new Document();
            for(DependantForSave eachDependent : dependant){
                dispoDoc.append(eachDependent.getStrLabel(), eachDependent.getStrValue());
                if(eachDependent.isIsConversionDependant()){
                    new AgentPerformanceDAO("conversionDependent", 0.00, extension, campaignID, serverNamePrefix, null, eachDependent.getStrLabel(), mongoConn).run();
                }
            }
            updateQry.append("dependant", dispoDoc);
            collection.updateOne(whereQry, new Document("$set",updateQry));
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                startDate = dc.getDate("eventDateTime");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "insertDispoDependantInCalls", mexc.getMessage());
        }
        return startDate;
    } 
    
    /*
        Update Call Details In Campaign List(Base)
    */
    public void updateCallHistoryInList(String callID,String callsCollectionName,String listID,String custID,
        Date startDate,String leadStatus,MongoClient mongoConn){
        if(custID==null)
            return;
        try{   
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(new util().returnCollNameToSearchContactInfo(listID));
            HashMap callhistory = new HashMap<String,String>();  
            callhistory.put("callID",callID);  
            callhistory.put("callsCollectionName",callsCollectionName);
            callhistory.put("startDate",startDate);
            Document whereQry = new Document("_id",new ObjectId(custID));
            Document updateCondtion = new Document("$push",new Document("callHistory",callhistory));
            collection.updateOne(whereQry, updateCondtion);
            Document updateQry = new Document("leadStatus",leadStatus);
            /*if(leadStatus.equals("Call Back")){
                updateQry.append("followUpOn", followUpDate);
                updateQry.append("extension", extension);
            }*/
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
             LogClass.logMsg("CtiDB", "updateCallHistoryInList", mexc.getMessage());
        }
    }
    
    /*
        Update PopupStatus In Users
    */
    public void updatePopupFreeInUsers(String userExtension,String serverID,MongoClient mongoConn){
        try{   
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("users");
            Document searchQuery = new Document("userExtension",userExtension);
            searchQuery.append("serverID", new ObjectId(serverID));
            Document updateCondtion = new Document("popupStatus","free");
            updateCondtion.append("popupStatusUpdateTime",new Date());
            collection.updateOne(searchQuery, new Document("$set", updateCondtion));
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "updatePopupFreeInUsers", mexc.getMessage());
        }
    }
    
    /*
        Delete Record From Popup Collection
    */
    public void deletePopupRecord(String userExtension,String serverNamePrefix,String popupObjectID,MongoClient mongoConn){
        try{   
            String collectionName = new util().returnCollectionNameForPopup(serverNamePrefix,userExtension);  
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(collectionName);
            Document deleteQuery = new Document("_id",new ObjectId(popupObjectID));
            collection.deleteOne(deleteQuery);
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "deletePopupRecord", mexc.getMessage());
        }
    }
    
    
    /*
        Get CustID(_id of CampaignBase Of Specific Doc) from Popup Collection
    */
    public String getCustIDFromPopup(String popupDocID,String extension,String serverNamePrefix,MongoClient mongoConn){
        String custID = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(new util().returnCollectionNameForPopup(serverNamePrefix, extension));
            Document whereQry = new Document("_id", new ObjectId(popupDocID));
            Document selectQry = new Document("custID",1);
            selectQry.append("moduleLinked", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                try{
                    if(!dc.getBoolean("moduleLinked"))
                        custID = dc.getObjectId("custID").toString();
                    else
                        custID = dc.getString("custID");
                }
                catch(NullPointerException nexc){
                    LogClass.logMsg("CtiDB", "getCustIDFromPopup", nexc.getMessage());
                }
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "getCustIDFromPopup", mexc.getMessage());
        }
        return custID;
    }
    
    /*
        Delete Doc From AutoWrap Collection
    */
    public void deleteFromOpenPopup(String popupDocID,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("openPopup");
            Document whereQry = new Document("popupID", new ObjectId(popupDocID));
            collection.deleteOne(whereQry);
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "deleteFromOpenPopup", mexc.getMessage());
        }
    }
    
    /*
        Get Details For Originating Calls
    */
    public ArrayList<OriginateRequestModel> getNumbersToOriginate(String serverNamePrefix,MongoClient mongoConn){
        ArrayList<OriginateRequestModel> numbersToOriginate = new ArrayList<>();
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix.concat("_originatecall"));
            long callOriginateTime = new util().generateNewDateInYYYYMMDDFormat("UTC").getTime();
            Document whereQry = new Document("callOn", new Document("$lt", callOriginateTime));
            whereQry.append("status", "toInitiate");
            whereQry.append("hasAgentAnswered", "No");
            MongoCursor cursor = collection.find(whereQry).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                OriginateRequestModel originateReq = new OriginateRequestModel();
                originateReq = new OriginateCallDb().convertDbObjToJavaObj(dc);
                numbersToOriginate.add(originateReq);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "getNumbersToOriginate", mexc.getMessage());
        }
        return numbersToOriginate;
    }
    
    /*
        Update status In originate Request Collection After Originate
    */
    public void updateCallInitiated(String docID,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix.concat("_originatecall"));
            Date initiatedUpdatedOn = new util().generateNewDateInYYYYMMDDFormat("UTC");
            Long initiatedUpdatedOnSecs = initiatedUpdatedOn.getTime() + (60*1000);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("status", "Initiated");
            updateQry.append("initiatedUpdateOn", initiatedUpdatedOn);
            updateQry.append("initiatedUpdateOnSecs", initiatedUpdatedOnSecs);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "updateCallInitiated", mexc.getMessage());
        }
    }
    
    /*
        Get Details Of Number Not Attended By Agent In SIP From Originate Request Collection
    */
    public ArrayList<HashMap> getNumbersNotAttendedByAgent(String serverNamePrefix,CtiDB ctiDb,MongoClient mongoConn){
        ArrayList<HashMap> numbersNotAttendedByAgent = new ArrayList<>();
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix.concat("_originatecall"));
            Document whereQry = new Document("status", "Initiated");
            whereQry.append("hasAgentAnswered", "No");
            whereQry.append("initiatedUpdateOnSecs", new Document("$lt", new util().generateNewDateInYYYYMMDDFormat("UTC").getTime()));
            Document selectQry = new Document("popupID", 1);
            selectQry.append("userExtension", 1);
            selectQry.append("phoneNumber", 1);
            selectQry.append("custID", 1);
            selectQry.append("campaignName", 1);
            selectQry.append("listID", 1);
            selectQry.append("campaignID", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                HashMap<String,String> details = new HashMap();
                details.put("extension", dc.getString("userExtension"));
                details.put("phoneNumber", dc.getString("phoneNumber"));
                details.put("popupID", dc.getObjectId("popupID").toString());
                details.put("custID", dc.getObjectId("custID").toString());
                details.put("campaignName", dc.getString("campaignName"));
                details.put("listID", dc.getString("listID"));
                details.put("campaignID", dc.getObjectId("campaignID").toString());
                numbersNotAttendedByAgent.add(details);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "getNumbersNotAttendedByAgent", mexc.getMessage());
        }
        return numbersNotAttendedByAgent;
    }
    
    /*
        Update AgentAnswer Status in Originate Call Request
    */
    public void updateHasAgentAnsweredInOriginateCall(String extension,String phoneNumber,String updateVal,
            String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix.concat("_originatecall"));
            Document whereQry = new Document("status", "Initiated");
            whereQry.append("hasAgentAnswered", "No");
            whereQry.append("userExtension", extension);
            whereQry.append("phoneNumber", phoneNumber);
            Document updateQry = new Document("hasAgentAnswered",updateVal);
            updateQry.append("hasAgentAnsweredUpdatedOn", new util().generateNewDateInYYYYMMDDFormat("UTC"));
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "updateHasAgentAnsweredInOriginateCall", mexc.getMessage());
        }
    }
    
    public void updateTryNextNoInList(String custID,String listID,String nextNoField,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(new util().returnCollNameToSearchContactInfo(listID));
            Document whereQry = new Document("_id", new ObjectId(custID));
            Document updateQry = new Document("tryNextNo", nextNoField);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "updateTryNextNoInList", mexc.getMessage());
        }
    }
    
    public void deleteOriginateCallReq(String extension,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix.concat("_originatecall"));
            Document whereQry = new Document("userExtension",extension);
            whereQry.append("status", "toInitiate");
            collection.deleteMany(whereQry);
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "deleteOriginateCallReq", mexc.getMessage());
        }
    }
    
    public BasicDBList getPreviousInteraction(ArrayList callHistory,MongoClient mongoConn){
        BasicDBList previousInteraction = new BasicDBList();
        try{
            for(Iterator<Object> it=callHistory.iterator();it.hasNext();){
                Document callHistoryObj = (Document) it.next();
                String callCollectionName = callHistoryObj.getString("callsCollectionName");
                String callID = callHistoryObj.getString("callID");
                previousInteraction.add(this.returnInteractionDoc(callID, callCollectionName, mongoConn));
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "getPreviousInteraction", mexc.getMessage());
        }
        return previousInteraction;
    }
    
    public Document returnInteractionDoc(String callID,String callCollectionName,MongoClient mongoConn){
        Document interactionDoc = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(callCollectionName);
            Document whereQry = new Document("_id", new ObjectId(callID));
            Document selectQry = new Document("disposition",1);
            selectQry.append("comments", 1);
            selectQry.append("dependant", 1);
            selectQry.append("extension", 1);
            selectQry.append("eventDateTime", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                interactionDoc = new Document();
                try{
                    interactionDoc.append("disposition", dc.getString("disposition"));
                }
                catch(NullPointerException nexc){
                    LogClass.logMsg("CtiDB", "returnInteractionDoc", nexc.getMessage());
                }
                try{
                    interactionDoc.append("comments", dc.getString("comments"));
                }
                catch(NullPointerException nexc){
                    LogClass.logMsg("CtiDB", "returnInteractionDoc", nexc.getMessage());
                }
                try{
                    interactionDoc.append("extension", dc.getString("extension"));
                }
                catch(NullPointerException nexc){
                    LogClass.logMsg("CtiDB", "returnInteractionDoc", nexc.getMessage());
                }
                try{
                    interactionDoc.append("eventDateTime", dc.getDate("eventDateTime"));
                }
                catch(NullPointerException nexc){
                    LogClass.logMsg("CtiDB", "returnInteractionDoc", nexc.getMessage());
                }
                try{
                    Document dependantObj = (Document) dc.get("dependant");
                    interactionDoc.append("dependant", this.returnDependantAsKeyValueString(dependantObj));
                }
                catch(NullPointerException nexc){
                    LogClass.logMsg("CtiDB", "returnInteractionDoc", nexc.getMessage());
                }
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "returnInteractionDoc", mexc.getMessage());
        }
        return interactionDoc;
    }
    
    public String returnDependantAsKeyValueString(Document dependantDoc){
        String dependantString = "";
        try{
            for(Document.Entry<String,Object> e : dependantDoc.entrySet()){
                dependantString = dependantString.concat(e.getKey().toString()).concat(":");
                try{
                    dependantString = dependantString.concat(e.getValue().toString());
                }
                catch(NullPointerException nexc){
                    LogClass.logMsg("CtiDB", "returnDependantAsKeyValueString", nexc.getMessage());
                }
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDB", "returnDependantAsKeyValueString", ex.getMessage());
        }
        return dependantString;
    }
    
    public void updateLeadStatusInList(String listID,String docID,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(new util().returnCollNameToSearchContactInfo(listID));
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document("leadStatus", "open");
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "updateLeadStatusInList", mexc.getMessage());
        }
    }
    
    public void insertCallBackForCall(String custID,String campaignID,Date followUpDate,String extension,
            String phoneNumber,String serverNamePrefix,String customerName,String phone1,String phone2,String phone3,
            String disposition,String comments,String listID,String callID,String campaignName,MongoClient mongoConn){
        try{
            util utilObj = new util();
            Date dateEntered = utilObj.generateNewDateInYYYYMMDDFormat();
            String mmYYYY = utilObj.getMMYYYYForJavaDate(dateEntered.getTime());
            String callCollectionName = utilObj.returnCollectionNameForCurrentCalls(mmYYYY, serverNamePrefix);
            if(followUpDate==null){
                followUpDate = new util().genDateInYYYYMMDDHHMMSSFormatWithHourDelay();
            }
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix.concat("_").concat(extension).concat("_callback"));
            Document insertDoc = new Document("campaignID", new ObjectId(campaignID));
            insertDoc.append("phoneNumber", phoneNumber);
            insertDoc.append("followUpOn", followUpDate);
            insertDoc.append("custID", new ObjectId(custID));
            insertDoc.append("customerName", customerName);
            insertDoc.append("phone1", phone1);
            insertDoc.append("phone2", phone2);
            insertDoc.append("phone3", phone3);
            insertDoc.append("disposition", disposition);
            insertDoc.append("comments", comments);
            insertDoc.append("listID", listID);
            insertDoc.append("campaignName", campaignName);
            insertDoc.append("dateEntered", dateEntered);
            insertDoc.append("callCollectionName", callCollectionName);
            insertDoc.append("callID", new ObjectId(callID));
            collection.insertOne(insertDoc);
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "insertCallBackForCall", mexc.getMessage());
        }
    }
    
    public void deleteOldCallBackEntryForNumberAndCampaign(String phoneNumber,String campaignID,String extension,
        String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix.concat("_").concat(extension).concat("_callback"));
            Document whereQry = new Document("phoneNumber",phoneNumber);
            whereQry.append("campaignID", new ObjectId(campaignID));
            collection.deleteMany(whereQry);
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "deleteOldCallBackEntryForNumberAndCampaign", mexc.getMessage());
        }
    }
    
    public boolean checkIfCallBackCall(String extension,String serverNamePrefix,String popupID,MongoClient mongoConn){
        boolean callBackCall = false;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(new util().returnCollectionNameForPopup(serverNamePrefix, extension));
            Document whereQry = new Document("_id", new ObjectId(popupID));
            whereQry.append("callType", "CallBack Call");
            Document selectQry = new Document("callType", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                callBackCall = true;
            }
        }
        catch(MongoException mexc){
            
        }
        return callBackCall;
    }
    
    public void deletePopupIfExistsDuringNextCall(String phoneNumber,String extension,String serverNamePrefix,
        MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(new util().returnCollectionNameForPopup(serverNamePrefix, extension));
            String[] phoneFields = {"phoneNumber"};
            /*BasicDBList orCond = new GetBasicContactInfo().returnOrCondtionForContactSearch(phoneNumber,phoneFields);
            Document whereQry = new Document("$or", orCond);
            collection.deleteMany(whereQry);*/
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "deletePopupIfExistsDuringNextCall", ex.getMessage());
        }
    }

    public String getPopupIDFromNumber(String phoneNumber, String extension,String serverNamePrefix) {
        String docID = null;
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection =mongoDB.getCollection(serverNamePrefix.concat("_"+extension+"_Popup"));
            Document whereQuery = new Document("phoneNumber",phoneNumber);
            Document selectQuery = new Document("_id",1);
            MongoCursor cursor = collection.find(whereQuery).projection(selectQuery).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                docID = dc.getObjectId("_id").toString();
            }
        }
        catch(MongoException mecx){
            mecx.getMessage();
        }
        return docID;
    }
    
    public String getPopupIDUsingCallID(String callID,String extension,String serverNamePrefix,MongoClient mongoConn){
        String popupID = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix.concat("_"+extension+"_Popup"));
            Document whereQry = new Document("callID", new ObjectId(callID));
            Document selectQry = new Document("_id", 1);
            MongoCursor<Document> cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                popupID = dc.getObjectId("_id").toString();
            }
        }
        catch(Exception ex){
            
        }
        return popupID;
    }
    
    public void updateModuleLinkedCustIDInCalls(String custID,String callId,String serverPrefix,String mmYYYY,
        MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(new util().returnCollectionNameForCurrentCalls(mmYYYY, serverPrefix));
            Document whereQry = new Document("_id", new ObjectId(callId));
            Document updateQry = new Document("mysqlCustId", custID);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(Exception ex){
            
        }
    }
    
    /*
        Get CallConnectTime for CallId
    */
    public boolean checkIfPopupToBeDeleted(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        boolean deletePopup = false;
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            whereQry.append("callConnectTime", null);
            whereQry.append("callDirection", "incoming");
            whereQry.append("Join", new Document("$size", 1));
            if(collection.count(whereQry)>0){
                deletePopup = true;
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ProcessForReportsDB", "getCallConnectTime", mexc.getMessage());
        }
        return deletePopup;
    }
    
    public String getPopupIDForCallID(String callDocID,String extension,String serverPrefix,MongoClient mongoConn){
        String popupId = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            Document selectQry = new Document("_id", 1);
            Document whereQry = new Document("callID", new ObjectId(callDocID));
            MongoCollection collection = mongoDB.getCollection(new util().returnCollectionNameForPopup(serverPrefix, extension));
            MongoCursor<Document> cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                popupId = dc.getObjectId("_id").toString();
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CtiDB", "getPopupIDForCallID", mexc.getMessage());
        }
        return popupId;
    }
}
