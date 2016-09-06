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
import java.util.ArrayList;
import org.bson.Document;
import campaignconfig.model.CampaignModel;
import campaignconfig.model.PremptedModulesModel;
import campaignconfig.model.ShowFieldModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.BasicDBList;
import com.phonebridgecti.dao.DispositionDAO;
import com.phonebridgecti.dao.ShowFieldDAO;
import com.phonebridgecti.db.GetBasicContactInfo;
import java.util.Date;
import java.util.HashMap;
import org.bson.types.ObjectId;
import phonebridge.postdata.GetClientIdForNumber;
import phonebridge.util.LogClass;
import phonebridge.util.util;
import singleton.db.DBClass;


/**
 *
 * @author harini
 */
public class CampaignDB {
    
    /*
        List All Outgoing Campaign
    */
    public ArrayList<CampaignModel> getAllCampaignDetails(boolean retrieveIsDefault){
        ArrayList<CampaignModel> totalCampaignDetails = new ArrayList<>();
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document wherequery = new Document("deleted",0);
            Document sortQry = new Document("campaignCreatedOn", -1);
            MongoCursor cursor = collection.find().filter(wherequery).sort(sortQry).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                CampaignModel cm = this.convertDBObjecttoJavaObject(dc);
                totalCampaignDetails.add(cm);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CampaignDB", "getAllCampaignDetails", mexc.getMessage());
        }
        return totalCampaignDetails;
    }
    
    public void insertDefaultOutgoingCampaign(ObjectId serverID,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document whereQry = new Document("isDefault", true);
            MongoCursor cursor = collection.find(whereQry).iterator();
            if(cursor.hasNext())
                return;
            Document insertDoc = new Document();
            insertDoc.append("campaignName", "Default Outgoing");
            insertDoc.append("dialMethod", "manual");
            insertDoc.append("isGeneralPolling",true);
            insertDoc.append("wrapUpTime", 120);
            insertDoc.append("timeBetweenCall",0);
            insertDoc.append("status", "active");
            insertDoc.append("showFields",new ArrayList<>());
            insertDoc.append("dispositions",new ArrayList<>());
            insertDoc.append("campaignCreatedOn", new util().generateNewDateInYYYYMMDDFormat());
            insertDoc.append("deleted", 0);
            insertDoc.append("isDefault", true);
            collection.insertOne(insertDoc);
        }
        catch(MongoException mexc){
            LogClass.logMsg("CampaignDB", "insertDefaultOutgoingCampaign", mexc.getMessage());
        }
    }
        
    /*
        Get Campaign Details Using CampaignID
    */
    public CampaignModel getCampaignDetailsUsingID(String campaignID){
        CampaignModel campaign = null;
        /*
            Changed By Bharath on 23/04/2016
            Cahnged because getting exception when the passed campaignID IS NULL
        */
        if(campaignID==null)
            return campaign;
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document whereQry = new Document("_id",new ObjectId(campaignID));
            whereQry.append("deleted", 0);
            MongoCursor cursor = collection.find(whereQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                campaign = convertDBObjecttoJavaObject(dc);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CampaignDB", "getCampaignDetailsUsingID", mexc.getMessage());
        }
        return campaign;
    }
    
    /*
        Check Whether Campaign With Same Name Already Exists
    */
    public CampaignModel getCampaignByName(String campaignName){
        MongoClient conn = null;
        Document dc=null;
        try{
            conn = DBClass.getInstance().getConnection();
            MongoDatabase db=conn.getDatabase(DBClass.MDATABASE);
            MongoCollection<Document> collection=db.getCollection("campaign");
            Document whereQry = new Document("campaignName",campaignName);
            whereQry.append("deleted", 0);
            dc = collection.find(whereQry).first();
        }
        catch(MongoException ex){
            ex.printStackTrace();
        }
        return convertDBObjecttoJavaObject(dc);
    }
       
     //load campaign dropdown in usercampaignmapping
    public ArrayList<CampaignModel> loadCampaignName() {
        ArrayList<CampaignModel> totalcmpdetails = new ArrayList<>();
        MongoClient conn = null;
        try{
            conn = DBClass.getInstance().getConnection();
            MongoDatabase mdb = conn.getDatabase(DBClass.MDATABASE);
            MongoCollection mc = mdb.getCollection("campaign");
            Document wherequery = new Document("deleted",0);
            MongoCursor cursor = mc.find().filter(wherequery).iterator();
            while(cursor.hasNext())
            {
                Document dc = (Document) cursor.next();
                CampaignModel cm= this.convertDBObjecttoJavaObject(dc);
                totalcmpdetails.add(cm);
            }
        }
        catch(MongoException mex){
            mex.printStackTrace();
        }        
        return totalcmpdetails;
    }

    
    public CampaignModel convertDBObjecttoJavaObject(Document dc){
        CampaignModel campaigndetails  = null;
        if(dc==null)
            return campaigndetails;
        //
        
        boolean isDefault = false;
        Date listCreatedOn = null;
        Date campaignCreatedOn = null;
        String trunkValue = null;
        String serverID = null;
        int noOfAgentTries = 0;
        int noOfClientTries = 0;
        int wrapUpTime = 0;
        int timeBetweenCall = 0;
        boolean isGeneralPolling = false;
        String listID = null;
        String didNumber = null;
        String campaignSource = null;
        String moduleLinked = null;
        String callDirection = null;
        String linkedModuleCollection = null;
        boolean branchWise = false;
        String sortField = null;
        String moduleForNewClients = null;
        String resourceURL = null;
        String iFrameUrl = null;
        boolean callBackCampaign = false;
        String newClientURL = null;
        String existingClientURL = null;
        String cdrURL = null;
        int retryAfter = 0;
        
        try{
            branchWise = dc.getBoolean("branchWise");
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            isDefault = dc.getBoolean("isDefault");
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            sortField = dc.getString("sortField");
        }
        catch(NullPointerException nexc){
            
        }
        
        try{
            listCreatedOn = dc.getDate("listCreatedOn");
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            campaignCreatedOn = dc.getDate("campaignCreatedOn");
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            wrapUpTime = new util().returnIntegerValueFromObject(dc.get("wrapUpTime"));
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            timeBetweenCall = new util().returnIntegerValueFromObject(dc.get("timeBetweenCall"));
        }
        
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            isGeneralPolling = dc.getBoolean("isGeneralPolling");
        }
        
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            trunkValue = dc.getString("trunkValue");
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            serverID = dc.getObjectId("serverID").toString();
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            noOfAgentTries = new util().returnIntegerValueFromObject(dc.get("noOfAgentTries"));
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            noOfClientTries = new util().returnIntegerValueFromObject(dc.get("noOfClientTries"));
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            listID = dc.getString("listID");
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            didNumber = dc.getString("didNumber");
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            campaignSource = dc.getString("campaignSource");
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            moduleLinked = dc.getString("moduleLinked");
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            callDirection = dc.getString("callDirection");
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            linkedModuleCollection = dc.getString("linkedModuleCollection");
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            moduleForNewClients = dc.getString("moduleForNewClients");
        }
        catch(Exception nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            resourceURL = dc.getString("resourceURL");
        }
        catch(Exception nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            iFrameUrl = dc.getString("iFrameUrl");
        }
        catch(Exception nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            callBackCampaign = dc.getBoolean("callBackCampaign");
        }
        catch(Exception nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            newClientURL = dc.getString("newClientURL");
        }
        catch(Exception nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            existingClientURL = dc.getString("existingClientURL");
        }
        catch(Exception nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            cdrURL = dc.getString("cdrURL");
        }
        catch(Exception nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        
        try{
            retryAfter = new util().returnIntegerValueFromObject(dc.get("retryAfter"));
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("CampaignDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        campaigndetails = new CampaignModel(dc.getObjectId("_id").toString(),dc.getString("campaignName"),
            dc.getString("dialMethod"),dc.getString("status"),wrapUpTime,timeBetweenCall,isGeneralPolling,listID,isDefault,listCreatedOn,
            campaignCreatedOn,trunkValue,serverID,noOfClientTries,noOfAgentTries,didNumber,campaignSource,moduleLinked,
            callDirection,linkedModuleCollection,branchWise,sortField,resourceURL,iFrameUrl,callBackCampaign,
            newClientURL,existingClientURL,cdrURL,retryAfter
        );
        campaigndetails.setModuleForNewClients(moduleForNewClients);
        
        return campaigndetails;
    }
    
    /*
        To Get Details Of Number(CTI)
    */
    public Document checkAndGetDetailsForNumber(CampaignModel campaign,String phoneNumber,MongoClient mongoConn){
        Document insertDoc = new Document();
        try{
            String collectionName = null;
            ArrayList<String> phoneFields = new ArrayList<>();
            String nameField = null;
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            switch(campaign.getCampaignSource()){
                case "base":
                    collectionName = "lst_".concat(campaign.getListID());
                    phoneFields.add("phone1");
                    phoneFields.add("phone2");
                    phoneFields.add("phone3");
                    nameField = "name";
                    MongoCollection collection = mongoDB.getCollection(collectionName);

                    GetBasicContactInfo basicContactInfo = new GetBasicContactInfo();
                    BasicDBList orCond = basicContactInfo.returnOrCondtionForContactSearch(phoneNumber,phoneFields);
                    Document whereQry = new Document("$or", orCond);

                    MongoCursor cursor = collection.find(whereQry).iterator();
                    if(cursor.hasNext()){
                        insertDoc.append("iFrameUrl", null);
                        Document dc = (Document) cursor.next();
                        ArrayList<ShowFieldModel> showField = new ShowFieldDAO().getShowFieldData(campaign.getCampaignID());
                        insertDoc = basicContactInfo.convertDBObjectToJavaObject(dc, nameField,phoneFields,showField,campaign.getSortField(),mongoConn);
                        insertDoc.append("moduleLinked", false);
                    }
                    break;
                case "modulelinked":
                    String resourceUrl = campaign.getResourceURL();
                    JsonNode clientData = new GetClientIdForNumber(phoneNumber, resourceUrl).run();
                    if(clientData!=null){
                        String clientId = clientData.get("id").asText();
                        insertDoc.append("custID", clientId);
                        insertDoc.append("name", clientData.get("name").asText());
                        /*
                            Changed By Bharath 0n 23/04/2016
                            Changing the IF statement. Adding additional condition
                            Adding because, for CRM dialer campaign we want it to go to else statement
                            So adding additional check of dialMethod
                            Also for CRMDialerCampaign we will ADD RESTful service URL in iFrameURL.
                            So IFrameURL will not be null and it is not a valid URL of the client
                            So adding that additional condition to by pass the IF condition
                        */
                        if(campaign.getPopupIFrameUrl()!=null && 
                            campaign.getPopupIFrameUrl().length()>0 && 
                                !"progressive".equalsIgnoreCase(campaign.getDialMethod())){
                            String iFrameUrl = campaign.getPopupIFrameUrl().replace("{clientId}", clientId);
                            insertDoc.append("iFrameUrl", iFrameUrl);
                        }
                        else{
                            String existingClientURL = campaign.getExisitingClientURL().replace("{clientId}", clientId);
                            insertDoc.append("existingClientURL", existingClientURL);
                        }
                        insertDoc.append("moduleLinked", true);
                    }
                    /*collectionName = campaignDetails.getLinkedModuleCollection();
                    PremptedModulesModel dataFromPremptedModule = this.getDataFromPremptedModule(campaignDetails.getLinkedModuleCollection(), mongoConn);
                    phoneFields = dataFromPremptedModule.getPhoneFields();
                    nameField = dataFromPremptedModule.getNameFieldKey();*/
                    
                    break;
                case "popupincrm":
                    insertDoc.append("popupincrm", true);
                    break;
                case "apicampaign":
                    insertDoc.append("apicampaign", true);
                    break;
                    
                case "none":
                    
                    break;
            }
            
            if(!insertDoc.isEmpty()){
                BasicDBList dispositionData = new DispositionDAO().getDispositionAsBasicDBList(campaign.getCampaignID(),mongoConn);
                insertDoc.append("disposition", dispositionData);
                insertDoc.append("campaignName", campaign.getCampaignName());
                insertDoc.append("listID", campaign.getListID());
                insertDoc.append("campaignID", new ObjectId(campaign.getCampaignID()));
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CampaignDB", "checkAndGetDetailsForNumber", mexc.getMessage());
        }
        return insertDoc;
    }
    
    /*
        Get popup Auto Close Time
    */
    public int getPopupWrapTimeFromCampaign(ObjectId campaignID,String callDirection,MongoClient mongoConn){
        int wrapUpTime = 0;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document whereQry = new Document("_id", campaignID);
            Document selectQry = new Document("wrapUpTime", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                wrapUpTime = new util().returnIntegerValueFromObject(dc.get("wrapUpTime"));
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CampaignDB", "getPopupWrapTimeFromCampaign", mexc.getMessage());
        }
        return wrapUpTime;
    }
    
    /*
        Get CampaignID Inserted In Popup(Used For Agent Performance and Auto Close Popup)
    */
    public HashMap getCampaignIDNameTypeOfCallandListIDFromPopup(String popupID,String serverNamePrefix,String extension,MongoClient mongoConn){
        HashMap campaignInfo = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(new util().returnCollectionNameForPopup(serverNamePrefix, extension));
            Document whereQry = new Document("_id", new ObjectId(popupID));
            Document selectQry = new Document("campaignID", 1);
            selectQry.append("campaignName", 1);
            selectQry.append("typeOfDialer", 1);
            selectQry.append("callType", 1);
            selectQry.append("listID", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                campaignInfo = new HashMap();
                try{
                    campaignInfo.put("campaignID", dc.getObjectId("campaignID").toString());
                }
                catch(NullPointerException nexc){
                    campaignInfo.put("campaignID", null);
                }
                try{
                    campaignInfo.put("campaignName", dc.getString("campaignName"));
                }
                catch(NullPointerException nexc){
                    campaignInfo.put("campaignName", null);
                }
                try{
                    campaignInfo.put("typeOfDialer", dc.getString("typeOfDialer"));
                }
                catch(NullPointerException nexc){
                    campaignInfo.put("typeOfDialer", null);
                }
                try{
                    campaignInfo.put("callType", dc.getString("callType"));
                }
                catch(NullPointerException nexc){
                    campaignInfo.put("callType", null);
                }
                try{
                    campaignInfo.put("listID", dc.getString("listID"));
                }
                catch(NullPointerException nexc){
                    campaignInfo.put("listID", null);
                }
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CampaignDB", "getCampaignIDNameTypeOfCallFromPopup", mexc.getMessage());
        }
        return campaignInfo;        
    }
    
    public void updateAvgTime(String campaignID,String fieldToUpdate,double avgTime,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document whereQry = new Document("_id", new ObjectId(campaignID));
            Document updateQry = new Document("avg".concat(fieldToUpdate), avgTime);
            collection.updateOne(whereQry, updateQry);
        }
        catch(MongoException mexc){
            LogClass.logMsg("CampaignDB", "updateAvgTime", mexc.getMessage());
        }
    }
    
    /*
        Get Default Campaign Info Of Inbound and Outbound based on Call Dierction
    */
    public CampaignModel getDefaultCampaignDetails(String callDirection,MongoClient mongoConn){
        CampaignModel defaultCampaign = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document whereQry = new Document("isDefault", true);
            whereQry.append("campaignSource", new Document("$ne", "modulelinked"));
            whereQry.append("callDirection", callDirection);
            MongoCursor cursor = collection.find(whereQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                defaultCampaign = this.convertDBObjecttoJavaObject(dc);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CampaignDB", "getDefaultCampaignDetails", mexc.getMessage());
        }
        return defaultCampaign;
    }
    
    public long getCountofOpenedLead(String listID,String extension,MongoClient mongoConn){
        long openLeadCount = 0;
        try{
            MongoDatabase db=mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection=db.getCollection("lst_".concat(listID));
            BasicDBList bl=new BasicDBList();
            bl.add(new Document("leadStatus","open"));
            bl.add(new Document("leadStatus", "Try Next No"));
            bl.add(new Document("leadStatus", "Try Again Later"));
            Document whereQuery = new Document("$or",bl);
            if(extension.length()>0)
                whereQuery.append("extension",extension);
            
            openLeadCount = collection.count(whereQuery);
        }
        catch(MongoException e){
            LogClass.logMsg("CampaignDB", "getCountofOpenedCalls", e.getMessage());
        }
        return openLeadCount;
    }
    
    public long getCountofTotalLeads(String listID,String extension,MongoClient mongoConn){
        long totalLeadCount = 0;
        try{
            MongoDatabase db=mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection=db.getCollection("lst_"+listID);
            Document whereQuery = new Document();
            if(extension.length()>0)
                whereQuery.append("extension", extension);
            
            
            totalLeadCount = collection.count(whereQuery);
        }
        catch(MongoException e){
            LogClass.logMsg("CampaignDB", "getCountofTotalLeads", e.getMessage());
        }
        return totalLeadCount;
    }
    
    public long getCountofClosedLeads(String listID,String extension,MongoClient mongoConn){
        long closedLeadCount = 0;
        try{
            MongoDatabase db=mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection=db.getCollection("lst_"+listID);
            BasicDBList bl = new BasicDBList();
            bl.add(new Document("leadStatus","Closed Lead"));
            bl.add(new Document("leadStatus", "Closed Due To WrapUp"));
            Document whr=new Document("$or",bl);
            if(extension.length()>0)
                whr.append("extension",extension);
            
            closedLeadCount = collection.count(whr);
        }
        catch(MongoException e){
            LogClass.logMsg("CampaignDB", "getCountofClosedLeads", e.getMessage());
        }
        return closedLeadCount;
    }
    
    public long getCountofCallBackLead(String listID,String extension,MongoClient mongoConn){
        long callBackLeadCount = 0;
        try{
            MongoDatabase db=mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection=db.getCollection("lst_"+listID);
            Document whereQuery = new Document("leadStatus","Call Back");
            if(extension.length()>0)
                whereQuery.append("extension", extension);
            callBackLeadCount = collection.count(whereQuery);
        }
        catch(MongoException e){
            LogClass.logMsg("CampaignDB", "getCountofCallBackLead", e.getMessage());
        }
        return callBackLeadCount;
    }
    
    public String getCampaignNameForID(String campaignID){
        String campaignName = null;
        MongoClient mongoConn = null;
        try{
            mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document wherequery = new Document("_id",new ObjectId(campaignID));
            MongoCursor cursor = collection.find().filter(wherequery).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                campaignName = dc.getString("campaignName");
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CampaignDB", "getCampaignNameForID", mexc.getMessage());
        }
        return campaignName;
    }
    
    public ArrayList<String> getServerNamePrefixForCampaignID(String campaignID,MongoClient mongoConn){
        ArrayList<String> serverNamePrefixForCampaign = new ArrayList<>();
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            Document whereQry = new Document("campaignID", new ObjectId(campaignID));
            Document distinctQry = new Document("distinct", "usercampaignmapping");
            distinctQry.append("key", "serverNamePrefix");
            distinctQry.append("query", whereQry);
            Document distinctResult = mongoDB.executeCommand(distinctQry);
            if(distinctResult!=null){
                serverNamePrefixForCampaign = (ArrayList<String>) distinctResult.get("values");
            }     
        }
        catch(MongoException mexc){
            LogClass.logMsg("CampaignDB", "getServerNamePrefixForCampaignID", mexc.getMessage());
        }
        return serverNamePrefixForCampaign;
    }
    
    public PremptedModulesModel getDataFromPremptedModule(String collectionName,MongoClient mongoConn){
        PremptedModulesModel dataFromPremptedModule = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("premptedmodules");
            Document whereQry = new Document("collectionName", collectionName);
            MongoCursor<Document> cursor = collection.find(whereQry).iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                dataFromPremptedModule = new PremptedModulesModel();
                dataFromPremptedModule.setPhoneFields((ArrayList<String>) dc.get("phoneFields"));
                dataFromPremptedModule.setSortFields((ArrayList<String>) dc.get("sortFields"));
                dataFromPremptedModule.setModuleName(dc.getString("name"));
                dataFromPremptedModule.setModuleCollection("collectioName");
                dataFromPremptedModule.setNameFieldKey(dc.getString("nameFieldKey"));
                
            }
        }
        catch(MongoException ex){
            
        }
        return dataFromPremptedModule;
    }
    
    public CampaignModel getDefaultModuleLinkedCampaign(MongoClient mongoConn,String callDirection){
        CampaignModel allCampaign = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document whereQry = new Document("campaignSource", "modulelinked");
            whereQry.append("callDirection", callDirection);
            MongoCursor<Document> cursor = collection.find(whereQry).iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                allCampaign = this.convertDBObjecttoJavaObject(dc);
            }
        }
        catch(MongoException ex){
            
        }
        return allCampaign;
    }
    
    public CampaignModel getAllPopupInCRMCampaigns(MongoClient mongoConn,String callDirection){
        CampaignModel allCampaign = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document whereQry = new Document("campaignSource", "popupincrm");
            whereQry.append("callDirection", callDirection);
            whereQry.append("deleted", 0);
            MongoCursor<Document> cursor = collection.find(whereQry).limit(1).iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                allCampaign = this.convertDBObjecttoJavaObject(dc);
            }
        }
        catch(MongoException ex){
            
        }
        return allCampaign;
    }
    
    public CampaignModel getAllAPICampaigns(MongoClient mongoConn,String callDirection){
        CampaignModel allCampaign = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document whereQry = new Document("campaignSource", "apicampaign");
            whereQry.append("callDirection", callDirection);
            whereQry.append("deleted", 0);
            MongoCursor<Document> cursor = collection.find(whereQry).limit(1).iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                allCampaign = this.convertDBObjecttoJavaObject(dc);
            }
        }
        catch(MongoException ex){
            
        }
        return allCampaign;
    }
    /*
        1) Used to get all missedCall(dialMethod) campaign from DB
    */
    public ArrayList getAllMissedCallCampaigns(MongoClient mongoConn){
        ArrayList<CampaignModel> missedCallCampaigns = new ArrayList<>();
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document whereQry = new Document("deleted", 0);
            BasicDBList orCond = new BasicDBList();
            orCond.add(new Document("dialMethod", "missedCall"));
            orCond.add(new Document("callBackCampaign", true));
            whereQry.append("$or", orCond);
            
            
            MongoCursor cursor = collection.find(whereQry).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                missedCallCampaigns.add(this.convertDBObjecttoJavaObject(dc));
            }
        }
        catch(MongoException mecx){
            LogClass.logMsg("CampaignDB", "getAllMissedCallTrunks", mecx.getMessage());
        }
        return missedCallCampaigns;
    }
    
    public ArrayList<CampaignModel> getDefaultModuleLinkedCampaign(MongoClient mongoConn){
        ArrayList<CampaignModel> allCampaign = new ArrayList<>();
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document whereQry = new Document("isDefault", true);
            whereQry.append("campaignSource", "modulelinked");
            MongoCursor<Document> cursor = collection.find(whereQry).iterator();
            while(cursor.hasNext()){
                Document dc = cursor.next();
                allCampaign.add(this.convertDBObjecttoJavaObject(dc));
            }
        }
        catch(MongoException ex){
            
        }
        return allCampaign;
    }
}
