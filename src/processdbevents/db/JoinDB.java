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
import java.util.Date;
import java.util.Iterator;
import org.bson.Document;
import org.bson.types.ObjectId;
import processdbevents.model.JoinModel;

/**
 *
 * @author bharath
 */
public class JoinDB {
    public Date getQueueJoinTime(String docID,String mmYYYY, String serverNamePrefix, MongoClient mongoConn){
        Date joinTime = null;
        try{
            MongoCollection collection = new ProcessAMIDB().returnMongoCollection(mmYYYY, serverNamePrefix, mongoConn);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document selectQry = new Document("Join.eventTime", 1);
            MongoCursor cursor = collection.find(whereQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                ArrayList tempArr = (ArrayList) dc.get("Join");
                
                for(Iterator<Object> it=tempArr.iterator();it.hasNext();){
                    Document tempDoc = (Document)it.next();
                    JoinModel join = this.convertDBObjectToJavaObject(tempDoc);
                    joinTime = join.getEventTime();
                }
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("JoinDB", "getQueueJoinTime", mexc.getMessage());
        }
        return joinTime;
    }
    
    private JoinModel convertDBObjectToJavaObject(Document dc){
        JoinModel join = new JoinModel();
        if(dc==null)
            return join;
        join.setEventTime(dc.getDate("eventTime"));
        return join;
    }
}
