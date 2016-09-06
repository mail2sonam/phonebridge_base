/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridgecti.dao;

import campaignconfig.db.UserDB;
import campaignconfig.model.UserModel;
import com.mongodb.MongoClient;
import phonebridge.util.LogClass;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author bharath
 */
public class UserDAO {
    public String checkIfPopUpObjectAlreadyExists(String extension,String phoneNumber,String callDirection,
            String serverNamePrefix,MongoClient mongoConn){
        String popupDocID = null;
        try{
            popupDocID = new UserDB().checkIfPopUpObjectAlreadyExists(extension,phoneNumber,callDirection,serverNamePrefix,mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("UserDAO", "checkIfPopUpObjectAlreadyExists", ex.getMessage());
        }
        return popupDocID;
    }
    
    public UserModel getUserNameAndIDForExtensionAndServerID(String extension,String serverID,MongoClient mongoConn){
        UserModel userDetails = new UserModel();
        try{
            userDetails = new UserDB().getUserNameAndIDForExtensionAndServerID(extension, serverID, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("UserDAO", "getUserNameAndIDForExtensionAndServerID", ex.getMessage());
        }
        return userDetails;
    }
    
    public UserModel checkExtensionExistInUsers(String extensionOrFollowMeNo,MongoClient mongoConn){
        return new UserDB().checkIfUserExistsWithExtensionOrFollowNo(extensionOrFollowMeNo, mongoConn);
    }
    
    public void updatePeerStatusAndRegisteredAddressInUsersUsingExtension(String extension, String ipAddress, String peerStatus,
            MongoClient mongoConn){
        new UserDB().updatePeerStatusAndRegisteredAddressInUsersUsingExtension(extension, ipAddress, peerStatus, mongoConn);
    }
}
