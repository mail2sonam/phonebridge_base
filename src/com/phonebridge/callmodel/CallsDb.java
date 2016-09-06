/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridge.callmodel;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Date;
import org.bson.Document;
import org.bson.types.ObjectId;
import phonebridge.util.util;
import singleton.db.DBClass;

/**
 *
 * @author bharath
 */
public class CallsDb {
    public CallModel getCallDataForId(String callId,String serverPrefix,String mmYYYY){
        CallModel callDetails = null;
        MongoClient mongoConn = DBClass.getInstance().getConnection();
        MongoDatabase mongoDb = mongoConn.getDatabase(DBClass.MDATABASE);
        util utilObj = new util();
        MongoCollection collection = mongoDb.getCollection(utilObj.returnCollectionNameForCurrentCalls(mmYYYY, serverPrefix));
        Document whereQry = new Document("_id", new ObjectId(callId));
        
        Document selectQry = new Document();
        selectQry.append("documentCreatedOn", 0);
        selectQry.append("isClosed",0);
        selectQry.append("insertedEvent", 0);
        selectQry.append("NewState", 0);
        selectQry.append("Join", 0);
        selectQry.append("Leave", 0);
        selectQry.append("DialBegin", 0);
        selectQry.append("Bridge", 0);
        selectQry.append("DialEnd", 0);
        selectQry.append("HangUp", 0);
        selectQry.append("Cdr", 0);
        selectQry.append("Hold", 0);
        selectQry.append("DTMF", 0);
        selectQry.append("Transfer", 0);
        selectQry.append("QueueCallerAbandon", 0);
        selectQry.append("channelsFoundInCall",0);
        selectQry.append("transferedChannel",0);
        selectQry.append("isTransfered", 0);
        selectQry.append("disposition", 0);
        selectQry.append("dependant", 0);
        selectQry.append("comments", 0);
        selectQry.append("wrapUpTime", 0);
        selectQry.append("wrapUpTimeInSecs", 0);
        
        MongoCursor<Document> cursor = collection.find(whereQry).projection(selectQry).iterator();
        if(cursor.hasNext()){
            Document dc = cursor.next();
            callDetails = this.convertDbObjToJavaObj(dc);
        }
        return callDetails;
    }
    
    public void updateCurlStatus(String status,String callId,String serverPrefix,String mmYYYY){
        MongoClient mongoConn = DBClass.getInstance().getConnection();
        MongoDatabase mongoDb = mongoConn.getDatabase(DBClass.MDATABASE);
        util utilObj = new util();
        MongoCollection collection = mongoDb.getCollection(utilObj.returnCollectionNameForCurrentCalls(mmYYYY, serverPrefix));
        Document whereQry = new Document("_id", new ObjectId(callId));
        Document updateQry = new Document("curlStatusToCRM", status);
        updateQry.append("curlStatusUpdateTime", new Date());
        collection.updateOne(whereQry, new Document("$set", updateQry));
    }
    
