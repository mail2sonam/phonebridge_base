/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridge.originate;

import campaignconfig.model.CampaignModel;
import campaignconfig.model.UserModel;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import singleton.db.DBClass;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.MongoCursor;
import java.util.Date;
import phonebridgelogger.model.Server;

/**
 *
 * @author sonamuthu
 */
public class OriginateCallDb {
    public OriginateRequestModel getDetailsFromOriginateCollForID(String originateID,String serverNamePrefix,MongoClient mongoConn){
        OriginateRequestModel detailsFromOriginate = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix.concat("_originatecall"));
            Document whereQry = new Document("_id", new ObjectId(originateID));
            MongoCursor<Document> cursor = collection.find(whereQry).iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                detailsFromOriginate = this.convertDbObjToJavaObj(dc);
            }
        }
        catch(MongoException ex){
            
        }
        return detailsFromOriginate;
    }
    
    public OriginateRequestModel convertDbObjToJavaObj(Document dc){
        OriginateRequestModel originateReq = null;
        if(dc==null)
            return originateReq;
        originateReq = new OriginateRequestModel();
        originateReq.setOriginateReqID(dc.getObjectId("_id").toString());
        originateReq.setPrefix(dc.getString("prefix"));
        originateReq.setPhoneNumber(dc.getString("phoneNumber"));
        originateReq.setUserExtension(dc.getString("userExtension"));
        originateReq.setTypeOfDialer(dc.getString("typeOfDialer"));
        originateReq.setCallContext(dc.getString("callContext"));
        try{
            originateReq.setExtensionType(dc.getString("extensionType"));
        }
        catch(Exception e)
        {
            originateReq.setExtensionType("SIP");
        }
        try{
            originateReq.setCampaignID(dc.getObjectId("campaignID").toString());
        }
        catch(Exception ex){

        }
        try{
            originateReq.setFollowMeExists(dc.getBoolean("followMeExists"));
        }
        catch(Exception ne){

        }
        try{
            originateReq.setFollowMeNumber(dc.getString("followMeNumber"));
        }
        catch(Exception ne){

        }
        return originateReq;
    }
    
    public String insertOriginateCallReq(Server server,String phoneNumber,String extension,
        CampaignModel campaign,String popupID,String custID,UserModel user){
        ObjectId id = new ObjectId();
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(server.getServerNamePrefix().concat("_originatecall"));
            String callContext = (user.getContext()!=null && user.getContext().length()>0)?user.getContext():server.getDialOutContext();
            String prefix = (user.getPrefix()!=null && user.getPrefix().length()>0)?user.getPrefix():server.getPrefix();
            Document insertDoc = new Document();
            insertDoc.append("_id", id);
            insertDoc.append("prefix", prefix);
            insertDoc.append("phoneNumber", phoneNumber);
            insertDoc.append("userExtension",extension);
            insertDoc.append("typeOfDialer" ,campaign.getDialMethod());
            insertDoc.append("callOn" ,new Date().getTime());
            insertDoc.append("status" , "toInitiate");
            insertDoc.append("hasAgentAnswered" ,"No");
            if(popupID!=null)
                insertDoc.append("popupID" , new ObjectId(popupID));
            insertDoc.append("campaignID" ,new ObjectId(campaign.getCampaignID()));
            insertDoc.append("callContext" ,callContext);
            if(custID!=null)
                insertDoc.append("custID" ,new ObjectId(custID));
            insertDoc.append("followMeExists" ,false);
            insertDoc.append("campaignName" ,campaign.getCampaignName());
            insertDoc.append("initiatedUpdateOn" ,null);
            insertDoc.append("initiatedUpdateOnSecs",null);
            insertDoc.append("hasAgentAnsweredUpdatedOn",null);
            collection.insertOne(insertDoc);
            
        }
        catch(Exception ex){
            System.out.println("ERR IN INSERTING ORIGINATE CALL REQ "+ex.getMessage());
        }
        return id.toString();
    }
}

