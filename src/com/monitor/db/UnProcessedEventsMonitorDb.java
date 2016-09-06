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
import com.monitor.model.UnProcessedEventsMonitor;
import java.util.ArrayList;
import java.util.Date;
import org.bson.Document;
import singleton.db.DBClass;

/**
 *
 * @author bharath
 */
public class UnProcessedEventsMonitorDb {
    public ArrayList<UnProcessedEventsMonitor> getAllUnProcessedEvents(String serverNamePrefix){
        ArrayList<UnProcessedEventsMonitor> unProcessedEvents = new ArrayList<>();
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("events_".concat(serverNamePrefix));
            Document sortQry = new Document("eventtime", 1);
            Document selectQry = new Document("event", 1);
            selectQry.append("eventtime", 1);
            MongoCursor<Document> cursor = collection.find().projection(selectQry).sort(sortQry).iterator();
            while(cursor.hasNext()){
                Document dc = cursor.next();
                unProcessedEvents.add(this.convertDBObjToJavaObj(dc));
            }
        }
        catch(Exception ex){
            
        }
        return unProcessedEvents;
    }
    
    private UnProcessedEventsMonitor convertDBObjToJavaObj(Document dc){
        UnProcessedEventsMonitor unProcessedEventsMonitor = new UnProcessedEventsMonitor();
        String docID = null;
        String event = null;
        Date eventTime = null;
        
        try{
            docID = dc.getObjectId("_id").toString();
            unProcessedEventsMonitor.setDocID(docID);
        }
        catch(Exception ex){
            
        }
        try{
            event = dc.getString("event");
            unProcessedEventsMonitor.setEventName(event);
        }
        catch(Exception ex){
            
        }
        try{
            eventTime = dc.getDate("eventtime");
            unProcessedEventsMonitor.setEventDateTime(eventTime);
        }
        catch(Exception ex){
            
        }
        return unProcessedEventsMonitor;
    }
}
