/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridgecti.dao;

import campaignconfig.db.CampaignDB;
import campaignconfig.model.CampaignModel;
import com.mongodb.MongoClient;
import phonebridge.util.LogClass;
import java.util.ArrayList;
import java.util.HashMap;
import org.bson.Document;
import org.bson.types.ObjectId;


public class CampaignDAO{
    public Document checkAndGetDetailsForNumber(String phoneNumber,String callDirection,CampaignModel campaignDetails,
            String extension,String queueNumber,String serverID,MongoClient mongoConn){
        Document insertDoc = new Document();
        try{
            switch(callDirection){
                case "outgoing":
                    insertDoc = new CampaignDB().checkAndGetDetailsForNumber(campaignDetails,phoneNumber,mongoConn);
                    break;
                case "incoming":
                    insertDoc = new CampaignDB().checkAndGetDetailsForNumber(campaignDetails,phoneNumber,mongoConn);
                    break;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CampaignDAO", "checkAndGetDetailsForNumber", ex.getMessage());
        }
        return insertDoc;
    }
    
    public void updateAvgTime(String campaignID,String fieldToUpdate,double avgTime,MongoClient mongoConn){
        try{
            new CampaignDB().updateAvgTime(campaignID,fieldToUpdate,avgTime,mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CampaignDAO", "updateAvgCallTime", ex.getMessage());
        }
    }
    
    public int getPopupWrapTimeFromCampaign(ObjectId campaignID,String callDirection,MongoClient mongoConn){
        int wrapUpTime = 0;
        try{
            wrapUpTime = new CampaignDB().getPopupWrapTimeFromCampaign(campaignID, callDirection, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CampaignDAO", "getPopupWrapTimeFromCampaign", ex.getMessage());
        }
        return wrapUpTime;
    }
    
    public HashMap getCampaignIDNameTypeOfCalandListIDlFromPopup(String popupID,String serverNamePrefix,String extension,MongoClient mongoConn){
        HashMap campaignInfo = null;
        try{
            campaignInfo = new CampaignDB().getCampaignIDNameTypeOfCallandListIDFromPopup(popupID, serverNamePrefix, extension, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CampaignDAO", "getCampaignIDNameTypeOfCallFromPopup", ex.getMessage());
        }
        return campaignInfo;      
    }
    
    public CampaignModel getDefaultCampaignDetails(String callDirection,MongoClient mongoConn){
        CampaignModel defaultCampaign = null;
        try{
            defaultCampaign = new CampaignDB().getDefaultCampaignDetails(callDirection, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CampaignDAO", "getDefaultCampaignDetails", ex.getMessage());
        }
        return defaultCampaign;
    }
    
    public CampaignModel getCampaignDetailsForID(String campaignID){
        CampaignModel campaignDetails = null;
        try{
            campaignDetails = new CampaignDB().getCampaignDetailsUsingID(campaignID);
        }
        catch(Exception ex){
            LogClass.logMsg("CampaignDAO", "getCampaignDetailsForID", ex.getMessage());
        }
        return campaignDetails;
    }
    
    public ArrayList<CampaignModel> getAllCampaign(boolean toRetrieveDefault){
        ArrayList<CampaignModel> allCampaign = new ArrayList<>();
        try{
            allCampaign = new CampaignDB().getAllCampaignDetails(toRetrieveDefault);
        }
        catch(Exception ex){
            LogClass.logMsg("CampaignDAO", "getCampaignDetailsForID", ex.getMessage());
        }
        return allCampaign;
    }
    
    public void insertDefaultCampaign(ObjectId serverID,MongoClient mongoConn){
        try{
            new CampaignDB().insertDefaultOutgoingCampaign(serverID,mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CampaignDAO", "insertDefaultCampaign", ex.getMessage());
        }
    }
    
    public ArrayList<String> getServerNamePrefixForCampaignID(String campaignID,MongoClient mongoConn){
        ArrayList<String> serverNamePrefixForCampaign = new ArrayList<>();
        try{
            serverNamePrefixForCampaign = new CampaignDB().getServerNamePrefixForCampaignID(campaignID, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CampaignDAO", "getServerNamePrefixForCampaignID", ex.getMessage());
        }
        return serverNamePrefixForCampaign;
    }
    
    public CampaignModel getDefaultModuleLinkedCampaign(MongoClient mongoConn,String callDirection){
        return new CampaignDB().getDefaultModuleLinkedCampaign(mongoConn,callDirection);
    }
    
    public CampaignModel getAllPopupInCRMCampaigns(MongoClient mongoConn,String callDirection){
        return new CampaignDB().getAllPopupInCRMCampaigns(mongoConn,callDirection);
    }
    
    public CampaignModel getAllAPICampaigns(MongoClient mongoConn,String callDirection){
        return new CampaignDB().getAllAPICampaigns(mongoConn,callDirection);
    }
    
    /*public Document checkAndGetDetailsForNumberFromDefaultModuleLinkedCampaign(String phoneNumber,String extension,
        String serverID,MongoClient mongoConn){
        Document insertDoc = null;
        try{
            ArrayList<CampaignModel> defaultModuleLinkedCampaign = new CampaignDAO().getDefaultModuleLinkedCampaign(mongoConn,"outgoing");
            for(CampaignModel eachCampaign : defaultModuleLinkedCampaign){
                insertDoc = this.checkAndGetDetailsForNumber(phoneNumber, "outgoing", eachCampaign, extension, null, serverID, mongoConn);
                if(insertDoc!=null)
                    break;
            }
        }
        catch(Exception ex){
            
        }
        return insertDoc;
    }*/
    
    /*
        1) Used to get all missedCall(dialMethod) campaign from DB
    */
    public ArrayList getAllMissedCallCampaigns(MongoClient mongoConn){
        return new CampaignDB().getAllMissedCallCampaigns(mongoConn);
    }
}
