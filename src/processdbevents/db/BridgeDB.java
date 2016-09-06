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
import phonebridge.util.LogClass;
import java.util.ArrayList;
import java.util.Iterator;
import org.bson.Document;
import org.bson.types.ObjectId;
import processdbevents.model.BridgeModel;

/**
 *
 * @author bharath
 */
public class BridgeDB {
    public BridgeModel getFirstBridgeTime(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        BridgeModel bridge = null;
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            whereQry.append("Bridge", new Document("$elemMatch", new Document("bridgeState", "Link")));
            Document selectQry = new Document("Bridge.$", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                ArrayList bridgeFromDB = (ArrayList)dc.get("Bridge");
                
                for(Iterator<Object> it=bridgeFromDB.iterator();it.hasNext();){
                    Document documentTrunkObject = (Document)it.next();
                    bridge = convertDBObjecttoJavaObject(documentTrunkObject);
                }
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("BridgeDB", "getFirstBridgeTime", mexc.getMessage());
        }
        return bridge;
    }
    
    private BridgeModel convertDBObjecttoJavaObject(Document dc){
        BridgeModel bridge = null;
        if(dc==null)
            return bridge;
        bridge = new BridgeModel();
        bridge.setEventTime(dc.getDate("eventTime"));
        return bridge;
    }
}
