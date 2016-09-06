/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridge.originate;

/**
 *
 * @author sonamuthu
 */
public class OriginateRequestModel {
    private String originateReqID;
    private String prefix;
    private String phoneNumber;
    private String userExtension;
    private String typeOfDialer;
    private String callContext;
    private String campaignID;
    private boolean followMeExists;
    private String extensionType;
    private String followMeNumber;
    /**
     * @return the originateReqID
     */
    public String getOriginateReqID() {
        return originateReqID;
    }

    /**
     * @param originateReqID the originateReqID to set
     */
    public void setOriginateReqID(String originateReqID) {
        this.originateReqID = originateReqID;
    }

    public String getExtensionType() {
        return extensionType;
    }

    public void setExtensionType(String extensionType) {
        this.extensionType = extensionType;
    }

    public String getFollowMeNumber() {
        return followMeNumber;
    }

    public void setFollowMeNumber(String followMeNumber) {
        this.followMeNumber = followMeNumber;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
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
     * @return the userExtension
     */
    public String getUserExtension() {
        return userExtension;
    }

    /**
     * @param userExtension the userExtension to set
     */
    public void setUserExtension(String userExtension) {
        this.userExtension = userExtension;
    }

    /**
     * @return the typeOfDialer
     */
    public String getTypeOfDialer() {
        return typeOfDialer;
    }

    /**
     * @param typeOfDialer the typeOfDialer to set
     */
    public void setTypeOfDialer(String typeOfDialer) {
        this.typeOfDialer = typeOfDialer;
    }

    /**
     * @return the callContext
     */
    public String getCallContext() {
        return callContext;
    }

    /**
     * @param callContext the callContext to set
     */
    public void setCallContext(String callContext) {
        this.callContext = callContext;
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

    /**
     * @return the followMeExists
     */
    public boolean isFollowMeExists() {
        return followMeExists;
    }

    /**
     * @param followMeExists the followMeExists to set
     */
    public void setFollowMeExists(boolean followMeExists) {
        this.followMeExists = followMeExists;
    }
}
