/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridgecti.db;

import campaignconfig.model.ShowFieldModel;
import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import phonebridge.util.LogClass;
import java.util.ArrayList;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author bharath
 */
public class GetBasicContactInfo {
    public BasicDBList returnOrCondtionForContactSearch(String phoneNumber,ArrayList<String> fieldsToUse){
        BasicDBList orCond = new BasicDBList();
        try{
            for(String phoneFld : fieldsToUse){
                Document whereQry1 = new Document(phoneFld, phoneNumber);
                Document whereQry2 = new Document(phoneFld, "0".concat(phoneNumber));
                Document whereQry3 = new Document(phoneFld, "91".concat(phoneNumber));
                Document whereQry4 = new Document(phoneFld, "091".concat(phoneNumber));
                orCond.add(whereQry1);
                orCond.add(whereQry2);
                orCond.add(whereQry3);
                orCond.add(whereQry4);
            }
        }
        catch(Exception ex){
            LogClass.logMsg("GetBasicContactInfo", "returnOrCondtionForContactSearch", ex.getMessage());
        }
        return orCond;
    }
    
    public Document convertDBObjectToJavaObject(Document dc,String nameField,ArrayList<String> phoneFields,
        ArrayList<ShowFieldModel> showFields,String sortField,
        MongoClient mongoConn){
        Document insertDoc = new Document();
        BasicDBList showFieldsToInsert = new BasicDBList();
        ArrayList<Document> ticketHistory = new ArrayList<>();
        String custID = null;
        String clientCode = "";
        
        if(dc==null)
            return insertDoc;
        try{
            custID = dc.getObjectId("_id").toString();
            insertDoc.append("custID", new ObjectId(custID));
        }
        catch(Exception nexc){
            insertDoc.append("custID", custID);
        }
        
        try{
            insertDoc.append("name", dc.getString(nameField));
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("ListDAO", "convertDBObjectToJavaObject", nexc.getMessage());
        }
        
        try{
            int cnt = 1;
            for(String eachPhField : phoneFields){
                insertDoc.append("phone"+cnt, dc.getString(eachPhField));
                cnt+=1;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("ListDAO", "convertDBObjectToJavaObject", ex.getMessage());
        }
        
        try{
            String address = dc.getString("address");
            insertDoc.append("address", address);
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("ListDAO", "convertDBObjectToJavaObject", nexc.getMessage());
        }
        
        try{
            ArrayList callHistory = (ArrayList) dc.get("callHistory");
            if(callHistory!=null){
                BasicDBList prevInteractions = new CtiDB().getPreviousInteraction(callHistory,mongoConn);
                insertDoc.append("prevInteractions", prevInteractions);
            }
            System.out.println("CALL HISTORY IS "+callHistory);
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("ListDAO", "convertDBObjectToJavaObject", nexc.getMessage());
        }
        
        try{
            clientCode = dc.getString("clientCode");
            insertDoc.append("clientCode", clientCode);
        }
        catch(Exception ex){
            
        }
        for(ShowFieldModel eachShowField : showFields){
            Document showFieldDoc = new Document();
            showFieldDoc.append("fieldLabel", eachShowField.getFieldLabel());
            showFieldDoc.append("isUrl",eachShowField.isIsUrl());
            try{
                showFieldDoc.append("fieldValue",dc.get(eachShowField.getFieldLabel()).toString());
            }
            catch(NullPointerException nexc){
                showFieldDoc.append("fieldValue", null);
            }
            showFieldsToInsert.add(showFieldDoc);
        }
        
        insertDoc.append("showField", showFieldsToInsert);
        return insertDoc;
    }
}
