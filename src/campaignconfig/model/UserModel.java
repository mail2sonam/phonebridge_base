/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campaignconfig.model;

/**
 *
 * @author harini
 */
public class UserModel {
    
    public UserModel(){
        userSelected = false;
    }
    
    private String name;
    private String userName;
    private String extension;
    private String serverID;
    private String userID;
    private String serverNamePrefix;
    private String password;
    private String confirmPassword;
    private String serverName;
    private String prefix;
    private boolean userSelected;
    private String userType;
    private String followNumber;
    private String address;
    private String userMessage;
    private String email;
    private String phoneNumber;
    private boolean isFollowMe;
    private String departmentID;
    private String designationID;
    private String departmentName;
    private String designationName;
    private String branchID;
    private String branchName;
    private String branchCode;
    private String extensionStatus;
    private String context;
    private String callStatus;
    private String popupStatus;
    private String onBreak;
    private String userPrefix;
    private String extensionType;
    private String queueName;
    private boolean incomingSMSNotification;

    public String getExtensionType() {
        return extensionType;
    }

    public void setExtensionType(String extensionType) {
        this.extensionType = extensionType;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean isIncomingSMSNotification() {
        return incomingSMSNotification;
    }

    public void setIncomingSMSNotification(boolean incomingSMSNotification) {
        this.incomingSMSNotification = incomingSMSNotification;
    }

    public String getUserPrefix() {
        return userPrefix;
    }

    public void setUserPrefix(String userPrefix) {
        this.userPrefix = userPrefix;
    }
    
    public boolean isUserSelected() {
        return userSelected;
    }

    public void setUserSelected(boolean userSelected) {
        this.userSelected = userSelected;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
     public String getServerNamePrefix() {
        return serverNamePrefix;
    }

    public void setServerNamePrefix(String serverNamePrefix) {
        this.serverNamePrefix = serverNamePrefix;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
    
    public UserModel(String userID,String name,String userName,String Password,String extension,String serverID,
        String serverNamePrefix,String serverName,String prefix,String followNumber,String address,String userMessage,
        String email,String phoneNumber,boolean isFollowMe,String departmentID,String designationID,
        String departmentName,String designationName,String branchID,String branchName,String branchCode,
        String extensionStatus,String context,String callStatus,String popupStatus,String onBreak,
        String queueName,String extensionType){
        this.userID = userID;
        this.name = name;
        this.userName = userName;
        this.extension = extension;
        this.serverID = serverID;
        this.serverNamePrefix = serverNamePrefix;
        this.password = Password;
        this.prefix = prefix;
        this.followNumber = followNumber;
        this.serverName = serverName;
        this.address = address;
        this.userMessage = userMessage;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isFollowMe = isFollowMe;
        this.departmentID = departmentID;
        this.designationID = designationID;
        this.departmentName = departmentName;
        this.designationName = designationName;
        this.branchID = branchID;
        this.branchName = branchName;
        this.branchCode = branchCode;
        this.extensionStatus = extensionStatus;
        this.context = context;
        this.callStatus = callStatus;
        this.popupStatus = popupStatus;
        this.onBreak = onBreak;
        this.queueName = queueName;
        this.extensionType = extensionType;
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
     * @return the userType
     */
    public String getUserType() {
        return userType;
    }

    /**
     * @param userType the userType to set
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFollowNumber() {
        return followNumber;
    }

    public void setFollowNumber(String followNumber) {
        this.followNumber = followNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the isFollowMe
     */
    public boolean isIsFollowMe() {
        return isFollowMe;
    }

    /**
     * @param isFollowMe the isFollowMe to set
     */
    public void setIsFollowMe(boolean isFollowMe) {
        this.isFollowMe = isFollowMe;
    }

    /**
     * @return the departmentID
     */
    public String getDepartmentID() {
        return departmentID;
    }

    /**
     * @param departmentID the departmentID to set
     */
    public void setDepartmentID(String departmentID) {
        this.departmentID = departmentID;
    }

    /**
     * @return the designationID
     */
    public String getDesignationID() {
        return designationID;
    }

    /**
     * @param designationID the designationID to set
     */
    public void setDesignationID(String designationID) {
        this.designationID = designationID;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDesignationName() {
        return designationName;
    }

    public void setDesignationName(String designationName) {
        this.designationName = designationName;
    }

    /**
     * @return the branchID
     */
    public String getBranchID() {
        return branchID;
    }

    /**
     * @param branchID the branchID to set
     */
    public void setBranchID(String branchID) {
        this.branchID = branchID;
    }

    /**
     * @return the branchName
     */
    public String getBranchName() {
        return branchName;
    }

    /**
     * @param branchName the branchName to set
     */
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getExtensionStatus() {
        return extensionStatus;
    }

    public void setExtensionStatus(String extensionStatus) {
        this.extensionStatus = extensionStatus;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    public String getPopupStatus() {
        return popupStatus;
    }

    public void setPopupStatus(String popupStatus) {
        this.popupStatus = popupStatus;
    }

    public String getOnBreak() {
        return onBreak;
    }

    public void setOnBreak(String onBreak) {
        this.onBreak = onBreak;
    }
}
