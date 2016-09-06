/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridge.callmodel;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author bharath
 */
public class CallModel {
    private String custId;
    private String callId;
    private String phoneNumber;
    private String primaryExtension;
    private String allExtensions;
    private String recordings;
    private Date callStartTime;
    private Date callAnswerTime;
    private Date callEndTime;
    private Date wrapUpTime;
    private double duration;
    private double callRingingDuration;//Time between startTime and answerTime
    private double wrapUpTimeSecs;
    private String callDirection;
    private String callStatus;
    private String serverId;
    private String disposition;
    private String dependant;
    private String comments;
    private String transferDetails;
    private String uniqueId;
    private Date ivrEntryTime;
    private String queueNumber;
    private Date queueJoinTime;
    private double queueWaitTime;//Time between queueJoinTime and answerTime
    private String campaignName;
    private String hangUpReason;
    private String callType;
    private String dialMethod;
    private String hoppingDetails;
    private boolean BCCBCall;

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public CallModel(){
        
    }

    public String getRecordings() {
        return recordings;
    }

    public void setRecordings(String recordings) {
        this.recordings = recordings;
    }

    public Date getCallStartTime() {
        return callStartTime;
    }

    public void setCallStartTime(Date callStartTime) {
        this.callStartTime = callStartTime;
    }

    public Date getCallEndTime() {
        return callEndTime;
    }

    public void setCallEndTime(Date callEndTime) {
        this.callEndTime = callEndTime;
    }

    public Date getCallAnswerTime() {
        return callAnswerTime;
    }

    public void setCallAnswerTime(Date callAnswerTime) {
        this.callAnswerTime = callAnswerTime;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getCallDirection() {
        return callDirection;
    }

    public void setCallDirection(String callDirection) {
        this.callDirection = callDirection;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public String getDependant() {
        return dependant;
    }

    public void setDependant(String dependant) {
        this.dependant = dependant;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Date getIvrEntryTime() {
        return ivrEntryTime;
    }

    public void setIvrEntryTime(Date ivrEntryTime) {
        this.ivrEntryTime = ivrEntryTime;
    }

    public Date getQueueJoinTime() {
        return queueJoinTime;
    }

    public void setQueueJoinTime(Date queueJoinTime) {
        this.queueJoinTime = queueJoinTime;
    }

    public String getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(String queueNumber) {
        this.queueNumber = queueNumber;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public double getCallRingingDuration() {
        return callRingingDuration;
    }

    public void setCallRingingDuration(double callRingingDuration) {
        this.callRingingDuration = callRingingDuration;
    }

    public double getQueueWaitTime() {
        return queueWaitTime;
    }

    public void setQueueWaitTime(double queueWaitTime) {
        this.queueWaitTime = queueWaitTime;
    }

    public String getHangUpReason() {
        return hangUpReason;
    }

    public void setHangUpReason(String hangUpReason) {
        this.hangUpReason = hangUpReason;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getDialMethod() {
        return dialMethod;
    }

    public void setDialMethod(String dialMethod) {
        this.dialMethod = dialMethod;
    }

    public Date getWrapUpTime() {
        return wrapUpTime;
    }

    public void setWrapUpTime(Date wrapUpTime) {
        this.wrapUpTime = wrapUpTime;
    }

    public double getWrapUpTimeSecs() {
        return wrapUpTimeSecs;
    }

    public void setWrapUpTimeSecs(double wrapUpTimeSecs) {
        this.wrapUpTimeSecs = wrapUpTimeSecs;
    }
    
    public CallModel(String custId,String callId,String phoneNumber,String primaryExtension,String allExtensions,
        String recordings,Date callStartTime,Date callAnswerTime,Date callEndTime,Date wrapUpTime,double duration,
        double callRingingDuration,double wrapUpTimeSecs,String callDirection,String callStatus,String serverId,
        String disposition,String dependant,String comments,String transferDetails,
        String uniqueId,Date ivrEntryTime,String queueNumber,Date queueJoinTime,double queueWaitTime,
        String campaignName,String hangUpReason,String callType,String dialMethod,String hoppingDetails,
        boolean BCCBCall){
            this.custId = custId;
            this.callId = callId;
            this.phoneNumber = phoneNumber;
            this.primaryExtension = primaryExtension;
            this.allExtensions = allExtensions;
            this.recordings = recordings;
            this.callStartTime = callStartTime;
            this.callAnswerTime = callAnswerTime;
            this.callEndTime = callEndTime;
            this.wrapUpTime = wrapUpTime;
            this.duration = duration;
            this.callRingingDuration = callRingingDuration;
            this.wrapUpTimeSecs = wrapUpTimeSecs;
            this.callDirection = callDirection;
            this.callStatus = callStatus;
            this.serverId = serverId;
            this.disposition = disposition;
            this.dependant = dependant;
            this.comments = comments;
            this.transferDetails = transferDetails;
            this.uniqueId = uniqueId;
            this.ivrEntryTime = ivrEntryTime;
            this.queueNumber = queueNumber;
            this.queueJoinTime = queueJoinTime;
            this.queueWaitTime = queueWaitTime;
            this.campaignName = campaignName;
            this.hangUpReason = hangUpReason;
            this.callType = callType;
            this.dialMethod = dialMethod;
            this.hoppingDetails = hoppingDetails;
            this.BCCBCall = BCCBCall;
    }

    public String getPrimaryExtension() {
        return primaryExtension;
    }

    public void setPrimaryExtension(String primaryExtension) {
        this.primaryExtension = primaryExtension;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getAllExtensions() {
        return allExtensions;
    }

    public void setAllExtensions(String allExtensions) {
        this.allExtensions = allExtensions;
    }

    public String getTransferDetails() {
        return transferDetails;
    }

    public void setTransferDetails(String transferDetails) {
        this.transferDetails = transferDetails;
    }

    public String getHoppingDetails() {
        return hoppingDetails;
    }

    public void setHoppingDetails(String hoppingDetails) {
        this.hoppingDetails = hoppingDetails;
    }

    public boolean isBCCBCall() {
        return BCCBCall;
    }

    public void setBCCBCall(boolean BCCBCall) {
        this.BCCBCall = BCCBCall;
    }
}
