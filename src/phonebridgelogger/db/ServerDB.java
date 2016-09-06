/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridgelogger.db;

import singleton.db.DBClass;
import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import phonebridgelogger.dao.TrunkDAO;
import phonebridgelogger.model.Server;
import phonebridgelogger.model.Trunk;
import phonebridge.util.LogClass;
import phonebridge.util.util;
import java.util.ArrayList;
import java.util.Date;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author sonamuthu
 */
public class ServerDB {
    
    protected Server convertDBObjecttoJavaObject(Document retrievedDoc){
        Server server = null;
        if(retrievedDoc==null)
            return server;
        int amiPort;
        int sshPort;
        String prefix = null;
        String serverName = retrievedDoc.getString("serverName");
        String serverIP = retrievedDoc.getString("serverIP");
        String amiUserName = retrievedDoc.getString("amiUserName");
        String amiPassword = retrievedDoc.getString("amiPassword");
        try{
            amiPort = new util().returnIntegerValueFromObject(retrievedDoc.get("amiPort"));
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("ServerDB","convertDBObjecttoJavaObject", nexc.getMessage());
            amiPort = 0;
        }
        String sshUsername = retrievedDoc.getString("sshUserName");
        String sshPassword = retrievedDoc.getString("sshPassword");
        try{
            sshPort = new util().returnIntegerValueFromObject(retrievedDoc.get("sshPort"));
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("ServerDB","convertDBObjecttoJavaObject", nexc.getMessage());
            sshPort = 0;
        }
        try{
            prefix = retrievedDoc.getString("prefix");
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("ServerDB","convertDBObjecttoJavaObject", nexc.getMessage());
        }
        String remoteRecordingLocation = retrievedDoc.getString("remoteRecordingLocation");
        String currentRecordingLocation = retrievedDoc.getString("currentRecordingLocation");
        String serverRecordingLocation = retrievedDoc.getString("serverRecordingLocation");
        String serverType = retrievedDoc.getString("serverType");
        String queueContext = retrievedDoc.getString("queueContext");
        String location = retrievedDoc.getString("location");
        boolean syncEnabled = retrievedDoc.getBoolean("syncEnabled");
        String serverID = retrievedDoc.getObjectId("_id").toString();
        String serverNamePrefix = retrievedDoc.getString("serverNamePrefix");
        ArrayList<Object> eventsToPostInJMS = new ArrayList<Object>();
        String transferContext = retrievedDoc.getString("transferContext");
        String ivrContext = retrievedDoc.getString("ivrContext");
        String cdrQueueLastApplication = retrievedDoc.getString("cdrQueueLastApplication");
        boolean autoStart = retrievedDoc.getBoolean("autoStart");
        String crmContext = retrievedDoc.getString("crmContext");
        String dialerContext = retrievedDoc.getString("dialerContext");
        String dialOutContext = retrievedDoc.getString("dialOutContext");
        String currentStatus = retrievedDoc.getString("currentStatus");
        Date lastEventTime = retrievedDoc.getDate("lastEventTime");
        String queueName = retrievedDoc.getString("queueName");
        int lifeTime = new util().returnIntegerValueFromObject(retrievedDoc.get("lifeTime"));
        String callProcessStatus = retrievedDoc.getString("callProcessStatus");
        String knownIncomingContext = retrievedDoc.getString("knownIncomingContext");
        String missedCallTrunk = retrievedDoc.getString("missedCallTrunk");

        server = new Server(serverName,serverIP,amiUserName,amiPassword,amiPort,sshUsername,sshPassword,sshPort,
                remoteRecordingLocation,currentRecordingLocation,serverRecordingLocation,serverType,queueContext,syncEnabled,
                serverID,location,serverNamePrefix,transferContext,
                ivrContext,cdrQueueLastApplication,autoStart,crmContext,dialerContext,dialOutContext,currentStatus,
                lastEventTime,queueName,lifeTime,callProcessStatus,knownIncomingContext,missedCallTrunk,prefix
        );
        return server;
    }
    
