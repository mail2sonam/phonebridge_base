/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campaignconfig.model;

import java.util.Date;

/**
 *
 * @author bharath
 */
public class DisplayCallModel {
    private String name;
    private String phone1;
    private String phone2;
    private String phone3;
    private String listID;
    private String campaignName;
    private String campaignID;
    private Date lastCalledOn;
    private Date followUpOn;
    private String lastDisposition;
    private String lastComments;
    private String custID;
    private String callID;
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the phone1
     */
    public String getPhone1() {
        return phone1;
    }

    /**
     * @param phone1 the phone1 to set
     */
    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    /**
     * @return the phone2
     */
    public String getPhone2() {
        return phone2;
    }

    /**
     * @param phone2 the phone2 to set
     */
    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    /**
     * @return the phone3
     */
    public String getPhone3() {
        return phone3;
    }

    /**
     * @param phone3 the phone3 to set
     */
    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }

    /**
     * @return the lastCalledOn
     */
    public Date getLastCalledOn() {
        return lastCalledOn;
    }

    /**
     * @param lastCalledOn the lastCalledOn to set
     */
    public void setLastCalledOn(Date lastCalledOn) {
        this.lastCalledOn = lastCalledOn;
    }

    /**
     * @return the followUpOn
     */
    public Date getFollowUpOn() {
        return followUpOn;
    }

    /**
     * @param followUpOn the followUpOn to set
     */
    public void setFollowUpOn(Date followUpOn) {
        this.followUpOn = followUpOn;
    }

    /**
     * @return the lastDisposition
     */
    public String getLastDisposition() {
        return lastDisposition;
    }

    /**
     * @param lastDisposition the lastDisposition to set
     */
    public void setLastDisposition(String lastDisposition) {
        this.lastDisposition = lastDisposition;
    }

    /**
     * @return the lastComments
     */
    public String getLastComments() {
        return lastComments;
    }

    /**
     * @param lastComments the lastComments to set
     */
    public void setLastComments(String lastComments) {
        this.lastComments = lastComments;
    }

    /**
     * @return the custID
     */
    public String getCustID() {
        return custID;
    }

    /**
     * @param custID the custID to set
     */
    public void setCustID(String custID) {
        this.custID = custID;
    }

    /**
     * @return the campaignName
     */
    public String getCampaignName() {
        return campaignName;
    }

    /**
     * @param campaignName the campaignName to set
     */
    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    /**
     * @return the campaignID
     */
    public String getCampaignID() {
        return campaignID;
    }

    /**
     * @param campaignID the campaignID to set
     */
    public void setCampaignID(String campaignID) {
        this.campaignID = campaignID;
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public String getCallID() {
        return callID;
    }

    public void setCallID(String callID) {
        this.callID = callID;
    }
}
