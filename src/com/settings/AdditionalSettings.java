/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settings;

import phonebridgelogger.model.Server;

/**
 *
 * @author bharath
 */
public class AdditionalSettings {
    private String docId;
    private String messageURL;
    private String mobileNumber;
    private String message;
    private String managerMobileNos;
    private String fileLocation;
    private String callBackURL;
    private String popupDataUpdateURL;
    private String singleSignOn;
    private Server server;
    private String afterCallSMSContent;
    private String popupURL;
    private String crmPopupURL;

    public String getCrmPopupURL() {
        return crmPopupURL;
    }

    public void setCrmPopupURL(String crmPopupURL) {
        this.crmPopupURL = crmPopupURL;
    }

    public String getMessageURL() {
        return messageURL;
    }

    public void setMessageURL(String messageURL) {
        this.messageURL = messageURL;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getManagerMobileNos() {
        return managerMobileNos;
    }

    public void setManagerMobileNos(String managerMobileNos) {
        this.managerMobileNos = managerMobileNos;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getCallBackURL() {
        return callBackURL;
    }

    public void setCallBackURL(String callBackURL) {
        this.callBackURL = callBackURL;
    }

    public String getSingleSignOn() {
        return singleSignOn;
    }

    public void setSingleSignOn(String singleSignOn) {
        this.singleSignOn = singleSignOn;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
    
    public AdditionalSettings(){
        
    }
    
    public AdditionalSettings(String docId,String messageURL,String mobileNumber,String message,String managerMobileNos,
        String fileLocation,String callBackURL,String singleSignOn,String popupDetailUpdateURL,
        String afterCallSMSContent,String popupURL,String crmPopupURL){
        this.docId = docId;
        this.messageURL = messageURL;
        this.mobileNumber = mobileNumber;
        this.message = message;
        this.managerMobileNos = managerMobileNos;
        this.fileLocation = fileLocation;
        this.callBackURL = callBackURL;
        this.singleSignOn = singleSignOn;
        this.popupDataUpdateURL = popupDetailUpdateURL;
        this.afterCallSMSContent = afterCallSMSContent;
        this.popupURL = popupURL;
        this.crmPopupURL = crmPopupURL;
    }

    public String getPopupDataUpdateURL() {
        return popupDataUpdateURL;
    }

    public void setPopupDataUpdateURL(String popupDataUpdateURL) {
        this.popupDataUpdateURL = popupDataUpdateURL;
    }

    public String getAfterCallSMSContent() {
        return afterCallSMSContent;
    }

    public void setAfterCallSMSContent(String afterCallSMSContent) {
        this.afterCallSMSContent = afterCallSMSContent;
    }

    public String getPopupURL() {
        return popupURL;
    }

    public void setPopupURL(String popupURL) {
        this.popupURL = popupURL;
    }
}
