/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campaignconfig.db;

import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import singleton.db.DBClass;
import campaignconfig.model.UserModel;
import com.phonebridgecti.db.GetBasicContactInfo;
import org.bson.Document;
import org.bson.types.ObjectId;
import phonebridge.util.LogClass;
import phonebridge.util.util;

/**
 *
 * @author harini
 */
public class UserDB {
        //list all campaigns
    public ArrayList<UserModel> getAllUserDetailsfromDB(){
        ArrayList<UserModel> allUsers = new ArrayList<>();
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("users");
            Document whereQry = new Document("deleted",0);
            whereQry.append("userType", new Document("$ne","superadmin"));
            MongoCursor cursor = collection.find().filter(whereQry).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                UserModel user = this.convertDBObjecttoJavaObject(dc);
                allUsers.add(user);
            }
        }
        catch(Exception ex){
            System.out.println("ERR IN getAllUserDetailsfromDB "+ex.getMessage());
        }
        return allUsers;
    }
    
       
    public UserModel getUserDetailsByExtension(String extension){
        MongoClient conn = null;
        UserModel userDetails = null;
        try{
            conn = DBClass.getInstance().getConnection();
            MongoDatabase db = conn.getDatabase(DBClass.MDATABASE);
            MongoCollection<Document> collection = db.getCollection("users");
            Document whereQuery = new Document();
            whereQuery.append("userExtension", extension);
            whereQuery.append("deleted", 0);
            MongoCursor cursor = collection.find(whereQuery).limit(1).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                userDetails = this.convertDBObjecttoJavaObject(dc);
            }
        }
        catch(MongoException ex){
            ex.printStackTrace();
        }  
        return userDetails;
    }
    
    //edit user details
    
    public UserModel getUserDetailsUsingID(String userID){
        UserModel user = null;
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("users");
            Document whereQry = new Document("_id", new ObjectId(userID));
            MongoCursor cursor = collection.find(whereQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                user = convertDBObjecttoJavaObject(dc);
            }
        }
        catch(Exception ex){
            System.out.println("ERR IN getUserDetailsUsingID "+ex.getMessage());
        }
        return user;
    }
   
    /*
        Get Users based on serverID - Only server specific users
    */
    public ArrayList<UserModel> getServerMappedUserDetailsFromDB(String serverID){
        ArrayList<UserModel> allUsers = new ArrayList<>();
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("users");
            Document whereQry = new Document("deleted",0);
            whereQry.append("serverID",new ObjectId(serverID));
            MongoCursor cursor = collection.find().filter(whereQry).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                UserModel user = this.convertDBObjecttoJavaObject(dc);
                allUsers.add(user);
            }
        }
        catch(Exception ex){
            System.out.println("ERR IN getServerMappedUserDetailsFromDB "+ex.getMessage());
        }
        return allUsers;
    }

    

    protected UserModel convertDBObjecttoJavaObject(Document dc){
        UserModel user  = null;
        if(dc==null)
            return user;
        String userID = null;
        String name = null;
        String userName = null;
        String password = null;
        String userExtension = null;
        String serverID = null;
        String serverNamePrefix = null;
        String serverName = null;
        String userPrefix = null;
        String followMeNumber = null;
        String address = null;
        String userMessage = null;
        String email = null;
        String phoneNumber = null;
        boolean isFollowMe = false;
        String departmentID = null;
        String designationID = null;
        String departmentName = null;
        String designationName = null;
        String branchID = null;
        String branchName = null;
        String branchCode = null;
        String extensionStatus = null;
        String context = null;
        String callStatus = null;
        String popupStatus = null;
        String onBreak = null;
        String queueName = null;
        String extensionType = null;
        
        try{
            userID = dc.getObjectId("_id").toString();
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            name = dc.getString("name");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            userName = dc.getString("userName");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            password = dc.getString("password");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            userExtension = dc.getString("userExtension");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            serverID = dc.getObjectId("serverID").toString();
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            serverNamePrefix = dc.getString("serverNamePrefix");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            serverName = dc.getString("serverName");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            userPrefix = dc.getString("prefix");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            followMeNumber = dc.getString("followNumber");
        }
        catch(NullPointerException ex){
            ex.getMessage();
        }
        try{
            address = dc.getString("address");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            userMessage = dc.getString("userMessage");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            email = dc.getString("email");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            phoneNumber = dc.getString("phoneNumber");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            isFollowMe = dc.getBoolean("isFollowMe");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            departmentID = dc.getObjectId("departmentID").toString();
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            designationID = dc.getObjectId("designationID").toString();
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            departmentName = dc.getString("departmentName");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            designationName = dc.getString("designationName");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            branchID = dc.getObjectId("branchID").toString();
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            branchName = dc.getString("branchName");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            branchCode = dc.getString("branchCode");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            extensionStatus = dc.getString("extensionStatus");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            context = dc.getString("context");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            callStatus = dc.getString("callStatus");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            popupStatus = dc.getString("popupStatus");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            onBreak = dc.getString("onBreak");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            queueName = dc.getString("queueName");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        try{
            extensionType = dc.getString("extensionType");
        }
        catch(Exception ex){
            ex.getMessage();
        }
        
        user = new UserModel(userID,name,userName,password,userExtension,serverID,serverNamePrefix,serverName,
            userPrefix,followMeNumber,address,userMessage,email,phoneNumber,isFollowMe,departmentID,designationID,
            departmentName,designationName,branchID,branchName,branchCode,extensionStatus,context,callStatus,
            popupStatus,onBreak,queueName,extensionType
        );
        return user;
    }
    
    /*
        Check If Popup For the same number Exists for the user
    */
    public String checkIfPopUpObjectAlreadyExists(String extension,String phoneNumber,String callDirection,
            String serverNamePrefix,MongoClient mongoConn){
        String popupDocID = null;
        try{
            ArrayList<String> fieldsToUse = new ArrayList<>();
            fieldsToUse.add("phoneNumber");
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection(new util().returnCollectionNameForPopup(serverNamePrefix, extension));
            Document whereQry = new Document("extension", extension);
            whereQry.append("callDirection", callDirection);
            whereQry.append("endTime", new Document("$exists", false));
            BasicDBList orCond = new GetBasicContactInfo().returnOrCondtionForContactSearch(phoneNumber, fieldsToUse);
            whereQry.append("$or", orCond);
            MongoCursor cursor = collection.find(whereQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                popupDocID = dc.getObjectId("_id").toString();
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("UserDB", "checkIfPopUpObjectAlreadyExists", mexc.getMessage());
        }
        return popupDocID;
    }
    
    public UserModel getUserNameAndIDForExtensionAndServerID(String extension,String serverID,MongoClient mongoConn){
        UserModel userDetail = null;
        try{
            userDetail = new UserModel();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("users");
            Document whereQry = new Document("userExtension", extension);
            whereQry.append("serverID", new ObjectId(serverID));
            whereQry.append("deleted", 0);
            Document selectQry = new Document("name", 1);
            selectQry.append("prefix", 1);
            selectQry.append("isFollowMe", 1);
            selectQry.append("followNumber", 1);
            selectQry.append("branchID", 1);
            selectQry.append("branchName", 1);
            selectQry.append("extensionStatus", 1);
            selectQry.append("extensionType", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                userDetail = this.convertDBObjecttoJavaObject(dc);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("UserDB", "getUserNameAndIDForExtensionAndServerID", mexc.getMessage());
        }
        return userDetail;
    }
    
    /*
        1) Check if userExistion with extension or followMe Number
    */
    public UserModel checkIfUserExistsWithExtensionOrFollowNo(String extensionOrFollowMeNo,MongoClient mongoConn){
        UserModel user = null;
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("users");
            Document whereQry = new Document("deleted", 0);
            BasicDBList orCond = new BasicDBList();
            orCond.add(new Document("userExtension",extensionOrFollowMeNo));
            orCond.add(new Document("followNumber",extensionOrFollowMeNo));
            whereQry.append("$or",orCond);
            Document proj = new Document("userExtension",1);
            proj.append("followNumber",1);
            proj.append("isFollowMe", 1);
            MongoCursor cursor = collection.find(whereQry).projection(proj).iterator();
            if(cursor.hasNext()){
                Document dc=(Document)cursor.next();
                user = new UserModel();
                user.setUserID(dc.getObjectId("_id").toString());
                try{
                    user.setIsFollowMe(dc.getBoolean("isFollowMe"));
                    if(!user.isIsFollowMe())
                        user.setFollowNumber(null);
                else
                    try{
                        user.setFollowNumber(dc.getString("followNumber"));
                    }
                    catch(NullPointerException ne){
                    
                    }
                }
                catch(NullPointerException ne){
                
                }
                try{
                    user.setExtension(dc.getString("userExtension"));
                }
                catch(NullPointerException ne){
                    
                }
            }
        }
        catch(MongoException mecx){
            LogClass.logMsg("UserDB", "checkExtensionExistInUsers", mecx.getMessage());
        }
        return user;
    }
    
    public void updatePeerStatusAndRegisteredAddressInUsersUsingExtension(String extension,String ipAddress,String peerStatus,
            MongoClient mongoConn){
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("users");
            Document selectQry = new Document();
            selectQry.append("userExtension", extension);
            selectQry.append("deleted", 0);
            Document updateCondition = new Document();
            updateCondition.append("registerStatus", peerStatus);
            updateCondition.append("registeredIpAddress", ipAddress);
            Document updateQry = new Document("$set",updateCondition);
            collection.updateOne(selectQry, updateQry);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

