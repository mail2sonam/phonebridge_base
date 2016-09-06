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
public class Category {
    private String categoryID;
    private String categoryName;
    private ArrayList<SubCategory> subCategories;

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ArrayList<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }
    
    public Category(){
        
    }
    
    public Category(String categoryID,String categoryName,ArrayList<SubCategory> subCategories){
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.subCategories = subCategories;
    }
}
