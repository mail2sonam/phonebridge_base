/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridgecti.dao;

import campaignconfig.db.DispositionDB;
import campaignconfig.model.DispositionModel;
import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import java.util.ArrayList;
import phonebridge.util.LogClass;

/**
 *
 * @author bharath
 */
public class DispositionDAO {
    public BasicDBList getDispositionAsBasicDBList(String campaignID,MongoClient mongoConn){
        BasicDBList dispositions = null;
        try{
            dispositions = new DispositionDB().getDispositionAsBasicDBList(campaignID,mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("DispositionDAO", "getDispositionData", ex.getMessage());
        }
        return dispositions;
    }
    
    public ArrayList<DispositionModel> getAllDispositionWithDependentForCampaign(String campaignID){
        ArrayList<DispositionModel> allDispositionWithDependent = new ArrayList<>();
        try{
            allDispositionWithDependent = new DispositionDB().getAllDispositionWithDependentForCampaign(campaignID);
        }
        catch(Exception ex){
            LogClass.logMsg("DispositionDAO", "getAllDispositionWithDependentForCampaign", ex.getMessage());
        }
        return allDispositionWithDependent;
    }
    
    public ArrayList<String> getAllDispositionName(String campaignID,MongoClient mongoConn){
        ArrayList<String> allDispositionName = new ArrayList<>();
        try{
            allDispositionName = new DispositionDB().getAllDispositionName(campaignID, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("DispositionDAO", "getAllDispositionName", ex.getMessage());
        }
        return allDispositionName;
    }
}
