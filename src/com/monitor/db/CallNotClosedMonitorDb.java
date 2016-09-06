/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.monitor.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.monitor.model.CallDirectionMonitor;
import com.monitor.model.CallNotClosedMonitor;
import java.util.ArrayList;
import java.util.Date;
import org.bson.Document;
import org.joda.time.DateTime;
import phonebridge.util.util;
import processdbevents.dao.CdrDAO;
import processdbevents.dao.DialBeginDAO;
import processdbevents.dao.HangUpDAO;
import processdbevents.model.CdrModel;
import processdbevents.model.DialBeginModel;
import processdbevents.model.HangupModel;
import singleton.db.DBClass;

/**
 *
 * @author bharath
 */
public class CallNotClosedMonitorDb {
    public ArrayList<CallNotClosedMonitor> getCallsNotClosed(String serverNamePrefix,Date fromDate,Date toDate){
        ArrayList<CallNotClosedMonitor> callsNotClosed = new ArrayList<>();
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            util utilObj = new util();
            ArrayList<String> mmYYYY = utilObj.getMMYYYYForTwoDate(fromDate, toDate);
            DateTime jodaFromDate = utilObj.convertJavaDateToMongoDate(fromDate.getTime());
            DateTime jodaToDate = utilObj.convertJavaDateToMongoDate(toDate.getTime());
            Document whereQry = new Document("isClosed", false);
            Document dateQry = new Document("$gte", jodaFromDate.toDate());
            dateQry.append("$lte", jodaToDate.toDate());
            whereQry.append("eventDateTime", dateQry);
            
            Document sortQry = new Document("eventDateTime", -1);
            
            Document selectQry = new Document("HangUp", 1);
            selectQry.append("Cdr", 1);
            selectQry.append("callDirection", 1);
            selectQry.append("eventDateTime", 1);
            
            for(String eachMMYYYY : mmYYYY){
                MongoCollection collection = mongoDB.getCollection(utilObj.returnCollectionNameForCurrentCalls(eachMMYYYY, serverNamePrefix));
                MongoCursor<Document> cursor = collection.find(whereQry).projection(selectQry).iterator();
                while(cursor.hasNext()){
                    Document dc = cursor.next();
                    callsNotClosed.add(this.convertDbObjToJavaObj(dc));
                }
            }
        }
        catch(MongoException ex){
            
        }
        return callsNotClosed;
    }
    
    private CallNotClosedMonitor convertDbObjToJavaObj(Document dc){
        CallNotClosedMonitor callNotClosed = new CallNotClosedMonitor();
        String docID = null;
        Date eventDateTime = null;
        String callDirection = null;
        ArrayList<HangupModel> hangUp = new ArrayList<>();
        ArrayList<CdrModel> cdr = new ArrayList<>();
        
        try{
            docID = dc.getObjectId("_id").toString();
            callNotClosed.setDocID(docID);
        }
        catch(Exception ex){
            
        }
        try{
            eventDateTime = dc.getDate("eventDateTime");
            callNotClosed.setEventDateTime(eventDateTime);
        }
        catch(Exception ex){
            
        }
        try{
            callDirection = dc.getString("callDirection");
            callNotClosed.setCallDirection(callDirection);
        }
        catch(Exception ex){
            
        }
        try{
            ArrayList<Document> hangUpArr = (ArrayList<Document>) dc.get("HangUp");
            HangUpDAO hangUpDao = new HangUpDAO();
            for(Document eachHangUp : hangUpArr){
                hangUp.add(hangUpDao.createHangupObjectFromCurrentCalls(eachHangUp));
            }
            callNotClosed.setHangUp(hangUp);
        }
        catch(Exception ex){
            
        }
        try{
            ArrayList<Document> cdrArr = (ArrayList<Document>) dc.get("Cdr");
            CdrDAO cdrDao = new CdrDAO();
            for(Document eachCdr : cdrArr){
                cdr.add(cdrDao.createCdrObjectFromCurrentCalls(eachCdr));
            }
            callNotClosed.setCdr(cdr);
        }
        catch(Exception ex){
            
        }
        return callNotClosed;
    }
}
