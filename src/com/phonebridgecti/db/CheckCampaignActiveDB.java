package com.phonebridgecti.db;
import campaignconfig.db.CampaignDB;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;
import singleton.db.DBClass;
import phonebridge.util.LogClass;
import phonebridgelogger.model.Server;
import phonebridge.util.util;
import java.util.ArrayList;
import java.util.Date;
import campaignconfig.model.CampaignModel;
import campaignconfig.model.DisplayCallModel;
import com.mongodb.BasicDBList;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

public class CheckCampaignActiveDB{
    public CampaignModel getActiveProgressiveCampaignForExtension(String extension,Server serverDetails,MongoClient mongoConn){
        CampaignModel campaign = null;
        MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
        MongoCollection collection = mongoDB.getCollection("usercampaignmapping");
        Document whereQuery = new Document();
        whereQuery.append("extension",extension);
        whereQuery.append("status","active");
        whereQuery.append("dialerType", "progressive");
        whereQuery.append("deleted", 0);
        MongoCursor cursor = collection.find(whereQuery).limit(1).iterator();
        if(cursor.hasNext()){
            Document d = (Document) cursor.next();
            campaign = new CampaignDB().getCampaignDetailsUsingID(d.getObjectId("campaignID").toString());
        }
        return campaign;
    }
    
