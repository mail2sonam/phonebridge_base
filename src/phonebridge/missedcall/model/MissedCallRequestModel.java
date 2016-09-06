/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridge.missedcall.model;

import java.util.Date;

/**
 *
 * @author sonamuthu
 */
public class MissedCallRequestModel {
    private String missedCallReqID;
    private String phoneNumber;
    private String lastExtensionTried;
    private String trunk;
    private String branchID;
    private Date dateEntered;

    /**
     * @return the missedCallReqID
     */
    public String getMissedCallReqID() {
        return missedCallReqID;
    }

    public String getBranchID() {
        return branchID;
    }

    public void setBranchID(String branchID) {
        this.branchID = branchID;
    }

    /**
     * @param missedCallReqID the missedCallReqID to set
     */
    public void setMissedCallReqID(String missedCallReqID) {
        this.missedCallReqID = missedCallReqID;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the lastExtensionTried
     */
    public String getLastExtensionTried() {
        return lastExtensionTried;
    }

    /**
     * @param lastExtensionTried the lastExtensionTried to set
     */
    public void setLastExtensionTried(String lastExtensionTried) {
        this.lastExtensionTried = lastExtensionTried;
    }

    /**
     * @return the trunk
     */
    public String getTrunk() {
        return trunk;
    }

    /**
     * @param trunk the trunk to set
     */
    public void setTrunk(String trunk) {
        this.trunk = trunk;
    }

    public Date getDateEntered() {
        return dateEntered;
    }

    public void setDateEntered(Date dateEntered) {
        this.dateEntered = dateEntered;
    }
}