    public Server getServerByID(String serverID){
        Server serverDetails = null;
        MongoClient mongoConn = null;
        try{
            mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection<Document> collection = mongoDB.getCollection("server");
            Document obj = new Document("_id",new ObjectId(serverID));
        
            MongoCursor cursor = collection.find(obj).iterator();
            if(cursor.hasNext()){
                Document ob = (Document)cursor.next();
                serverDetails = convertDBObjecttoJavaObject(ob);
            }

            TrunkDAO trunkDAO = new TrunkDAO();
            ArrayList<Trunk> trunkVal = trunkDAO.getTrunksForServer(serverID);
            serverDetails.setTrunk(trunkVal);
            
        }
        catch(MongoException mexc){
            LogClass.logMsg("ServerDB","getServerByID", mexc.getMessage());
        }
        /*finally{
            try{
                mongoConn.close();
            }
            catch(NullPointerException nexc){
                LogClass.logMsg("ServerDB","getServerByID", nexc.getMessage());
            }
        }*/
        return serverDetails;
    }
    
    public Date getLastEventTimeFromLog(String serverID,String logCollectionName,MongoClient mongoConn){
        Date lastLogEventTime = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(logCollectionName);
            Document whereQuery = new Document("serverID",new ObjectId(serverID));
            Document selectQuery = new Document("eventTime",1);
            Document sortQuery = new Document("eventTime",-1);
            MongoCursor cursor = collection.find().filter(whereQuery).projection(selectQuery).sort(sortQuery).limit(1).iterator();
            while(cursor.hasNext()){
                Document retrievedDoc = (Document) cursor.next();
                lastLogEventTime = retrievedDoc.getDate("eventTime");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ServerDB","getLastEventTimeFromLog", mexc.getMessage());
        }
        return lastLogEventTime;
    }
    
    public void updateLastEventTime(String serverID,Date lastEventTime,MongoClient mongoConn){
        try{
                MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
                MongoCollection collection = mongoDB.getCollection("server");
                Document whereQuery = new Document("_id",new ObjectId(serverID));
                Document updateQuery = new Document("$set",new Document("lastEventTime",lastEventTime));
                collection.updateOne(whereQuery,updateQuery);
        }
        catch(MongoException mexc){
            LogClass.logMsg("ServerDB","updateLastEventTime", mexc.getMessage());
        }
    }
    
