/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridgecti.aggregate.db;

import com.mongodb.MongoClient;
import phonebridge.util.LogClass;

/**
 *
 * @author bharath
 */
public class ProcessCampaignPerformanceDB {
    public void insertTalkTimeDoc(String campaignID,MongoClient mongoConn){
        try{
            
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessCampaignPerformance", "insertTalkTimeDoc", ex.getMessage());
        }
    }
    
    public void insertWrapTimeDoc(String campaignID,MongoClient mongoConn){
        try{
            
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessCampaignPerformance", "insertWrapTimeDoc", ex.getMessage());
        }
    }
    
    public void insertCallConnectingTimeDoc(String campaignID,MongoClient mongoConn){
        try{
            
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessCampaignPerformance", "insertCallConnectingTimeDoc", ex.getMessage());
        }
    }
    
    public boolean checkForTalkTimeDoc(String campaignID,MongoClient mongoConn){
        boolean exists = false;
        try{
            
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessCampaignPerformance", "checkForTalkTimeDoc", ex.getMessage());
        }
        return exists;
    }
    
    public boolean checkForWrapTimeDoc(String campaignID,MongoClient mongoConn){
        boolean exists = false;
        try{
            
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessCampaignPerformance", "checkForWrapTimeDoc", ex.getMessage());
        }
        return exists;
    }
    
    public boolean checkForCallConnectingTimeDoc(String campaignID,MongoClient mongoConn){
        boolean exists = false;
        try{
            
        }
        catch(Exception ex){
            LogClass.logMsg("ProcessCampaignPerformance", "checkForCallConnectingTimeDoc", ex.getMessage());
        }
        return exists;
    }
}
