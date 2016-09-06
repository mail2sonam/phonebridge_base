/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridge.tickettask;

/**
 *
 * @author bharath
 */
public class AssociateResponsible {
    private String associateID;
    private String associateUserName;
    private String associateUserID;

    public String getAssociateUserName() {
        return associateUserName;
    }

    public void setAssociateUserName(String associateUserName) {
        this.associateUserName = associateUserName;
    }

    public String getAssociateUserID() {
        return associateUserID;
    }

    public void setAssociateUserID(String associateUserID) {
        this.associateUserID = associateUserID;
    }
    
    public AssociateResponsible(){
        
    }
    
    public AssociateResponsible(String associateID,String associateUserName,String associateUserID){
        this.associateID = associateID;
        this.associateUserName = associateUserName;
        this.associateUserID = associateUserID;
    }

    public String getAssociateID() {
        return associateID;
    }

    public void setAssociateID(String associateID) {
        this.associateID = associateID;
    }
}