    public String checkForStopRequest(String serverID,String whatToCheckFor,MongoClient mongoConn){
        String requestID = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("loggeractionrequest");
            Document whereQuery = new Document();
            whereQuery.append("serverID",new ObjectId(serverID));
            whereQuery.append("actionStartedOn",null);
            whereQuery.append("actionCompletedOn",null);
            whereQuery.append("actionRequest","stop");
            whereQuery.append("whatToStart", whatToCheckFor);
            Document selectQuery = new Document("_id",1);
            MongoCursor cursor = collection.find().filter(whereQuery).projection(selectQuery).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                requestID = dc.getObjectId("_id").toString();
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ServerDB","checkForStopRequest", mexc.getMessage());
        }
        return requestID;
    }
    
    public void insertIntoUptime(String serverID,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("uptime");
            Document insertDoc = new Document();
            insertDoc.append("serverID",new ObjectId(serverID));
            insertDoc.append("startedOn",new Date());
            insertDoc.append("terminatedOn",null);
            insertDoc.append("disConnectedReason", null);
            collection.insertOne(insertDoc);
            updateServerStatus(serverID,"started",mongoConn);
        }
        catch(MongoException mexc){
            LogClass.logMsg("ServerDB","insertIntoUptime", mexc.getMessage());
        }
    }
    
    public void updateServerStatus(String serverID,String status,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("server");
            Document whereQuery = new Document("_id",new ObjectId(serverID));
            Document updateQuery = new Document("$set",new Document("currentStatus",status));
            collection.updateOne(whereQuery, updateQuery);
        }
        catch(MongoException mexc){
            LogClass.logMsg("ServerDB","updateServerStatus", mexc.getMessage());
        }
    }
    
    public void updateUptime(String serverID,String reason,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("uptime");
            BasicDBList andQueryList = new BasicDBList();
            andQueryList.add(new Document("serverID",new ObjectId(serverID)));
            andQueryList.add(new Document("terminatedOn",null));
            Document whereQuery = new Document("$and",andQueryList);
            Document updateQuery = new Document();
            updateQuery.append("disConnectedReason", reason);
            updateQuery.append("terminatedOn",new Date());
            Document updateQry = new Document("$set",updateQuery);
            collection.updateOne(whereQuery,updateQry);
            updateServerStatus(serverID,"stopped",mongoConn);
        }
        catch(MongoException mexc){
            LogClass.logMsg("ServerDB","updateUptime", mexc.getMessage());
        }
    }
    
    public void updateRequestCompletedOn(String requestID,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("loggeractionrequest");
            Document whereQuery = new Document("_id",new ObjectId(requestID));
            Document updateQuery = new Document();
            updateQuery.append("actionCompletedOn", new Date());
            Document updateQry = new Document("$set",updateQuery);
            collection.updateOne(whereQuery,updateQry);
        }
        catch(MongoException mexc){
            LogClass.logMsg("ServerDB","updateRequestCompletedOn", mexc.getMessage());
        }
    }
    
    public void updateRequestStartedOn(String requestID,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("loggeractionrequest");
            Document whereQuery = new Document("_id",new ObjectId(requestID));
            Document updateQuery = new Document();
            updateQuery.append("actionStartedOn", new Date());
            Document updateQry = new Document("$set",updateQuery);
            collection.updateOne(whereQuery,updateQry);
        }
        catch(MongoException mexc){
            LogClass.logMsg("ServerDB","updateRequestStartedOn", mexc.getMessage());
        }
    }
    
    public ArrayList<Server> getAllServerDetails(){
        ArrayList<Server> serverDetails = new ArrayList<>();
        MongoClient mongoConn = null;
        try{
            mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("server");
            Document whereQuery = new Document("deleted",0);
            MongoCursor cursor = collection.find(whereQuery).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                serverDetails.add(this.convertDBObjecttoJavaObject(dc));
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ServerDB","getAllServerDetails", mexc.getMessage());
        }
        return serverDetails;
    }
    
    public void updateCallProcessStatus(String valToUpdate,String serverID,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("server");
            Document whereQuery = new Document("_id",new ObjectId(serverID));
            Document updateQuery = new Document();
            updateQuery.append("callProcessStatus", valToUpdate);
            if(valToUpdate.equals("started"))
                updateQuery.append("callProcessStartedOn", new util().generateNewDateInYYYYMMDDFormat("UTC"));
            else
                updateQuery.append("callProcessStoppedOn", new util().generateNewDateInYYYYMMDDFormat("UTC"));
            collection.updateOne(whereQuery,new Document("$set", updateQuery));
        }
        catch(MongoException mexc){
            LogClass.logMsg("ServerDB","updateCallProcessStatus", mexc.getMessage());
        }
    }
    
    public String getWSIP(MongoClient mongoConn){
        String wsIP = "";
        try{
            MongoDatabase mdb = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection mc = mdb.getCollection("settings");
            MongoCursor cursor = mc.find().limit(1).sort(new Document("dateEntered",-1)).iterator();
            if(cursor.hasNext()){
                Document doc = (Document)cursor.next();
                wsIP = doc.getString("wsIP");
            }
        }
        catch(MongoException mexc){
             LogClass.logMsg("ServerDB","getWSIP", mexc.getMessage());
        }
        return wsIP;
    }
    
    public String getServerNamePrefixForID(String serverID,MongoClient mongoConn){
        String serverNamePrefix = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("server");
            MongoCursor cursor = collection.find(new Document("_id", new ObjectId(serverID))).
                    projection(new Document("serverNamePrefix", 1)).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                serverNamePrefix = dc.getString("serverNamePrefix");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("ServerDB","getServerNamePrefixForID", mexc.getMessage());
        }
        return serverNamePrefix;
    }
}
