/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridge.tickettask;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import org.bson.Document;
import org.bson.types.ObjectId;
import phonebridge.util.LogClass;
import singleton.db.DBClass;

/**
 *
 * @author bharath
 */
public class CategoryDb {
    public ArrayList<Category> getAllTicketType(boolean getAlongWithSubCategory){
        ArrayList<Category> allTicketType = new ArrayList<>();
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("ticket_task");
            Document whereQry = new Document("deleted",0);
            Document sortQry = new Document("dateCreatedOn", 1);
            MongoCursor cursor = collection.find(whereQry).sort(sortQry).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                Category category = this.convertDBObjecttoJavaObject(dc);
                ArrayList<SubCategory> allSubCategory = new ArrayList<>();
                if(getAlongWithSubCategory){
                    ArrayList<Document> allSubCategoryDoc = (ArrayList<Document>) dc.get("subCategory");
                    SubCategoryDb subCategoryDb = new SubCategoryDb();
                    for(Document eachSubCategory : allSubCategoryDoc){
                        allSubCategory.add(subCategoryDb.convertDBObjecttoJavaObject(eachSubCategory));
                    }
                    category.setSubCategories(allSubCategory);
                }
                allTicketType.add(category);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CampaignDB", "getAllCampaignDetails", mexc.getMessage());
        }
        return allTicketType;
    }
    
    public Category convertDBObjecttoJavaObject(Document dc){
        Category category = null;
        if(dc==null)
            return category;
        
        String categoryName = null;
        String categoryID = null;
      
        try{
            categoryName = dc.getString("category");
        }
        catch(Exception nexc){
            
        }
        try{
            categoryID = dc.getObjectId("_id").toString();
        }
        catch(Exception ex){
            
        }
        
        category = new Category(categoryID,categoryName,new ArrayList<SubCategory>());
        
        return category;
    }
    
    /*
        Get ticket type using ID
    */
    public Category getTicketTypeUsingID(String categoryID){
        Category category = null;
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("ticket_task");
            Document whereQry = new Document("_id",new ObjectId(categoryID));
            whereQry.append("deleted", 0);
            MongoCursor<Document> cursor = collection.find(whereQry).iterator();
            if(cursor.hasNext()){
                Document dc =  cursor.next();
                category = convertDBObjecttoJavaObject(dc);
                ArrayList<Document> subCategoryDoc = (ArrayList<Document>) dc.get("subCategory");
                ArrayList<SubCategory> allSubCategoryForCategory = new ArrayList<>();
                for(Document eachSubCategory : subCategoryDoc){
                    SubCategory subCategory = new com.phonebridge.tickettask.SubCategoryDb().convertDBObjecttoJavaObject(eachSubCategory);
                    allSubCategoryForCategory.add(subCategory);
                }
                category.setSubCategories(allSubCategoryForCategory);
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("CampaignDB", "getCampaignDetailsUsingID", mexc.getMessage());
        }
        return category;
    }
}
