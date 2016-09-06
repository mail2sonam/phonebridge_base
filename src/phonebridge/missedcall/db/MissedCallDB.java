/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridge.missedcall.db;

import campaignconfig.db.CampaignDB;
import campaignconfig.model.CampaignModel;
import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.phonebridgecti.dao.BranchDAO;
import com.phonebridgecti.dao.CampaignDAO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import phonebridge.missedcall.model.MissedCallRequestModel;
import phonebridge.util.LogClass;
import phonebridge.util.util;
import processdbevents.model.CdrModel;
import singleton.db.DBClass;

/**
 *
 * @author venky
 */
public class MissedCallDB {
    public ArrayList<MissedCallRequestModel> getAllMissedCallRequest(String listID,String branchID,MongoClient mongoConn){
        ArrayList<MissedCallRequestModel> missedCallReqs = new ArrayList<>();
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("lst_".concat(listID));
            Document whereQuery = new Document();
            whereQuery.append("currentStatus", "Not Initiated");
            whereQuery.append("calledBack", false);
            whereQuery.append("noOfClientTries", 0);
            if(branchID!=null)
                whereQuery.append("branchID", new ObjectId(branchID));
            Document selectQuery = new Document();
            selectQuery.append("phoneNumber", 1);
            selectQuery.append("lastExtnTried", 1);
            selectQuery.append("trunk", 1);
            selectQuery.append("dateEntered", 1);
            
            Document sortQuery = new Document("priority",1);
            sortQuery.append("dateEntered", 1);
            MongoCursor cursor = collection.find(whereQuery).projection(selectQuery).sort(sortQuery).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                MissedCallRequestModel missedCallReq = new MissedCallRequestModel();
                missedCallReq.setPhoneNumber(dc.getString("phoneNumber"));
                missedCallReq.setLastExtensionTried(dc.getString("lastExtnTried"));
                missedCallReq.setMissedCallReqID(dc.getObjectId("_id").toString());
                missedCallReq.setTrunk(dc.getString("trunk"));
                missedCallReq.setDateEntered(dc.getDate("dateEntered"));
                if(branchID!=null)
                    missedCallReq.setBranchID(branchID);
                missedCallReqs.add(missedCallReq);
            }
        }
        catch(MongoException mecx){
            LogClass.logMsg("missedCallDB", "getmissedCallCallBackNumber", mecx.getMessage());
        }
        return missedCallReqs;
    }
    
    public ArrayList<MissedCallRequestModel> getAllRetryMissedCallRequest(String listID,String branchID,MongoClient mongoConn){
        ArrayList<MissedCallRequestModel> missedCallReqs = new ArrayList<>();
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("lst_".concat(listID));
            Document whereQuery = new Document();
            whereQuery.append("currentStatus", "Not Initiated");
            whereQuery.append("calledBack", false);
            whereQuery.append("noOfClientTries",new Document("$gte",1));
            whereQuery.append("retryOn", new Document("$lte",new Date()));
            if(branchID!=null)
                whereQuery.append("branchID", new ObjectId(branchID));
            Document selectQuery = new Document();
            selectQuery.append("phoneNumber", 1);
            selectQuery.append("lastExtnTried", 1);
            selectQuery.append("trunk", 1);
            selectQuery.append("dateEntered", 1);
            
            Document sortQuery = new Document("priority",1);
            sortQuery.append("dateEntered", 1);
            MongoCursor cursor = collection.find(whereQuery).projection(selectQuery).sort(sortQuery).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                MissedCallRequestModel missedCallReq = new MissedCallRequestModel();
                missedCallReq.setPhoneNumber(dc.getString("phoneNumber"));
                missedCallReq.setLastExtensionTried(dc.getString("lastExtnTried"));
                missedCallReq.setMissedCallReqID(dc.getObjectId("_id").toString());
                missedCallReq.setTrunk(dc.getString("trunk"));
                missedCallReq.setDateEntered(dc.getDate("dateEntered"));
                if(branchID!=null)
                    missedCallReq.setBranchID(branchID);
                missedCallReqs.add(missedCallReq);
            }
        }
        catch(MongoException mecx){
            LogClass.logMsg("missedCallDB", "getmissedCallCallBackNumber", mecx.getMessage());
        }
        return missedCallReqs;
    }
    
    public void updateMissedCallReqStatus(String listID,String reqID,String extension,String status,String reason,Date retryOn,
            MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("lst_"+listID);
            Document whereQry = new Document("_id",new ObjectId(reqID));
            whereQry.append("currentStatus",new Document("$ne", "Completed"));
            
            Document selectQry = new Document("listOfExtensionsTried", 1);
            
            MongoCursor<Document> cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document updateQuery = new Document();
                updateQuery.append("currentStatus", status);
                if(status.contains("Completed")){
                    updateQuery.append("completedOn", new Date());
                    updateQuery.append("completedByExtn", extension);
                    updateQuery.append("reason", reason);
                    updateQuery.append("calledBack", true);
                }
                else
                    if(retryOn!=null)
                        updateQuery.append("retryOn", retryOn);
                if(extension!=null)
                    updateQuery.append("lastExtnTried", extension);
                updateQuery.append("processedOn", new Date());
                
                collection.updateOne(whereQry,new Document("$set", updateQuery));
                
                /*
                    Updating extension tried and respective time inside an array
                */
                if(!status.contains("Completed") && extension!=null){
                    Document extensionUpdateQry = new Document("extension", extension);
                    extensionUpdateQry.append("updateTime", new util().generateNewDateInYYYYMMDDFormat());
                    collection.updateOne(whereQry, new Document("$push", new Document("extensionsTried", extensionUpdateQry)));
                }
                
                /*
                    Updating listOfExtesions as string for displaying in front
                */
                Document dc = cursor.next();
                String listOfExtensionsTried = null;
                try{
                    listOfExtensionsTried = dc.getString("listOfExtensionsTried");
                }
                catch(Exception ex){
                    
                }
                if(listOfExtensionsTried==null && extension!=null)
                    listOfExtensionsTried = extension;
                else if(listOfExtensionsTried!=null && extension!=null)
                    listOfExtensionsTried = listOfExtensionsTried.concat(",").concat(extension);
                
                if(listOfExtensionsTried!=null && listOfExtensionsTried.length()>0){
                    collection.updateOne(whereQry,new Document("$set", new Document("listOfExtensionsTried", listOfExtensionsTried)));
                }
            }
        }
        catch(MongoException mecx){
            LogClass.logMsg("missedCallDB", "updateMissedCallReqStatus", mecx.getMessage());
        }
    }
    
    
    public void originateCall(String prefix, String phoneNumber,String userExtension, String typeOfDialer, 
        String serverNamePrefix,int delayBeforeCall,String popupObjectID,String campaignID,String dialOutContext,
        String campaignName,String customerID,boolean followMeExists,String callID,String extensionType,String followMeNumber){
        MongoClient conn = null;
        try{   
            conn = DBClass.getInstance().getConnection();
            MongoDatabase mdb = conn.getDatabase(DBClass.MDATABASE);
            MongoCollection mc = mdb.getCollection(serverNamePrefix+"_originatecall");
            Long dt = new util().generateNewDateInYYYYMMDDFormat("UTC").getTime();
            dt+=(delayBeforeCall*1000);
            Document insertQuery = new Document("prefix", prefix)
                .append("phoneNumber", phoneNumber)
                .append("userExtension", userExtension).append("typeOfDialer", typeOfDialer)
                .append("callOn", dt).append("status", "toInitiate")
                .append("hasAgentAnswered", "No")
                .append("popupID", null)
                .append("campaignID",new ObjectId(campaignID))
                .append("callContext",dialOutContext)
                .append("custID",null)
                .append("followMeExists",followMeExists)
                .append("campaignName",campaignName)
                .append("extensionType", extensionType)
                .append("followMeNumber", followMeNumber)
                .append("_id", new ObjectId(callID));
            mc.insertOne(insertQuery);
        }
        catch(Exception ex){
            ex.getMessage();
        }
    }
    
    public ArrayList<String> getExtensionToOrignateMissedCall(String campaignID,String branchID,MongoClient mongoConn){
        ArrayList<String> extensionDetails = new ArrayList<>();
        try{
            if(branchID==null)
                branchID = new BranchDAO().getBranchIDByBranchCode("HO");
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("usercampaignmapping");
            Document whereQuery = new Document();
            whereQuery.append("campaignID", new ObjectId(campaignID));
            if(branchID!=null)
                whereQuery.append("branchID", new ObjectId(branchID));
            whereQuery.append("deleted", 0);
            Document selectQuery = new Document("extension", 1);
            MongoCursor cursor = collection.find(whereQuery).projection(selectQuery).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                extensionDetails.add(dc.getString("extension"));
            }
        }
        catch(Exception ex){
            ex.getMessage();
        }
        return extensionDetails;
    }
    
    public ArrayList<String> getTotalExtensionsForMissedCallCampaign(String campaignID,String branchID,MongoClient mongoConn){
        ArrayList<String> extensions=new ArrayList<String>();
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("usercampaignmapping");
            Document whereQuery = new Document();
            whereQuery.append("campaignID", new ObjectId(campaignID));
            whereQuery.append("deleted", 0);
            //whereQuery.append("", this)
            if(branchID!=null)
                whereQuery.append("branchID", new ObjectId(branchID));
            whereQuery.append("extension", new Document("$ne", null));
            MongoCursor cur=collection.find(whereQuery).iterator();
            while(cur.hasNext())
            {
            Document doc=(Document)cur.next();
            extensions.add(doc.getString("extension"));
            }
        }
        catch(Exception ex){
            ex.getMessage();
        }
        return extensions;
    }
    
    public HashMap getNoOfAgentAndClientTries(String listID,String originateReqID,MongoClient mongoConn){
        HashMap<String,Integer> noOfTries = new HashMap<>();
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("lst_".concat(listID));
            Document whereQuery = new Document("_id",new ObjectId(originateReqID));
            
            Document selectQuery = new Document();
            selectQuery.append("noOfAgentTries", 1);
            selectQuery.append("noOfClientTries", 1);
            MongoCursor cursor = collection.find(whereQuery).projection(selectQuery)/*.sort(sortQuery)*/.iterator();
            if(cursor.hasNext()){
                util utilObj = new util();
                Document dc = (Document) cursor.next();
                noOfTries.put("agentTries", utilObj.returnIntegerValueFromObject(dc.get("noOfAgentTries")));
                noOfTries.put("clientTries", utilObj.returnIntegerValueFromObject(dc.get("noOfClientTries")));
            }
        }
        catch(MongoException mecx){
            mecx.getMessage();
        }
        return noOfTries;
    }
    
    public void incrementAgentOrClientRetry(String fieldToInc,String listID,String missedCallID,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("lst_".concat(listID));
            Document whereQry = new Document("_id", new ObjectId(missedCallID));
            whereQry.append("currentStatus", "processing");
            Document updateQry = new Document(fieldToInc, 1);
            collection.updateOne(whereQry, new Document("$inc", updateQry));
        }
        catch(MongoException ex){
            
        }
    }
    
    public void deleteRecFromOriginateCallWithID(String originateCallID,String serverNamePrefix,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(serverNamePrefix.concat("_originatecall"));
            collection.deleteOne(new Document("_id", new ObjectId(originateCallID)));
        }
        catch(MongoException ex){
            
        }
    }
    
    public String getOriginateReqIDUsingPhoneNumber(String listID,String phoneNumber,String prefix,MongoClient mongoConn){
        String originateReqID = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("lst_".concat(listID));
            String tempPhoneNumber = null;
            if(phoneNumber.startsWith(prefix))
                tempPhoneNumber = phoneNumber.substring(prefix.length());
            Document whereQry = new Document("phoneNumber", tempPhoneNumber);
            whereQry.append("currentStatus", "processing");
            whereQry.append("calledBack", false);
            Document selectQry = new Document("_id", 1);
            MongoCursor<Document> cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                originateReqID =  dc.getObjectId("_id").toString();
            }
        }
        catch(MongoException ex){
            
        }
        return originateReqID;
    }
    
    public boolean checkIfExtensionFreeFromCurrentCalls(String extension,String serverNamePrefix,MongoClient mongoConn){
        boolean extensionFree = true;
        try{
            util utilObj = new util();
            Date javaDate = utilObj.genDateInYYYYMMDDFormat();
            String mmYYYY = utilObj.getMMYYYYForJavaDate(javaDate.getTime());
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(utilObj.returnCollectionNameForCurrentCalls(mmYYYY, serverNamePrefix));
            DateTime startTime = utilObj.convertJavaDateToMongoDate(javaDate.getTime());
            Document whereQry = new Document("isClosed", false);
            whereQry.append("eventDateTime", new Document("$gte", startTime.toDate()));
            whereQry.append("extension", extension);
            MongoCursor cursor = collection.find(whereQry).iterator();
            if(cursor.hasNext()){
                extensionFree = false;
            }
        }
        catch(MongoException ex){
            
        }
        return extensionFree;
    }
    
    /*
        1) Used to get the extension sorted in order of attended call count to route the call to the least 
            called extension
    */
    public ArrayList<String> getExtensionBasedOnNoOfCallsAnswered(String listID,String branchID,
        ArrayList<String> extensionsToCheck,MongoClient mongoConn){
        ArrayList<String> extension = new ArrayList<>();
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("lst_".concat(listID));
            Document whereQry = new Document("lastExtnTried", new Document("$ne", null));
            whereQry.append("calledBack", true);
            whereQry.append("dateEntered", new Document("$gt", new util().genDateInYYYYMMDDFormat()));
            whereQry.append("lastExtnTried", new Document("$in", extensionsToCheck));
            if(branchID!=null)
                whereQry.append("branchID", new ObjectId(branchID));
            Document matchQry = new Document("$match", whereQry);
            
            Document grpQry = new Document("_id", "$lastExtnTried");//new Document("_id", "$completedByExtn");
            grpQry.append("count", new Document("$sum", 1));
            Document groupQry = new Document("$group", grpQry);
            
            Document sortQry = new Document("$sort", new Document("count", 1));
            
            Document selectQry = new Document("_id", 1);
            Document projectQry = new Document("$project", selectQry);
            
            MongoCursor<Document> cursor = collection.aggregate(Arrays.asList(matchQry,groupQry,sortQry,projectQry)).iterator();
            while(cursor.hasNext()){
                Document dc = cursor.next();
                extension.add(dc.getString("_id"));
            }
        }
        catch(Exception ex){
            
        }
        return extension;
    }
    
    /*
        1) Check if cdr belongs to a missed call given by the customer
        2) If so, then insert request, if a request is not already pending
    */
    public boolean checkIfMissedCallCdr(CdrModel cdr,String mmYYYY,String serverID,String serverNamePrefix,
        MongoClient mongoConn){
        boolean missedCallCdr = false;
        try{
            String phoneNumber = null;
            ArrayList<CampaignModel> missedCallCampaigns = new CampaignDAO().getAllMissedCallCampaigns(mongoConn);
            for(CampaignModel campaignDetails : missedCallCampaigns){
                if(cdr.getDestination().equals(campaignDetails.getDidNumber())){
                    phoneNumber = new util().stripAdditionalInfoFromNumber(cdr.getSource());
                    if(phoneNumber.matches("-?\\d+(\\.\\d+)?")){
                        missedCallCdr = true;
                        this.checkAndInsertForMissedCall(campaignDetails, phoneNumber, mongoConn);
                        break;
                    }
                }
            }
        }
        catch(Exception ex){
            
        }
        return missedCallCdr;
    }
    
    public boolean checkAndInsertForMissedCall(CampaignModel campaign,String phoneNumber,MongoClient mongoConn){
        /*
            Changed By Bharath 17/04/2016
            Returning boolean value from this function
            Modified because to identify whether this call was processed in BCCB before continuing with MCCB
        */
        boolean missedCall = false;
        Date completedOn = null;
        String currentStatus = "Not Initiated";
        Date processedOn = null;
        String reason = "";
        boolean calledBack = false;
        boolean customerAnswered = false;
        boolean agentAnswered = false;
        /*
            Checking if there is a pending request for this phoneNumber and calledBack status to be false
        */
        if(this.checkIfMissedCallAlreadyPending(campaign.getListID(),phoneNumber, mongoConn)){
            completedOn = new Date();
            currentStatus = "Completed";
            processedOn = new Date();
            reason = "Duplicate Request";
            calledBack = true;
            customerAnswered = false;
            agentAnswered = false;
        }
        Document clientDetails = null;//this.getClientDetilsByUsingPhoneNumberAndCampaignID(campaign.getCampaignID(), phoneNumber);
        this.insertMissedCallDetails(phoneNumber,completedOn,currentStatus,processedOn,reason,calledBack,
            campaign.getDidNumber(),campaign.getListID(),customerAnswered,agentAnswered,clientDetails,mongoConn);
        return missedCall;
    }
    
    /*
        1) Used to isert missed call details into appropriate list
    */
    public void insertMissedCallDetails(String phoneNumber,Date completedOn,String currentStatus,Date processedOn,
        String reason,boolean calledBack,String missedCallTrunk,String listID,boolean customerAnswered,
        boolean agentAnswered,Document clientDetails,MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("lst_"+listID);
            Document insertQuery = new Document();
            insertQuery.append("phoneNumber", phoneNumber);
            insertQuery.append("dateEntered", new DateTime().toDate());
            insertQuery.append("completedOn", completedOn);
            insertQuery.append("completedByExtn", null);
            insertQuery.append("currentStatus", currentStatus);
            insertQuery.append("lastExtnTried", null);
            insertQuery.append("processedOn", processedOn);
            insertQuery.append("reason", reason);
            insertQuery.append("noOfClientTries", 0);
            insertQuery.append("noOfAgentTries", 0);
            insertQuery.append("trunk", missedCallTrunk);
            insertQuery.append("calledBack", calledBack);
            insertQuery.append("agentAnswered", agentAnswered);
            insertQuery.append("customerAnswered", customerAnswered);
            String priority = null;
            try{
                priority = clientDetails.getString("sortField");
            }
            catch(Exception mecx){
                System.out.println("Exception while inserting MCCB OR BCCB priority key");
            }
            if(priority==null)
                priority = "3";
            insertQuery.append("priority", priority);
            String branchID = null;
            try{
                branchID = clientDetails.getString("branchID");
            }
            catch(Exception ie){
                System.out.println("Exception while inserting MCCB OR BCCB branchID key");
            }
            if(branchID==null)
                branchID = new BranchDAO().getBranchIDByBranchCode("HO");
            insertQuery.append("branchID", new ObjectId(branchID));
            collection.insertOne(insertQuery);
        }
        catch(MongoException mecx){
            LogClass.logMsg("ProcessAMIDB", "insertMissedCallDetails", mecx.getMessage());
        }
    }
    
    /*
        1) Check if there is pending missed call request for the same phoneNumer
    */
    public boolean checkIfMissedCallAlreadyPending(String listID,String phoneNumber,MongoClient mongoConn){
        boolean phoneNumberExist = false;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("lst_"+listID);
            Document whereQuery = new Document();
            whereQuery.append("phoneNumber", phoneNumber);
            whereQuery.append("calledBack", false);
            MongoCursor cursor = collection.find(whereQuery).iterator();
            if(cursor.hasNext()){
                phoneNumberExist = true;
            }
        }
        catch(MongoException mecx){
            LogClass.logMsg("ProcessAMIDB", "checkIfMissedCallAlreadyPending", mecx.getMessage());
        }
        return phoneNumberExist;
    }
    
    /*
        1) Get clientDetails using phoneNumber
    */
    public Document getClientDetilsByUsingPhoneNumberAndCampaignID(String campaignID,String phoneNumber){
        CampaignDAO campaignDao = new CampaignDAO();
        CampaignModel campaignDetails = campaignDao.getCampaignDetailsForID(campaignID);
        return new CampaignDB().checkAndGetDetailsForNumber(campaignDetails, phoneNumber, DBClass.getInstance().getConnection());
    }
    
    public boolean checkIfAlreadyCalled(Date timeToCheck,String serverPrefix,String phoneNumber,MongoClient mongoConn){
        boolean alreadySpoken = false;
        MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
        util utilObj = new util();
        String mmYYYY = utilObj.getMMYYYYForJavaDate(timeToCheck.getTime());
        phoneNumber = utilObj.stripAdditionalInfoFromNumber(phoneNumber);
        BasicDBList orCond = new BasicDBList();
        orCond.add(new Document("phoneNumber", "0".concat(phoneNumber)));
        orCond.add(new Document("phoneNumber", "91".concat(phoneNumber)));
        orCond.add(new Document("phoneNumber", phoneNumber));
        
        BasicDBList orCond1 = new BasicDBList();
        orCond1.add(new Document("isClosed", true).append("callConnectTime", new Document("$ne", null)));
        orCond1.add(new Document("isClosed", false));
        
        
        String collectionName = utilObj.returnCollectionNameForCurrentCalls(mmYYYY, serverPrefix);
        MongoCollection collection = mongoDB.getCollection(collectionName);
        Document whereQry = new Document("eventDateTime",new Document("$gt", timeToCheck));
        
        whereQry.append("callConnectTime", new Document("$ne", null));
        whereQry.append("$or", orCond);
        whereQry.append("$or", orCond1);
        
        if(collection.count(whereQry)>0)
            alreadySpoken = true;
        return alreadySpoken;
    }
    
    /*
        Added by Bharath on 17/04/2016
        Added for MCCB functionality
        Retrieves all missed call campaign from DB
        Identified with dialMethod - missedCall
    */
    public ArrayList getAllMissedCallCampaigns(MongoClient mongoConn){
        ArrayList<CampaignModel> missedCallCampaigns = new ArrayList<>();
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document whereQuery = new Document();
            whereQuery.append("dialMethod", "missedCall");
            whereQuery.append("deleted", 0);
            MongoCursor cursor = collection.find(whereQuery).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                missedCallCampaigns.add(new CampaignDB().convertDBObjecttoJavaObject(dc));
            }
        }
        catch(MongoException mecx){
            LogClass.logMsg("ProcessAMIDB", "getAllMissedCallTrunks", mecx.getMessage());
        }
        return missedCallCampaigns;
    }
    
    public void updateLastExtensionTriedInMissedCall(String listID,String missedCallReqId,String lastExtensionTried,
            MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("lst_"+listID);
            Document whereQry = new Document();
            whereQry.append("_id", new ObjectId(missedCallReqId));
            Document updateCondition = new Document();
            updateCondition.append("lastExtnTried", lastExtensionTried);
            Document updateQry = new Document("$set",updateCondition);
            collection.updateOne(whereQry, updateQry);
        }catch(Exception e){
            e.getMessage();
        }
    }
}
