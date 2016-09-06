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
import processdbevents.model.NewStateModel;

/**
 *
 * @author bharath
 */
public class NewStateDB {
    public NewStateModel getNewStateDataFromDB(String docID,String uniqueID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        NewStateModel newState = null;
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            whereQry.append("callDirection", "incoming");
            whereQry.append("NewState", new Document("$elemMatch", new Document("uniqueID", uniqueID)));
            Document selectQry = new Document("NewState.$", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                ArrayList bridgeFromDB = (ArrayList)dc.get("NewState");
                
                for(Iterator<Object> it=bridgeFromDB.iterator();it.hasNext();){
                    Document documentTrunkObject = (Document)it.next();
                    newState = convertDBObjecttoJavaObject(documentTrunkObject);
                }
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("NewStateDB", "getNewStateDataFromDB", mexc.getMessage());
        }
        return newState;
    }
    private NewStateModel convertDBObjecttoJavaObject(Document dc){
        NewStateModel newState = null;
        if(dc==null)
            return newState;
        newState = new NewStateModel();
        newState.setEventTime(dc.getDate("eventTime"));
        return newState;
    }
}
