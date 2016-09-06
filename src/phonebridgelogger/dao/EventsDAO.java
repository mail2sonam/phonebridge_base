/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridgelogger.dao;

import com.mongodb.MongoClient;
import phonebridgelogger.db.EventsDB;
import phonebridgelogger.model.Server;
import phonebridge.util.LogClass;
import phonebridge.util.util;
import org.bson.Document;

/**
 *
 * @author bharath
 */
public class EventsDAO {
    public void logEventsInDB(Document events,MongoClient mongoConn,EventsDB eventsModelDB,Server serverDetails,util utilObj){
        String eventLogCollectionName = null;
        try{
            eventLogCollectionName = utilObj.generateCollectionNameForEventsLog(serverDetails.getServerNamePrefix());
            eventsModelDB.insertEventsIntoDB(events,serverDetails.getServerID(),eventLogCollectionName,mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("EventsDAO", "logEventsInDB", ex.getMessage());
        }
    }
    
    public void deleteOriginateCallsForServer(String serverNamePrefix,EventsDB eventsDb,MongoClient mongoConn){
        try{
            eventsDb.deleteOriginateCallsForServer(serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("EventsDAO", "deleteOriginateCallsForServer", ex.getMessage());
        }
    }
    
}
