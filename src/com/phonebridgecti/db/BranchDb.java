/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridgecti.db;

import campaignconfig.model.BranchModel;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import org.bson.Document;
import singleton.db.DBClass;

/**
 *
 * @author bharath
 */
public class BranchDb {
    public ArrayList<BranchModel> getAllBranches() {
        ArrayList<BranchModel> branchDetails = new ArrayList<>();
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("branch");
            Document whereQuery = new Document("deleted",0);
            MongoCursor cursor = collection.find(whereQuery).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                BranchModel bm = this.convertDBObjecttoJavaObject(dc);
                branchDetails.add(bm);
            }
        }
        catch(MongoException mecx){
            mecx.getMessage();
        }
        return branchDetails;
    }
    
    public BranchModel convertDBObjecttoJavaObject(Document dc){
        BranchModel branchDetails = null;
        if(dc==null)
            return branchDetails;
        String branchID = null;
        String branchCode = null;
        String branchName = null;
        
        try{
            branchID = dc.getObjectId("_id").toString();
        }
        catch(Exception ex){
            System.out.println("BRANCH CDBO "+ex.getMessage());
        }
        try{
            branchCode = dc.getString("branchCode");
        }
        catch(Exception ex){
            System.out.println("BRANCH CDBO "+ex.getMessage());
        }
        try{
            branchName = dc.getString("branchName");
        }
        catch(Exception ex){
            System.out.println("BRANCH CDBO "+ex.getMessage());
        }
        
        branchDetails = new BranchModel(branchID, branchCode, branchName);
        return branchDetails;
    }
    
    public String getBranchIDByBranchCode(String branchCode){
        String branchID = null;
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("branch");
            Document whereQuery = new Document("branchCode",branchCode);
            whereQuery.append("deleted", 0);
            Document selectQuery = new Document("_id",1);
            MongoCursor cursor = collection.find(whereQuery).projection(selectQuery).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                branchID = dc.getObjectId("_id").toString();
            }
        }
        catch(MongoException mecx){
            System.out.println("ERR IN getBranchIDByBranchCode "+mecx.getMessage());
        }
        return branchID;
    }
}
