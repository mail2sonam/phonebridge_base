/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridgelogger.db;

import singleton.db.DBClass;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import phonebridgelogger.model.Trunk;
import phonebridge.util.LogClass;
import java.util.ArrayList;
import java.util.Iterator;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author sonamuthu
 */
public class TrunkDB {
    public ArrayList<Trunk> getTrunkForServerID(String serverID) {
        MongoClient mongoConn = null;
        ArrayList<Trunk> trunkDet = new ArrayList<Trunk>();
        try{
            mongoConn =  DBClass.getInstance().getConnection();
            MongoDatabase mongoDatabase = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDatabase.getCollection("server");
            BasicDBObject searchQuery = new BasicDBObject("_id",new ObjectId(serverID));
            BasicDBObject displayQuery = new BasicDBObject("trunk",1);
            MongoCursor cursor = collection.find().filter(searchQuery).projection(displayQuery).iterator();
            while(cursor.hasNext()){
                Document ob = (Document)cursor.next();
                ArrayList trunkList = (ArrayList)ob.get("trunk");
                
                for(Iterator<Object> it=trunkList.iterator();it.hasNext();){
                    Document documentTrunkObject = (Document)it.next();
                    trunkDet.add(convertDBObjecttoJavaObject(documentTrunkObject));
                }
            }   
        }
        catch(Exception mexc){
            LogClass.logMsg("TrunkDB","getTrunkForServerID", mexc.getMessage());
        }
        finally{
            
                //mongoConn.close();
           
        }
        return trunkDet;
    }
    
    private Trunk convertDBObjecttoJavaObject(Document retrievedDoc){
        if(retrievedDoc==null)
            return null;
        String prefix = retrievedDoc.getString("prefix");
        String trunkValue = retrievedDoc.getString("trunkValue");
        String cdrTrunkValue = retrievedDoc.getString("cdrTrunkValue");
        Trunk trunk = new Trunk(prefix,trunkValue,cdrTrunkValue);
        return trunk;
    }
}
