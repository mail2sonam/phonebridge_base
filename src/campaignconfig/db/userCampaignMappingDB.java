/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campaignconfig.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import phonebridgelogger.model.Server;
import java.util.ArrayList;
import java.util.Date;
import campaignconfig.model.CampaignModel;
import campaignconfig.model.UserModel;
import org.bson.Document;
import org.bson.types.ObjectId;
import singleton.db.DBClass;


/**
 *
 * @author harini
 */
public class userCampaignMappingDB {
    public ArrayList<String> getAllUserDetailsfromDB(){
        ArrayList<String> totaluserdetails = new ArrayList<String>();
        MongoClient conn = null;
        try{
            conn = DBClass.getInstance().getConnection();
            MongoDatabase mdb = conn.getDatabase(DBClass.MDATABASE);
            MongoCollection mc = mdb.getCollection("users");
            Document wherequery = new Document("deleted",0);
            MongoCursor cursor = mc.find().filter(wherequery).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                totaluserdetails.add(dc.getString("extension"));
                totaluserdetails.add(dc.getString("name"));
                totaluserdetails.add(dc.getString("userName"));
                totaluserdetails.add(dc.getString("serverName"));
            }
        }
        catch(MongoException mex){
            mex.printStackTrace();
        }
        finally{
            //conn.close();
        }
        return totaluserdetails;
    } 


    public ArrayList<CampaignModel> getAllCampaignsForUser(String userID) {
        ArrayList<CampaignModel> result =  new ArrayList<CampaignModel>();
        MongoClient conn = null;
        try{
            conn=DBClass.getInstance().getConnection();
            MongoCollection mc=conn.getDatabase(DBClass.MDATABASE).getCollection("usercampaignmapping");
            MongoCursor mcur=mc.find(new Document("userID",new ObjectId(userID)).append("deleted", 0)).iterator();
            while(mcur.hasNext())
            {
            Document doc=(Document)mcur.next();
            CampaignModel c=new CampaignModel();
            c.setCampaignName(doc.getString("campaignName"));
            c.setCampaignID(doc.getObjectId("campaignID").toString());
            c.setDialMethod(doc.getString("dialerType"));
            result.add(c);
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally{
            //conn.close();
        }
        return result;
    }
    
    public void insertUserMapping(CampaignModel c,UserModel u,Server se) {
        MongoClient conn = null;
        try{
            conn = DBClass.getInstance().getConnection();
            MongoDatabase mdb = conn.getDatabase(DBClass.MDATABASE);
            MongoCollection mc = mdb.getCollection("usercampaignmapping");
            Document dc = new Document("campaignName",c.getCampaignName());
            dc.append("campaignID",new ObjectId(c.getCampaignID()));
            dc.append("userName",u.getUserName());
            dc.append("dialerType", c.getDialMethod());
            dc.append("extension", u.getExtension());
            dc.append("userID", new ObjectId(u.getUserID()));
            dc.append("status","inactive");
            dc.append("serverID",new ObjectId(se.getServerID()));
            dc.append("serverName",se.getServerName());
            dc.append("dateEntered",new Date());
            dc.append("serverNamePrefix", se.getServerNamePrefix());
            dc.append("isGeneralPolling",c.isIsGeneralPolling());
            dc.append("listID",c.getListID());
            dc.append("branchID", u.getBranchID());
            dc.append("deleted", 0);
            try{
                dc.append("branchID", new ObjectId(u.getBranchID()));
            }
            catch(Exception ex){
                
            }
            mc.insertOne(dc);
           
            /*Document searchQuery = new Document("campaignID",new Document("$ne",new ObjectId(c.getCampaignID())));
            searchQuery.append("userID",new ObjectId(u.getUserID()));
            searchQuery.append("dialerType","progressive");
        
            Document updateCondition = new Document("dateDeleted",new Date());
            updateCondition.append("deleted", 1);
            mc.updateMany(searchQuery, new Document("$set",updateCondition));*/
           
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally{
            //conn.close();
        }
    }

    public void removeUserMapping(CampaignModel c, UserModel u) {
        MongoClient conn = null;
    
        try{
         conn=DBClass.getInstance().getConnection();
        MongoDatabase mdb = conn.getDatabase(DBClass.MDATABASE);
        MongoCollection mc=mdb.getCollection("usercampaignmapping");   
        Document searchQuery = new Document("campaignID",new ObjectId(c.getCampaignID()));
        searchQuery.append("deleted", 0);
        searchQuery.append("userID",new ObjectId(u.getUserID()));
        Document updateCondition = new Document("$set",new Document("dateDeleted",new Date()).append("deleted", 1));
        mc.updateOne(searchQuery, updateCondition);
       }
        
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally{
        //conn.close();
    }
        
    }
}
    
