/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridgecti.dao;

import campaignconfig.model.BranchModel;
import com.phonebridgecti.db.BranchDb;
import java.util.ArrayList;

/**
 *
 * @author venky
 */
public class BranchDAO {

    public ArrayList<BranchModel> getAllBranches() {
        return new BranchDb().getAllBranches();
    }
    
    public String getBranchIDByBranchCode(String branchCode){
        return new BranchDb().getBranchIDByBranchCode(branchCode);
    }
}