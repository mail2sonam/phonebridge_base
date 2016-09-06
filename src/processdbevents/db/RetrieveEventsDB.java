/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.db;

import phonebridge.util.LogClass;
import phonebridge.util.util;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import singleton.db.DBClass;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author bharath
 */

public class RetrieveEventsDB {
    public Document getEventsFromCollection(String collName,String serverID,MongoClient mongoConn){
        Document eventsFromDB = null;
        try{
            //System.out.println("ENTERED TRY WITH COLL NAME "+collName);
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(collName);
            //Document whereQry = new Document();
            //whereQry.append("serverID", new ObjectId(serverID));
            //whereQry.append("_id", new ObjectId("55360e6a9bdeb628bb4b3121"));
            Document sortQry = new Document("eventtime",1);
            MongoCursor cursor = collection.find().sort(sortQry).limit(1).iterator();
            //System.out.println("BEFORE CURSOR HAS NEXT");
            if(cursor.hasNext()){
                eventsFromDB = (Document) cursor.next();
                System.out.println("SENDING DOC FOR PROCESSING "+eventsFromDB.getObjectId("_id"));
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("RetrieveEventsDB", "getEventsFromCollection", mexc.getMessage());
        }
        return eventsFromDB;
    }
    
    public void moveProcessedEventsToAnotherCollection(ObjectId docID,Long eventTime,String collName,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection fromCollection = mongoDB.getCollection(collName);
            MongoCollection toCollection = mongoDB.getCollection(collName.concat("_proccessed").
                    concat("_".concat(new util().getDDMMForJavaDate(eventTime))));
            /*
                1) Added index in the collection to retrieve data based on channel
                2) Done by N.R.Bharath
            */
            Document indexQry = new Document("eventtime", 1);
            toCollection.createIndex(indexQry);
            indexQry = new Document("channel", 1);
            toCollection.createIndex(indexQry);
            indexQry = new Document("destination", 1);
            toCollection.createIndex(indexQry);
            indexQry = new Document("channel1", 1);
            toCollection.createIndex(indexQry);
            indexQry = new Document("channel2", 1);
            toCollection.createIndex(indexQry);
            indexQry = new Document("destinationchannel", 1);
            toCollection.createIndex(indexQry);
            
            Document whereQry = new Document("_id", docID);
            MongoCursor cursor = fromCollection.find(whereQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                try{
                    toCollection.insertOne(dc);
                }
                catch(MongoException mexc){
                    toCollection = mongoDB.getCollection(collName.concat("_exception"));
                    toCollection.insertOne(dc);
                }
                fromCollection.deleteOne(whereQry);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("RetrieveEventsDB", "moveProcessedEventsToAnotherCollection", mexc.getMessage());
        }
    }
}
