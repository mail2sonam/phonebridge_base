/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.model;

import java.io.Serializable;
import org.bson.Document;
import java.util.Date;

/**
 *
 * @author sonamuthu
 */
public class CoreShowChannelModel implements Serializable{
    private String eventName;
    private String channel;
    private String uniqueID;
    private String context;
    private String extension;
    private String priority;
    private String channelState;
    private String channelStateDesc;
    private String application;
    private String applicationData;
    private String callerIDNum;
    private String callerIDName;
    private String connectedLineNum;
    private String connectedLineName;
    private String duration;
    private String accountCode;
    private String bridgedChannel;
    private String bridgedUniqueID;
    private Date eventTime;

    /**
     * @return the eventName
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * @param eventName the eventName to set
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * @return the channel
     */
    public String getChannel() {
        return channel;
    }

    /**
     * @param channel the channel to set
     */
    public void setChannel(String channel) {
        this.channel = channel;
    }

    /**
     * @return the uniqueID
     */
    public String getUniqueID() {
        return uniqueID;
    }

    /**
     * @param uniqueID the uniqueID to set
     */
    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    /**
     * @return the context
     */
    public String getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(String context) {
        this.context = context;
    }

    /**
     * @return the extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @param extension the extension to set
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * @return the priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * @return the channelState
     */
    public String getChannelState() {
        return channelState;
    }

    /**
     * @param channelState the channelState to set
     */
    public void setChannelState(String channelState) {
        this.channelState = channelState;
    }

    /**
     * @return the channelStateDesc
     */
    public String getChannelStateDesc() {
        return channelStateDesc;
    }

    /**
     * @param channelStateDesc the channelStateDesc to set
     */
    public void setChannelStateDesc(String channelStateDesc) {
        this.channelStateDesc = channelStateDesc;
    }

    /**
     * @return the application
     */
    public String getApplication() {
        return application;
    }

    /**
     * @param application the application to set
     */
    public void setApplication(String application) {
        this.application = application;
    }

    /**
     * @return the applicationData
     */
    public String getApplicationData() {
        return applicationData;
    }

    /**
     * @param applicationData the applicationData to set
     */
    public void setApplicationData(String applicationData) {
        this.applicationData = applicationData;
    }

    /**
     * @return the callerIDNum
     */
    public String getCallerIDNum() {
        return callerIDNum;
    }

    /**
     * @param callerIDNum the callerIDNum to set
     */
    public void setCallerIDNum(String callerIDNum) {
        this.callerIDNum = callerIDNum;
    }

    /**
     * @return the callerIDName
     */
    public String getCallerIDName() {
        return callerIDName;
    }

    /**
     * @param callerIDName the callerIDName to set
     */
    public void setCallerIDName(String callerIDName) {
        this.callerIDName = callerIDName;
    }

    /**
     * @return the connectedLineNum
     */
    public String getConnectedLineNum() {
        return connectedLineNum;
    }

    /**
     * @param connectedLineNum the connectedLineNum to set
     */
    public void setConnectedLineNum(String connectedLineNum) {
        this.connectedLineNum = connectedLineNum;
    }

    /**
     * @return the connectedLineName
     */
    public String getConnectedLineName() {
        return connectedLineName;
    }

    /**
     * @param connectedLineName the connectedLineName to set
     */
    public void setConnectedLineName(String connectedLineName) {
        this.connectedLineName = connectedLineName;
    }

    /**
     * @return the duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * @return the accountCode
     */
    public String getAccountCode() {
        return accountCode;
    }

    /**
     * @param accountCode the accountCode to set
     */
    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    /**
     * @return the bridgedChannel
     */
    public String getBridgedChannel() {
        return bridgedChannel;
    }

    /**
     * @param bridgedChannel the bridgedChannel to set
     */
    public void setBridgedChannel(String bridgedChannel) {
        this.bridgedChannel = bridgedChannel;
    }

    /**
     * @return the bridgedUniqueID
     */
    public String getBridgedUniqueID() {
        return bridgedUniqueID;
    }

    /**
     * @param bridgedUniqueID the bridgedUniqueID to set
     */
    public void setBridgedUniqueID(String bridgedUniqueID) {
        this.bridgedUniqueID = bridgedUniqueID;
    }
    
    /**
     * @return the eventTime
     */
    public Date getEventTime() {
        return eventTime;
    }

    /**
     * @param eventTime the eventTime to set
     */
    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }
    
    public Document getDocument(){
        Document doc = new Document();
        doc.append("eventName",this.eventName);
        doc.append("channel",this.channel);
        doc.append("uniqueID",this.uniqueID);
        doc.append("context",this.context);
        doc.append("extension",this.extension);
        doc.append("priority",this.priority);
        doc.append("channelState",this.channelState);
        doc.append("channelStateDesc",this.channelStateDesc);
        doc.append("application",this.application);
        doc.append("applicationData",this.applicationData);
        doc.append("callerIDNum",this.callerIDNum);
        doc.append("callerIDName",this.callerIDName);
        doc.append("connectedLineNum",this.connectedLineNum);
        doc.append("connectedLineName",this.connectedLineName);
        doc.append("duration",this.duration);
        doc.append("accountCode",this.accountCode);
        doc.append("bridgedChannel",this.bridgedChannel);
        doc.append("bridgedUniqueID",this.bridgedUniqueID);
        doc.append("eventTime", this.getEventTime());
        return doc;
    }
}
