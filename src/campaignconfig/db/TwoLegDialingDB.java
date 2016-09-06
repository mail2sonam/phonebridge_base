/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campaignconfig.db;

import campaignconfig.model.CampaignModel;
import campaignconfig.model.TwoLegDialingModel;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import org.bson.Document;
import org.bson.types.ObjectId;
import phonebridgelogger.model.Server;
import phonebridgelogger.model.Trunk;
import singleton.db.DBClass;

/**
 *
 * @author venky
 */
public class TwoLegDialingDB {
    public void insertValuesIntoDatabase(String phone1,String phone2,CampaignModel campaign,Server serverDetails){
        try{
            String prefix = "";
            for(Trunk trunk:serverDetails.getTrunk()){
                if(trunk.getTrunkValue().equals(campaign.getTrunkValue()))
                    prefix=trunk.getPrefix();
            }
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("twolegoriginaterequest");
            Document insertQuery = new Document();
            insertQuery.append("phone1", phone1);
            insertQuery.append("phone2", phone2);
            insertQuery.append("campaignID", new ObjectId(campaign.getCampaignID()));
            insertQuery.append("prefix", prefix);
            insertQuery.append("callContext", serverDetails.getDialOutContext());
            insertQuery.append("status", "pending");
            insertQuery.append("campaignName", campaign.getCampaignName());
            collection.insertOne(insertQuery);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public ArrayList<TwoLegDialingModel> getAllTwolegDialingRequests(){
        ArrayList<TwoLegDialingModel> allTwolegRequests = new ArrayList<>();
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("twolegoriginaterequest");
            Document whereQuery = new Document();
            whereQuery.append("status", "pending");
            MongoCursor cursor = collection.find(whereQuery).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                TwoLegDialingModel model = new TwoLegDialingModel();
                model.setPhone1(dc.getString("phone1"));
                model.setPhone2( dc.getString("phone2"));
                model.setCampaignID(dc.getObjectId("campaignID").toString());
                model.setPrefix(dc.getString("prefix"));
                model.setCallContext(dc.getString("callContext"));
                model.setCampaignName(dc.getString("campaignName"));
                allTwolegRequests.add(model);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return allTwolegRequests;
    }
    
    public void updateusingPhoneAndCampaignID(String phone1,String phone2,String campaignID,String status,String originate){
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("twolegoriginaterequest");
            Document whereQuery = new Document();
            whereQuery.append("phone1", phone1);
            whereQuery.append("phone2", phone2);
            whereQuery.append("campaignID", new ObjectId(campaignID));
            whereQuery.append("status", "pending");
            Document updateCondition = new Document("status",status);
            updateCondition.append("originate", originate);
            collection.updateOne(whereQuery, new Document("$set",updateCondition));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
