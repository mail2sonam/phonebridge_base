/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import singleton.db.DBClass;
import processdbevents.model.CdrModel;
import phonebridge.util.LogClass;
import java.util.ArrayList;
import java.util.Iterator;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author sonamuthu
 */
public class CdrDB {
    public void insertProcessedCDR(Document cdrDocument,String collectionName,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(collectionName);
            collection.insertOne(cdrDocument);
        }
        catch(MongoException mexc){
            LogClass.logMsg("CdrDB","insertProcessedCDR", mexc.getMessage());
        }
    }
    
    public String getTransferedFrom(String uniqueID,String logCollectionName,MongoClient mongoConn){
        String transferedFrom = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(logCollectionName);
            Document whereQuery = new Document();
            whereQuery.append("eventName", "Transfer");
            whereQuery.append("targetUniqueId", uniqueID);
            Document selectQry = new Document("channel", 1);
            MongoCursor cursor = collection.find(whereQuery).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                transferedFrom = dc.getString("channel");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CdrDB","getTransferedFrom", mexc.getMessage());
        }
        return transferedFrom;
    }
    
    public CdrModel getRecordingFileNameForUniqueID(String docID,String uniqueID,String serverNamePrefix,MongoClient mongoConn){
        CdrModel cdr = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix.concat("_currentCalls"));
            Document whereQry = new Document("_id", new ObjectId(docID));
            whereQry.append("Cdr", new Document("$elemMatch", new Document("uniqueID", uniqueID)));
            Document selectQry = new Document("Cdr.$", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                ArrayList cdrFromDB = (ArrayList)dc.get("Cdr");
                for(Iterator<Object> it=cdrFromDB.iterator();it.hasNext();){
                    Document documentTrunkObject = (Document)it.next();
                    cdr = convertDBObjecttoJavaObject(documentTrunkObject);
                }
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CdrDB","getRecordingFileNameForUniqueID", mexc.getMessage());
        }
        return cdr;
    }
    
    private CdrModel convertDBObjecttoJavaObject(Document dc){
        CdrModel cdr = null;
        if(dc==null)
            return cdr;
        cdr = new CdrModel();
        cdr.setUserField(dc.getString("userField"));
        return cdr;
    }
}
