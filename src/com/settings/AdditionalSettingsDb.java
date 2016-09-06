/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settings;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import singleton.db.DBClass;

/**
 *
 * @author bharath
 */
public class AdditionalSettingsDb {
    public AdditionalSettings getCurrentSetting(){
        AdditionalSettings settings = new AdditionalSettings();
        try{   
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("settings");
            MongoCursor<Document> cursor = collection.find().iterator();
            if(cursor.hasNext()){
                Document dc = cursor.next();
                settings = this.convertDBObjToJavaObj(dc);
            }
        }
        catch(MongoException ex){
            System.out.println("ERROR IN SETTINGS "+ex.getMessage());
        }
        return settings;
    }
    
    public AdditionalSettings convertDBObjToJavaObj(Document dc){
        AdditionalSettings settings = new AdditionalSettings();
        if(dc.isEmpty())
            return settings;
        
        String messageURL = null;
        String mobileNumber = null;
        String message = null;
        String managerMobileNos = null;
        String fileLocation = null;
        String callBackURL = null;
        String singleSignOn = null;
        String popupDataUpdateURL = null;
        String afterCallSMSContent = null;
        String popupURL = null;
        String crmPopupURL = null;
        
        try{
            messageURL = dc.getString("messageURL");
        }
        catch(Exception ex){
            System.out.println("SETTINGS CDBO "+ex.getMessage());
        }
        try{
            mobileNumber = dc.getString("mobileNumber");
        }
        catch(Exception ex){
            System.out.println("SETTINGS CDBO "+ex.getMessage());
        }
        try{
            message = dc.getString("message");
        }
        catch(Exception ex){
            System.out.println("SETTINGS CDBO "+ex.getMessage());
        }
        try{
            managerMobileNos = dc.getString("managerMobileNos");
        }
        catch(Exception ex){
            System.out.println("SETTINGS CDBO "+ex.getMessage());
        }
        try{
            fileLocation = dc.getString("fileLocation");
        }
        catch(Exception ex){
            System.out.println("SETTINGS CDBO "+ex.getMessage());
        }
        try{
            callBackURL = dc.getString("callBackURL");
        }
        catch(Exception ex){
            System.out.println("SETTINGS CDBO "+ex.getMessage());
        }
        try{
            popupDataUpdateURL = dc.getString("popupDataUpdateURL");
        }
        catch(Exception ex){
            System.out.println("SETTINGS CDBO "+ex.getMessage());
        }
        try{
            singleSignOn = dc.getString("singleSignOn");
        }
        catch(Exception ex){
            System.out.println("SETTINGS CDBO "+ex.getMessage());
        }
        try{
            afterCallSMSContent = dc.getString("afterCallSMSContent");
        }
        catch(Exception ex){
            System.out.println("SETTINGS CDBO "+ex.getMessage());
        }
        try{
            popupURL = dc.getString("popupURL");
        }
        catch(Exception ex){
            System.out.println("SETTINGS CDBO "+ex.getMessage());
        }
        try{
            crmPopupURL = dc.getString("crmPopupURL");
        }
        catch(Exception ex){
            System.out.println("SETTINGS CDBO "+ex.getMessage());
        }
        
        settings = new AdditionalSettings(dc.getObjectId("_id").toString(), messageURL, mobileNumber, message, managerMobileNos, fileLocation, 
            callBackURL, singleSignOn,popupDataUpdateURL,afterCallSMSContent,popupURL,crmPopupURL);
        return settings;
    }
}
