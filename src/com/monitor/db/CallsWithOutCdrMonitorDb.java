/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.monitor.db;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.monitor.model.CallsWithOutCdrMonitor;
import java.util.ArrayList;
import java.util.Date;
import org.bson.Document;
import org.joda.time.DateTime;
import phonebridge.util.util;
import singleton.db.DBClass;

/**
 *
 * @author bharath
 */
public class CallsWithOutCdrMonitorDb {
    public ArrayList<CallsWithOutCdrMonitor> getCallsWithOutCdr(String serverNamePrefix,Date fromDate,Date toDate){
        ArrayList<CallsWithOutCdrMonitor> callsWithOutCdr = new ArrayList<>();
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            util utilObj = new util();
            ArrayList<String> mmYYYY = utilObj.getMMYYYYForTwoDate(fromDate, toDate);
            DateTime jodaFromDate = utilObj.convertJavaDateToMongoDate(fromDate.getTime());
            DateTime jodaToDate = utilObj.convertJavaDateToMongoDate(toDate.getTime());
            
            Document whereQry = new Document();
            Document dateQry = new Document("$gte", jodaFromDate.toDate());
            dateQry.append("$lte", jodaToDate.toDate());
            whereQry.append("eventDateTime", dateQry);
            whereQry.append("Cdr", new Document("$size", 0));
            
            Document sortQry = new Document("eventDateTime", -1);
            
            Document selectQry = new Document("eventDateTime", 1);
            selectQry.append("callDirection", 1);
            selectQry.append("isClosed", 1);
            
            for(String eachMMYYYY : mmYYYY){
                MongoCollection collection = mongoDB.getCollection(utilObj.returnCollectionNameForCurrentCalls(eachMMYYYY, serverNamePrefix));
                MongoCursor<Document> cursor = collection.find(whereQry).projection(selectQry).sort(sortQry).iterator();
                while(cursor.hasNext()){
                    Document dc = cursor.next();
                    callsWithOutCdr.add(this.convertDbObjToJavaObj(dc));
                }
            }
        }
        catch(Exception ex){
            
        }
        return callsWithOutCdr;
    }
    
    private CallsWithOutCdrMonitor convertDbObjToJavaObj(Document dc){
        CallsWithOutCdrMonitor callsWithOutCdrMonitor = new CallsWithOutCdrMonitor();
        String docID = null;
        String callDirection = null;
        Date eventDateTime = null;
        boolean callClosed = false;
        
        try{
            docID = dc.getObjectId("_id").toString();
            callsWithOutCdrMonitor.setDocID(docID);
        }
        catch(Exception ex){
            
        }
        try{
            callDirection = dc.getString("callDirection");
            callsWithOutCdrMonitor.setCallDirection(callDirection);
        }
        catch(Exception ex){
            
        }
        try{
            eventDateTime = dc.getDate("eventDateTime");
            callsWithOutCdrMonitor.setEventDateTime(eventDateTime);
        }
        catch(Exception ex){
            
        }
        try{
            callClosed = dc.getBoolean("isClosed");
            callsWithOutCdrMonitor.setCallClosed(callClosed);
        }
        catch(Exception ex){
            
        }
        return callsWithOutCdrMonitor;
    }
}
