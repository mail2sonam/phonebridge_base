/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campaignconfig.model;

/**
 *
 * @author venky
 */
public class BranchModel {
    private String branchID;
    private String branchCode;
    private String branchName;

    public String getBranchID() {
        return branchID;
    }

    public void setBranchID(String branchID) {
        this.branchID = branchID;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
    
    public BranchModel(String branchID,String branchCode,String branchName){
        this.branchID = branchID;
        this.branchCode = branchCode;
        this.branchName = branchName;
    }
    
    public BranchModel(){
        
    }
}
