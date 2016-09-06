/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campaignconfig.db;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.Date;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.MongoException;
import com.mongodb.client.result.UpdateResult;
import com.phonebridgecti.dao.CampaignDAO;
import singleton.db.DBClass;
import phonebridgelogger.model.Server;
import java.util.List;
/**
 *
 * @author saran
 */
public class ServerDB extends phonebridgelogger.db.ServerDB{
    
//List View Server Details
    public ArrayList<Server> getAllServerDetails(){
        ArrayList<Server> result = new ArrayList<>();
        MongoClient conn = null;
        try{   
            conn = singleton.db.DBClass.getInstance().getConnection();
            MongoDatabase mdb = conn.getDatabase(singleton.db.DBClass.MDATABASE);
            MongoCollection mc=mdb.getCollection("server");
            Document searchQuery = new Document("deleted",0);
            //searchQuery.append("_id",new ObjectId("551b92ab046c070bcb5e30b4"));
            MongoCursor cursor = mc.find().filter(searchQuery).limit(100).iterator();
            while(cursor.hasNext()){
                Document dc=(Document)cursor.next();
                Server s=null;
                try{
                    s=this.convertDBObjecttoJavaObject(dc);
                    result.add(s);
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
                if(s==null)
                    System.out.println(dc.getObjectId("_id").toString());
            }      
        }
        catch(MongoException mexc){
            mexc.printStackTrace();
        }
        finally{
            try{
                //conn.close();
            }
            catch(NullPointerException nexc){
                nexc.printStackTrace();
            }
        }
        return result;
    }
    
    // Get Specific ServerIP
    public Server getServerdetailsByIP(String ip){
        MongoClient conn = null;
        Document dc =null;
        try{
            conn = singleton.db.DBClass.getInstance().getConnection();
            MongoDatabase db=conn.getDatabase(singleton.db.DBClass.MDATABASE);
            MongoCollection<Document> collection=db.getCollection("server");
            dc = collection.find(new Document("serverIP", ip)).first();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally{
            //conn.close();
        }
        return convertDBObjecttoJavaObject(dc);
    }
    
    //Get Specific Trunk    
    public Server getTrunkdetailsByIP(String ip){
        MongoClient conn = null;
        Document dc=null;
        try{
            conn = singleton.db.DBClass.getInstance().getConnection();
            MongoDatabase db=conn.getDatabase(singleton.db.DBClass.MDATABASE);
            MongoCollection<Document> collection=db.getCollection("server");
            dc = collection.find(new Document("serverIP", ip)).first();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally{
            //conn.close();
        }
        return convertDBObjecttoJavaObject(dc);
    }
      
     
    //Insert Server Details
    public String insertServerDetails(Server server){
        MongoClient mc =null;
        try{
            mc = DBClass.getInstance().getConnection();
            MongoDatabase db = mc.getDatabase(DBClass.MDATABASE);
            List<String> trunklist = new ArrayList<>();
            ObjectId serverID = new ObjectId();
            Document doc = new Document("_id",serverID)
                .append("amiUserName",server.getAmiUserName())
                .append("serverName", server.getServerName())
                .append("serverIP", server.getServerIP())
                .append("amiPassword",server.getAmiPassword())
                .append("location", server.getLocation())
                .append("serverType", server.getServerType())
                .append("syncEnabled", server.isSyncEnabled())
                .append("amiPort", server.getAmiPort())
                .append("currentRecordingLocation", server.getCurrentRecordingLocation())
                .append("serverRecordingLocation", server.getServerRecordingLocation())
                .append("remoteRecordingLocation", server.getRemoteRecordingLocation())
                .append("crmContext", server.getCrmContext())
                .append("dialOutContext", server.getDialOutContext())
                .append("dialerContext", server.getDialerContext())
                .append("queueContext", server.getQueueContext())
                .append("sshUserName", server.getSshUsername())
                .append("sshPassword", server.getSshPassword())
                .append("sshPort", server.getSshPort())
                .append("currentStatus","stopped")
                .append("callProcessStatus","stopped")
                .append("autoStart",server.isAutoStart())
                .append("ivrContext",server.getIvrContext())
                .append("lastEventTime",null)
                .append("serverNamePrefix",server.getServerNamePrefix())
                .append("transferContext",server.getTransferContext())
                .append("cdrQueueLastApplication","Queue")
                .append("trunk",trunklist)
                .append("prefix", server.getPrefix())
                .append("deleted",0);
            MongoCollection<Document> coll=db.getCollection("server");
            coll.insertOne(doc);
            new CampaignDAO().insertDefaultCampaign(serverID,mc);
        }
        finally{
            //mc.close();
        }
        return "Success";
    }

//Insert Logger Request Start or Stop    
    public void insertLoggeractionRequest(String id,String actionRequested,String whatToStart){
        MongoClient mc =null; 
        try{        
        mc=DBClass.getInstance().getConnection();
        MongoDatabase db=mc.getDatabase(DBClass.MDATABASE);
        Document doc;
        doc = new Document("actionCompletedOn",null)
                .append("actionRequest",actionRequested)
                .append("actionStartedOn", null)
                .append("whatToStart", whatToStart)
                .append("requestBy", null)
                .append("requestedOn",new Date())
                .append("serverID",new ObjectId(id));
        MongoCollection<Document> coll=db.getCollection("loggeractionrequest");
        coll.insertOne(doc);
        updateCurrentStatus(id,actionRequested,whatToStart);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally{
            //mc.close();
        }
    }

//
    
    public String updateCurrentStatus(String id,String actionRequested,String whatToStart){
        MongoClient conn = null;
        try{        
        conn=DBClass.getInstance().getConnection();
        MongoDatabase mdb = conn.getDatabase(DBClass.MDATABASE);
        MongoCollection mc=mdb.getCollection("server");
        Document searchQuery = new Document("_id",new ObjectId(id));
        searchQuery.append("whatToStart", whatToStart);
        String dbActionRequested="";
        switch(actionRequested)
        {
            case "start":
                dbActionRequested="starting";
                break;
            case "stop":
                dbActionRequested="stopping";
                break;
        }
        Document updateCondtion = new Document("$set",new Document("currentStatus",dbActionRequested));
        mc.updateOne(searchQuery, updateCondtion);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally{
            //conn.close();
        }
        return "listViewServer";
    }


//Edit Server Details
     public Server editServerDetails(String name){
        MongoClient mc = null;
        Server server=new Server();
        try{
        mc=DBClass.getInstance().getConnection();
        MongoDatabase db=mc.getDatabase(DBClass.MDATABASE);
        MongoCollection<Document> getdata=db.getCollection("server");
        Document dc=null;
        dc = getdata.find(new Document("amiUserName", name)).first();    
                server.setServerID(dc.getObjectId("_id").toString());
                server.setServerName(dc.getString("serverName"));
                server.setAmiUserName(dc.getString("amiUserName"));
                server.setServerIP(dc.getString("serverIP"));
                server.setAmiPort(dc.getInteger("amiPort"));
                server.setAmiPassword(dc.getString("amiPassword"));
                server.setLocation(dc.getString("location"));
                server.setServerType(dc.getString("serverType"));
                server.setSyncEnabled(dc.getBoolean("syncEnabled"));
                server.setCurrentRecordingLocation(dc.getString("currentRecordingLocation"));
                server.setServerRecordingLocation(dc.getString("serverRecordingLocation"));
                server.setRemoteRecordingLocation(dc.getString("remoteRecordingLocation"));
                server.setCrmContext(dc.getString("crmContext"));
                server.setDialOutContext(dc.getString("dialOutContext"));
                server.setDialerContext(dc.getString("dialerContext"));
                server.setQueueContext(dc.getString("queueContext"));
                server.setSshUsername(dc.getString("sshUserName"));
                server.setSshPassword(dc.getString("sshPassword"));
                server.setSshPort(dc.getInteger("sshPort"));
                server.setAutoStart(dc.getBoolean("autoStart"));
                server.setIvrContext(dc.getString("ivrContext"));
                server.setServerNamePrefix(dc.getString("serverNamePrefix"));
                server.setTransferContext(dc.getString("transferContext"));
                server.setPrefix(dc.getString("prefix"));
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
            finally{
            //mc.close();
            }        
            return server;
        }
//Update Server Details
      public boolean updateServerDetails(Server server){
        MongoClient conn = null;  
        UpdateResult isUpdated = null;
        boolean recordsUpdated = false;
        try{
        conn =DBClass.getInstance().getConnection();
        MongoDatabase mdb = conn.getDatabase(DBClass.MDATABASE);
        MongoCollection mc=mdb.getCollection("server");   
        Document searchQuery = new Document("_id",new ObjectId(server.getServerID()));
        Document updateCondtion = new Document("$set",new Document("serverIP",server.getServerIP())
                    .append("amiUserName",server.getAmiUserName())
                    .append("serverName",server.getServerName())
                    .append("amiPassword",server.getAmiPassword())
                    .append("location",server.getLocation())
                    .append("serverType",server.getServerType())
                    .append("syncEnabled",server.isSyncEnabled())
                    .append("amiPort",server.getAmiPort())
                    .append("currentRecordingLocation",server.getCurrentRecordingLocation())
                    .append("serverRecordingLocation",server.getServerRecordingLocation())
                    .append("remoteRecordingLocation",server.getRemoteRecordingLocation())
                    .append("crmContext",server.getCrmContext())
                    .append("dialOutContext",server.getDialOutContext())
                    .append("dialerContext",server.getDialerContext())
                    .append("queueContext",server.getQueueContext())
                    .append("sshUserName",server.getSshUsername())
                    .append("sshPassword",server.getSshPassword())
                    .append("sshPort",server.getSshPort())
                    .append("autoStart",server.isAutoStart())
                    .append("ivrContext",server.getIvrContext())
                    .append("serverNamePrefix",server.getServerNamePrefix())
                    .append("prefix", server.getPrefix())
                    .append("transferContext",server.getTransferContext()));
            isUpdated=mc.updateOne(searchQuery, updateCondtion);
            recordsUpdated =true;
    
         }catch(MongoException e){
             e.printStackTrace();
         }
        finally{
            //conn.close();
        }
        return recordsUpdated;
     }

//Delete Server       
     public String deleteServerRecords(String id){
         MongoClient conn = null;
        try{
        conn = DBClass.getInstance().getConnection();
        MongoDatabase mdb = conn.getDatabase(DBClass.MDATABASE);
        MongoCollection mc=mdb.getCollection("server");   
        Document searchQuery = new Document("_id",new ObjectId(id));
        Document updateCondtion = new Document("$set",new Document("deleted",1));
        mc.updateOne(searchQuery, updateCondtion);
         }catch(Exception e){
             e.printStackTrace();
         }
        finally{
            //conn.close();
        }
         return "Success";
     }

}
