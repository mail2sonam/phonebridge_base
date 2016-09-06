/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridgelogger.db;

import singleton.db.DBClass;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import phonebridge.util.LogClass;
import phonebridge.util.util;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author bharath
 */
public class EventsDB {
    public void insertEventsIntoDB(Document insertDoc,String serverID,String collectionName,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(collectionName);
            Document indexQry = new Document("eventtime", 1);
            collection.createIndex(indexQry);
            insertDoc.append("retrievedForProcessingAt",null);
            insertDoc.append("completedProcessingAt",null);
            insertDoc.append("serverID",new ObjectId(serverID));
            insertDoc.append("eventtime", new util().generateNewDateInYYYYMMDDFormat());
            collection.insertOne(insertDoc);
        }
        catch(MongoException mexc){
            LogClass.logMsg("EventsDB","insertEventsIntoDB", mexc.getMessage());
        }
    }
    
    public void deleteOriginateCallsForServer(String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix.concat("_originatecall"));
            collection.dropCollection();
        }
        catch(Exception ex){
            LogClass.logMsg("EventsDAO", "deleteOriginateCallsForServer", ex.getMessage());
        }
    }
}