    private CallModel convertDbObjToJavaObj(Document dc){
        CallModel callDetails = null;
        if(dc.isEmpty()){
            return callDetails;
        }
        String custId = null;
        String allExtensions = "";
        String recordings = "";
        double duration = 0;
        String callDirection = null;
        String serverId = null;
        String dependant = null;
        String transferDetails = "";
        String campaignName = null;
        double callRingingDuration = 0;
        double queueWaitTime = 0;
        String hangUpReason = null;
        String callType = null;
        String dialMethod = null;
        double wrapUpTimeSecs = 0;
        String hoppingDetails = "";
        Date answerTime = null;
        Date endTime = null;
        Date wrapUpTime = null;
        String uniqueId = null;
        String disposition = null;
        String comments = null;
        String phoneNumber = null;
        String extension = null;
        Date callStartTime = null;
        Date ivrEntryTime = null;
        String callStatus = null;
        String queueNumber = null;
        Date queueJoinTime = null;
        boolean BCCBCall = false;
        
        try{
                custId = (String) dc.get("custID");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            ArrayList<String> allExtensionsArr = (ArrayList<String>) dc.get("extensionsInCall");
            for(String eachExtension : allExtensionsArr){
                if(allExtensions.length()>0)
                    allExtensions = allExtensions.concat(",");
                allExtensions = allExtensions.concat(eachExtension);
            }
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            ArrayList<String> recordingsArr = (ArrayList<String>) dc.get("recordings");
            for(String eachRec : recordingsArr){
                recordings = recordings.concat(",").concat(eachRec);
            }
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            duration = dc.getDouble("answerDuration");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            ArrayList<Document> transferArr = (ArrayList<Document>) dc.get("TransferDetails");
            for(Document eachDoc : transferArr){
                if(transferDetails.length()>0)
                    transferDetails = transferDetails.concat(",");
                String transDet = eachDoc.getString("transferedFrom").concat("->").concat(eachDoc.getString("transferedTo"));
                transferDetails = transferDetails.concat(transDet);
            }
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            callRingingDuration = dc.getDouble("callConnectTimeInSecs");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            wrapUpTimeSecs = dc.getDouble("wrapUpTimeInSecs");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            callDirection = dc.getString("callDirection");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            campaignName = dc.getString("campaignName");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            hangUpReason = dc.getString("hangUpReason");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            callType = dc.getString("callType");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            dialMethod = dc.getString("typeOfDialer");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            answerTime = dc.getDate("callConnectTime");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            endTime = dc.getDate("hangUpTime");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            wrapUpTime = dc.getDate("wrapUpTime");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            dependant = new util().chageDependantToString(dc.get("dependant"));
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            uniqueId = dc.getString("callUniqueID");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            ArrayList<Document> hoppingDetArr = (ArrayList<Document>) dc.get("hoppingDetails");
            for(Document eachHopping : hoppingDetArr){
                if(hoppingDetails.length()>0)
                    hoppingDetails = hoppingDetails.concat(",");
                String hoppingDet = dc.getString("extension").concat("-").concat(new util().generateDateInDDMMYYYYHHMMSSFormat(
                    dc.getDate("hoppedTime"), "IST"));
                hoppingDetails = hoppingDetails.concat(hoppingDet);
            }
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            disposition = dc.getString("disposition");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            comments = dc.getString("comments");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            phoneNumber = dc.getString("phoneNumber");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            extension = dc.getString("extension");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            callStartTime = dc.getDate("eventDateTime");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            callStatus = dc.getString("callStatus");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            ivrEntryTime = dc.getDate("ivrEntryTime");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            queueNumber = dc.getString("queueNumber");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            queueJoinTime = dc.getDate("queueJoinTime");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        try{
            BCCBCall = dc.getBoolean("BCCBCall");
        }
        catch(Exception ex){
            System.out.println("ERROR IN CALLSDB "+ex.getMessage());
        }
        
        
        callDetails = new CallModel(custId,dc.getObjectId("_id").toString(), phoneNumber, extension, 
            allExtensions, recordings, callStartTime, answerTime, endTime, wrapUpTime, duration, 
            callRingingDuration, wrapUpTimeSecs, callDirection, callStatus, null, disposition,
            dependant, comments, transferDetails, uniqueId, ivrEntryTime, queueNumber,
            queueJoinTime, queueWaitTime, campaignName, hangUpReason, callType, dialMethod, hoppingDetails,
            BCCBCall
        );
        
        return callDetails;
    }
    
    public CallModel getDispositionAndDependantData(String callId,String mmYYYY,String serverPrefix){
        CallModel callDetails = null;
        MongoClient mongoConn = DBClass.getInstance().getConnection();
        MongoDatabase mongoDb = mongoConn.getDatabase(DBClass.MDATABASE);
        util utilObj = new util();
        MongoCollection collection = mongoDb.getCollection(utilObj.returnCollectionNameForCurrentCalls(mmYYYY, serverPrefix));
        Document whereQry = new Document("_id", new ObjectId(callId));
        
        Document selectQry = new Document();
        selectQry.append("disposition", 1);
        selectQry.append("dependant", 1);
        selectQry.append("comments", 1);
        selectQry.append("wrapUpTime", 1);
        selectQry.append("wrapUpTimeInSecs", 1);
        selectQry.append("custID", 1);
        
        MongoCursor<Document> cursor = collection.find(whereQry).projection(selectQry).iterator();
        if(cursor.hasNext()){
            Document dc = cursor.next();
            callDetails = this.convertDbObjToJavaObj(dc);
        }
        return callDetails;
    }
}