/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridge.tickettask;

import java.util.ArrayList;
import org.bson.Document;

/**
 *
 * @author bharath
 */
public class SubCategoryDb {
    public SubCategory convertDBObjecttoJavaObject(Document dc){
        SubCategory subCategory  = null;
        if(dc==null)
            return subCategory;
        
        String subCategoryName = null;
        String subCategoryID = null;
      
        try{
            subCategoryName = dc.getString("subCategory");
        }
        catch(Exception nexc){
            
        }
        try{
            subCategoryID = dc.getObjectId("id").toString();
        }
        catch(Exception ex){
            
        }
        subCategory = new SubCategory(subCategoryID,subCategoryName,new ArrayList<EscalationLevel>());
        
        return subCategory;
    }
}
