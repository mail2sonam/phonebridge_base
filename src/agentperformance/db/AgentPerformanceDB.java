/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentperformance.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.Date;
import singleton.db.DBClass;
import phonebridge.util.LogClass;
import phonebridge.util.util;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author bharath
 */
public class AgentPerformanceDB {
    public String checkIfDocExists(String extension,String campaignID,String serverNamePrefix,MongoClient mongoConn){
        String docID = null;
        try{
            util utilObj = new util();
            MongoCollection collection = this.returnCollection(mongoConn, serverNamePrefix, extension);
            Document whereQry = new Document("campaignID", new ObjectId(campaignID));
            whereQry.append("forDate", utilObj.addDateConditionToWhereQry("$gte"));
            Document selectQry = new Document("_id",1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                docID = dc.getObjectId("_id").toString();
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("AgentPerformanceDB", "checkIfDocExists", mexc.getMessage());
        }
        return docID;
    }
    
    public String insertNewDoc(String extension,String campaignID,String serverNamePrefix,MongoClient mongoConn){
        ObjectId docID = null;
        try{
            MongoCollection collection = this.returnCollection(mongoConn, serverNamePrefix, extension);
            docID = new ObjectId();
            Document insertDoc = new Document("_id", docID);
            insertDoc.append("extension", extension);
            insertDoc.append("campaignID", new ObjectId(campaignID));
            insertDoc.append("totalCallCnt", 0);
            insertDoc.append("totalAnswered", 0);
            insertDoc.append("totalTalkTime", 0.00);
            insertDoc.append("avgTalkTime", 0.00);
            insertDoc.append("totalWrapTime", 0.00);
            insertDoc.append("avgWrapTime", 0.00);
            insertDoc.append("totalConnectTime", 0.00);
            insertDoc.append("avgConnectTime", 0.00);
            insertDoc.append("callInititateTime", null);
            insertDoc.append("callAnswerTime", null);
            insertDoc.append("noOfBreaks", 0);
            insertDoc.append("conversionDisposition", new Document());
            insertDoc.append("forDate", new util().generateNewDate("UTC"));
            collection.insertOne(insertDoc);
        }
        catch(MongoException mexc){
            LogClass.logMsg("AgentPerformanceDB", "insertNewDoc", mexc.getMessage());
        }
        return docID.toString();
    }
    
    private MongoCollection returnCollection(MongoClient mongoConn,String serverNamePrefix,String extension){
        MongoCollection collection = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            collection = mongoDB.getCollection(new util().returnCollNameForPerformanceRpt(serverNamePrefix, extension));
        }
        catch(MongoException mexc){
            LogClass.logMsg("AgentPerformanceDB", "returnCollection", mexc.getMessage());
        }
        return collection;
    }
    
    public int getIntDataFromDoc(String fieldName,String docID,String extension,String campaignID,String serverNamePrefix,
            MongoClient mongoConn){
        int value = 0;
        try{
            MongoCollection collection = this.returnCollection(mongoConn, serverNamePrefix, extension);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document selectQry = new Document(fieldName, 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                value = dc.getInteger(fieldName);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("AgentPerformanceDB", "getDataFromDoc", mexc.getMessage());
        }
        return value;
    }
    
    public double getDoubleDataFromDoc(String fieldName,String docID,String extension,String campaignID,String serverNamePrefix,
            MongoClient mongoConn){
        double value = 0;
        try{
            MongoCollection collection = this.returnCollection(mongoConn, serverNamePrefix, extension);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document selectQry = new Document(fieldName, 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                value = dc.getDouble(fieldName);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("AgentPerformanceDB", "getDataFromDoc", mexc.getMessage());
        }
        return value;
    }
    
    public void incrementFieldInDoc(String fieldName,String docID,String extension,String campaignID,
        String serverNamePrefix,String prefixForSaving,MongoClient mongoConn){
        try{
            MongoCollection collection = this.returnCollection(mongoConn, serverNamePrefix, extension);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document();
            if(prefixForSaving!=null)
                updateQry = new Document(prefixForSaving.concat(fieldName), 1);
            else
                updateQry = new Document(fieldName, 1);
            collection.updateOne(whereQry, new Document("$inc", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("AgentPerformanceDB", "getDataFromDoc", mexc.getMessage());
        }
    }
    
    public void updateFieldInDoc(String fieldName,String docID,double updateVal,String extension,String campaignID,
            String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoCollection collection = this.returnCollection(mongoConn, serverNamePrefix, extension);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document(fieldName, updateVal);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("AgentPerformanceDB", "updateFieldInDoc", mexc.getMessage());
        }
    }
    
    public void updateTimeInDoc(String docID,Date dateToUpdate,String fieldName,String extension,String serverNamePrefix,
        MongoClient mongoConn){
        try{
            MongoCollection collection = this.returnCollection(mongoConn, serverNamePrefix, extension);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document updateQry = new Document(fieldName, dateToUpdate);
            collection.updateOne(whereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("AgentPerformanceDB", "updateTimeInDoc", mexc.getMessage());
        }
    }
    
    public Date getDateDataFromDoc(String fieldName,String docID,String extension,String campaignID,String serverNamePrefix,
            MongoClient mongoConn){
        Date value = null;
        try{
            MongoCollection collection = this.returnCollection(mongoConn, serverNamePrefix, extension);
            Document whereQry = new Document("_id", new ObjectId(docID));
            Document selectQry = new Document(fieldName, 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                value = dc.getDate(fieldName);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("AgentPerformanceDB", "getDateDataFromDoc", mexc.getMessage());
        }
        return value;
    }
}
