/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridgecti.dao;

import campaignconfig.db.ShowFieldDB;
import campaignconfig.model.ShowFieldModel;
import phonebridge.util.LogClass;
import java.util.ArrayList;

/**
 *
 * @author bharath
 */
public class ShowFieldDAO {
    public ArrayList<ShowFieldModel> getShowFieldData(String campaignID){
        ArrayList<ShowFieldModel> showFields = new ArrayList<>();
        try{
            showFields = new ShowFieldDB().getallshowFieldDetails(campaignID);
        }
        catch(Exception ex){
            LogClass.logMsg("ShowFieldDAO", "getShowFieldData", ex.getMessage());
        }
        return showFields;
    }
}