    public boolean checkUserAvailablityForDialer(String serverID,String extension,MongoClient mongoConn){
        boolean userStatus= false;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("users");
            Document whereQuery = new Document("serverID",new ObjectId(serverID));
            whereQuery.append("userExtension", extension);
            //whereQuery.append("extensionStatus", "0");
            whereQuery.append("popupStatus", "free");
            whereQuery.append("onBreak", "Available");
            MongoCursor cursor = collection.find(whereQuery).iterator();
            if(cursor.hasNext()){
                userStatus = true;
            }
        }
        catch(Exception e){
            LogClass.logMsg("CheckCampaignActiveDB", "checkUserStatus", e.getMessage());
        }
        return userStatus;
    }
    
    public String getPrefixForOutboundCampaign(String campaignID,MongoClient mongoConn){
        String prefix = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);    
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document whereQuery = new Document();
            whereQuery.append("_id",new ObjectId(campaignID));
            Document selectQuery = new Document();
            selectQuery.append("trunkPrefix", 1);
            MongoCursor cursor = collection.find(whereQuery).projection(selectQuery).iterator();
            if(cursor.hasNext()){
                Document d = (Document) cursor.next();
                prefix = d.getString("trunkPrefix");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CheckCampaignActiveDB", "getPrefixForOutboundCampaign", mexc.getMessage());
        }
        return prefix;
    }
    
    public String getContextForPDialer(String extension,MongoClient mongoConn){
        String context = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);    
            MongoCollection collection = mongoDB.getCollection("users");
            Document whereQuery = new Document();
            whereQuery.append("userExtension", extension);
            Document selectQuery = new Document();
            selectQuery.append("context", 1);
            MongoCursor cursor = collection.find(whereQuery).projection(selectQuery).iterator();
            if(cursor.hasNext()){
                Document d = (Document) cursor.next();
                context = d.getString("context");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CheckCampaignActiveDB", "getContextForPDialer", mexc.getMessage());
        }
        return context;
    }
    
    public DisplayCallModel getMobileNumberCallBackForPDialer(String serverNamePrefix,String extension,MongoClient mongoConn){
        DisplayCallModel callBackDetails = null;
        try{
            DateTime jodaCurTime = new util().convertJavaDateToMongoDate(new util().generateNewDateInYYYYMMDDFormat("UTC").getTime());
            Document whereQuery = new Document("followUpOn",new Document("$lte",jodaCurTime.toDate()));
            MongoCursor cursor = this.returnCallBackDetailsCursor(serverNamePrefix, extension, 1, whereQuery,mongoConn);
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                callBackDetails = new DisplayCallModel();
                callBackDetails.setPhone1(dc.getString("phoneNumber"));
                callBackDetails.setCampaignID(dc.getObjectId("campaignID").toString());
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CheckCampaignActiveDB", "getMobileNumberCallBackForPDialer", mexc.getMessage());
        }
        return callBackDetails;
    }
    
    public ArrayList<DisplayCallModel> getCallBackDetailsForDisplay(String serverNamePrefix,String extension,Date dateCondition,
        String campaignID,MongoClient mongoConn,String name,String phone){
        ArrayList<DisplayCallModel> displayCallDetails = new ArrayList<>();
        try{
            Document whereQry = new Document();
            if(campaignID!=null)
                whereQry.append("campaignID", new ObjectId(campaignID));
            if(dateCondition!=null){
                DateTime jodaStartTime = new util().convertJavaDateToMongoDate(dateCondition.getTime());
                DateTime jodaEntTime = jodaStartTime.plusDays(1);
                whereQry.append("followUpOn", new Document("$gte", jodaStartTime.toDate()).
                    append("$lt", jodaEntTime.toDate()));
            }
            if(name.length()>0)
            {
                BasicDBList bl=new BasicDBList();
                bl.add(new Document("customerName", "/"+name+"/"));
                bl.add(new Document("customerName", "/^"+name+"/"));
                bl.add(new Document("customerName", "/"+name+"^/"));
                whereQry.append("$or",bl);
            }
            if(phone.length()>0)
            {
                BasicDBList bl=new BasicDBList();
                bl.add(new Document("phone1", phone));
                bl.add(new Document("phone2", phone));
                bl.add(new Document("phone3", phone));
                whereQry.append("$or",bl);
            }
            MongoCursor cursor = this.returnCallBackDetailsCursor(serverNamePrefix, extension,0,whereQry,mongoConn);
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                DisplayCallModel callBackDetails = new DisplayCallModel();
                callBackDetails.setName(dc.getString("customerName"));
                callBackDetails.setPhone1(dc.getString("phone1"));
                callBackDetails.setPhone2(dc.getString("phone2"));
                callBackDetails.setPhone3(dc.getString("phone3"));
                callBackDetails.setLastDisposition(dc.getString("disposition"));
                callBackDetails.setLastComments(dc.getString("comments"));
                callBackDetails.setCampaignName(dc.getString("campaignName"));
                callBackDetails.setCampaignID(dc.getObjectId("campaignID").toString());
                callBackDetails.setListID(dc.getString("listID"));
                callBackDetails.setCustID(dc.getObjectId("custID").toString());
                callBackDetails.setLastCalledOn(dc.getDate("dateEntered"));
                callBackDetails.setFollowUpOn(dc.getDate("followUpOn"));
                displayCallDetails.add(callBackDetails);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CheckCampaignActiveDB", "getCallBackDetailsForDisplay", mexc.getMessage());
        }
        return displayCallDetails;
    }
    
    private MongoCursor returnCallBackDetailsCursor(String serverPrefix,String extension,int limit,
        Document whereQry,MongoClient mongoConn){
        MongoCursor cursor = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverPrefix+"_"+extension+"_callback");
            Document sortQry = new Document("followUpOn", 1);
            cursor = collection.find(whereQry).sort(sortQry).limit(limit).iterator();
        }
        catch(MongoException mexc){
            LogClass.logMsg("CheckCampaignActiveDB", "returnCallBackDetailsCursor", mexc.getMessage());
        }
        return cursor;
    }
    
    /*public String getMobileNumberFollowUpForPDialer(String campaignName,String extension,boolean isGeneralPolling,MongoClient mongoConn){
        String mobileNumber = null;
        try{
            DateTime jodaCurTime = new util().convertJavaDateToMongoDate(new util().generateNewDateInYYYYMMDDFormat("IST").getTime());
            Document whereQuery = new Document();
            whereQuery.append("leadStatus", "Call Back");
            whereQuery.append("followUpOn", new Document("$lt",jodaCurTime.toDate()));
            whereQuery.append("extension", extension);
            MongoCursor cursor = this.executeNumberRetrieveQry(whereQuery, campaignName,1, mongoConn);
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                mobileNumber = this.returnMobileNumberFromRetrievedDoc(dc);
                this.updateLastCalledOn(dc.getObjectId("_id"), campaignName,extension, mongoConn);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CheckCampaignActiveDB", "getMobileNumberFollowUpForPDialer", mexc.getMessage());
        }
        return mobileNumber;
    }*/
    
    public String getMobileNoForTryNextNoPDialer(String listID,String extension,boolean isGeneralPolling,MongoClient mongoConn){
        String mobileNumber = null;
        try{
            Document whereQuery = new Document();
            whereQuery.append("leadStatus", "Try Next No");
            if(!isGeneralPolling)
                whereQuery.append("extension", extension);
            MongoCursor cursor = this.executeNumberRetrieveQry(whereQuery, listID, 1,mongoConn);
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                mobileNumber = this.returnMobileNumberFromRetrievedDoc(dc);
                this.updateLastCalledOn(dc.getObjectId("_id"), listID,extension, mongoConn);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CheckCampaignActiveDB", "getMobileNoForTryNextNoPDialer", mexc.getMessage());
        }
        return mobileNumber;
    }
    
    public String getMobileNoForTryAgainPDialer(String listID,String extension,boolean isGeneralPolling,MongoClient mongoConn){
        String mobileNumber = null;
        try{
            Document whereQuery = new Document();
            whereQuery.append("leadStatus", "Try Again");
            if(!isGeneralPolling)
                whereQuery.append("extension", extension);
            MongoCursor cursor = this.executeNumberRetrieveQry(whereQuery, listID,1, mongoConn);
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                mobileNumber = this.returnMobileNumberFromRetrievedDoc(dc);
                this.updateLastCalledOn(dc.getObjectId("_id"), listID,extension, mongoConn);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CheckCampaignActiveDB", "getMobileNoForTryAgainPDialer", mexc.getMessage());
        }
        return mobileNumber;
    }
    
    public String getMobileNoForOpenedPDialer(String listID,String extension,boolean isGeneralPolling,MongoClient mongoConn){
        String mobileNumber = null;
        try{
            Document whereQuery = new Document();
            whereQuery.append("leadStatus", "open");
            if(!isGeneralPolling)
                whereQuery.append("extension", extension);
            MongoCursor cursor = this.executeNumberRetrieveQry(whereQuery, listID,1, mongoConn);
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                mobileNumber = this.returnMobileNumberFromRetrievedDoc(dc);
                this.updateLastCalledOn(dc.getObjectId("_id"), listID,extension, mongoConn);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CheckCampaignActiveDB", "getMobileNoForOpenedPDialer", mexc.getMessage());
        }
        return mobileNumber;
    }
    
    public String getMobileNoForTryAgainLaterPDialer(String listID,String extension,boolean isGeneralPolling,MongoClient mongoConn){
        String mobileNumber = null;
        try{
            Document whereQuery = new Document();
            whereQuery.append("leadStatus", "Try Again Later");
            if(!isGeneralPolling)
                whereQuery.append("extension", extension);
            MongoCursor cursor = this.executeNumberRetrieveQry(whereQuery, listID,1, mongoConn);
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                mobileNumber = this.returnMobileNumberFromRetrievedDoc(dc);
                this.updateLastCalledOn(dc.getObjectId("_id"), listID,extension, mongoConn);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CheckCampaignActiveDB", "getMobileNoForTryAgainLaterPDialer", mexc.getMessage());
        }
        return mobileNumber;
    }
    
    public String getMissedCallNumber(String MMYYYY,String serverNamePrefix,MongoClient mongoConn){
        String mobileNumber = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(MMYYYY.concat("_"+serverNamePrefix).concat("_missedCall"));
            Document whereQuery = new Document("callBack",false);
            Document selectQuery = new Document("phoneNumber",1);
            MongoCursor cursor = collection.find(whereQuery).projection(selectQuery).limit(1).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                mobileNumber = dc.getString("phoneNumber");
            }
        }
        catch(MongoException mecx){
            LogClass.logMsg("CheckCampaignActiveDB", "getMissedCallNumber", mecx.getMessage());
        }
        return mobileNumber;
    }
    
    private String returnMobileNumberFromRetrievedDoc(Document dc){
        String mobileNumber = null;
        try{
            if(dc.containsKey("tryNextNo")){
                mobileNumber = dc.getString(dc.getString("tryNextNo"));
            }
            else{
                if(dc.getString("phone1")!=null)
                    mobileNumber = dc.getString("phone1");
                else if(dc.getString("phone2")!=null)
                    mobileNumber = dc.getString("phone2");
                else
                    mobileNumber = dc.getString("phone3");
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CheckCampaignActiveDB", "returnMobileNumberFromRetrievedDoc", ex.getMessage());
        }
        return mobileNumber;
    }
    
    public MongoCursor executeNumberRetrieveQry(Document whereCond,String listID,int limit,MongoClient mongoConn){
        MongoCursor cursor = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("lst_"+listID);
            Document selectQuery = new Document();
            selectQuery.append("phone1", 1);
            selectQuery.append("phone2", 1);
            selectQuery.append("phone3", 1);
            selectQuery.append("tryNextNo", 1);
            selectQuery.append("lastCalledOn", 1);
            selectQuery.append("followUpOn", 1);
            selectQuery.append("name", 1);
            selectQuery.append("_id",1);
            Document sortQry = new Document("lastCalledOn",1);
            cursor = collection.find(whereCond).limit(limit).projection(selectQuery).sort(sortQry).iterator();
        }
        catch(MongoException mexc){
            LogClass.logMsg("CheckCampaignActiveDB", "getMobileNumberOpened", mexc.getMessage());
        }
        return cursor;
    }
    
    public void updateLastCalledOn(ObjectId docID,String listID,String extension,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("lst_"+listID);
            Document updateWhereQry = new Document("_id", docID);
            Document updateQry = new Document("lastCalledOn", new util().generateNewDateInYYYYMMDDFormat("UTC"))
                    .append("leadStatus", "Being Called")
                    .append("lastCalledBy", extension);
            collection.updateOne(updateWhereQry, new Document("$set", updateQry));
        }
        catch(MongoException mexc){
            LogClass.logMsg("CheckCampaignActiveDB", "updateLastCalledOn", mexc.getMessage());
        }
    }
    
    public int getDelaybeforeCallForPDialer(String campaignID,MongoClient mongoConn){
        int delayBeforeCall = 0;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document whereQuery = new Document();
            whereQuery.append("_id", new ObjectId(campaignID));
            Document selectQuery = new Document("timeBetweenCall", 1);
            MongoCursor cursor = collection.find(whereQuery).projection(selectQuery).iterator();
            if(cursor.hasNext()){
                Document d = (Document) cursor.next();
                delayBeforeCall = d.getInteger("timeBetweenCall");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CheckCampaignActiveDB", "getDelaybeforeCall", mexc.getMessage());
        }
        return delayBeforeCall;
    }
     public boolean checkIfExtensionFree(String extension,String serverID,MongoClient mongoConn){
        boolean extensionFree = false;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("users");
            Document whereQuery = new Document("serverID",new ObjectId(serverID));
            whereQuery.append("userExtension", extension);
            whereQuery.append("extensionStatus", "0");
            MongoCursor cursor = collection.find(whereQuery).iterator();
            if(cursor.hasNext()){
                extensionFree = true;
            }
        }
        catch(MongoException mexc){
            
        }
        return extensionFree;
    }
     
     public boolean hasOpenCallsInCurrentCallsForExtension(String extension,String serverNamePrefix,MongoClient mongoConn){
        boolean openCall = false;
        try{
            util utilObj = new util();
            String mmYYYY = utilObj.getMMYYYYForJavaDate(new Date().getTime());
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            String collectionName = utilObj.returnCollectionNameForCurrentCalls(mmYYYY, serverNamePrefix);
            MongoCollection collection = mongoDB.getCollection(collectionName);
            Document whereQry = new Document("extension",extension);
            whereQry.append("isClosed", false);
            Document sortQry = new Document("eventDateTime", -1);
            MongoCursor cursor = collection.find(whereQry).sort(sortQry).limit(1).iterator();
            if(cursor.hasNext()){
                openCall = true;
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CheckCampaignActiveDB", "checkForOpenCallsInCurrentCallsForExtension", mexc.getMessage());
        }
        return openCall;
    }
}
