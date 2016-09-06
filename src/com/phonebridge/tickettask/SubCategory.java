/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridge.tickettask;

import java.util.ArrayList;

/**
 *
 * @author bharath
 */
public class SubCategory {
    private String subCategoryID;
    private String subCategoryName;
    private ArrayList<EscalationLevel> escalationLevel;

    public String getSubCategoryID() {
        return subCategoryID;
    }

    public void setSubCategoryID(String subCategoryID) {
        this.subCategoryID = subCategoryID;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public ArrayList<EscalationLevel> getEscalationLevel() {
        return escalationLevel;
    }

    public void setEscaltionLevels(ArrayList<EscalationLevel> escalationLevel) {
        this.escalationLevel = escalationLevel;
    }
    
    public SubCategory(){
        
    }
    
    public SubCategory(String subCategoryID,String subCategoryName,ArrayList<EscalationLevel> escalationLevel){
        this.subCategoryID = subCategoryID;
        this.subCategoryName = subCategoryName;
        this.escalationLevel = escalationLevel;
    }
}
