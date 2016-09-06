/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.model;

import java.io.Serializable;
import java.util.Date;
import org.bson.Document;

/**
 *
 * @author bharath
 */
public class TransferModel implements Serializable{
    private String eventName;
    private String privilege;
    private String tranferMethod;
    private String tranferType;
    private String channel;
    private String uniqueID;
    private String sipCallId;
    private String targetChannel;
    private String targetUniqueId;
    private String transferExten;
    private String transferContext;
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
     * @return the privilege
     */
    public String getPrivilege() {
        return privilege;
    }

    /**
     * @param privilege the privilege to set
     */
    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    /**
     * @return the tranferMethod
     */
    public String getTranferMethod() {
        return tranferMethod;
    }

    /**
     * @param tranferMethod the tranferMethod to set
     */
    public void setTranferMethod(String tranferMethod) {
        this.tranferMethod = tranferMethod;
    }

    /**
     * @return the tranferType
     */
    public String getTranferType() {
        return tranferType;
    }

    /**
     * @param tranferType the tranferType to set
     */
    public void setTranferType(String tranferType) {
        this.tranferType = tranferType;
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
     * @return the sipCallId
     */
    public String getSipCallId() {
        return sipCallId;
    }

    /**
     * @param sipCallId the sipCallId to set
     */
    public void setSipCallId(String sipCallId) {
        this.sipCallId = sipCallId;
    }

    /**
     * @return the targetChannel
     */
    public String getTargetChannel() {
        return targetChannel;
    }

    /**
     * @param targetChannel the targetChannel to set
     */
    public void setTargetChannel(String targetChannel) {
        this.targetChannel = targetChannel;
    }

    /**
     * @return the targetUniqueId
     */
    public String getTargetUniqueId() {
        return targetUniqueId;
    }

    /**
     * @param targetUniqueId the targetUniqueId to set
     */
    public void setTargetUniqueId(String targetUniqueId) {
        this.targetUniqueId = targetUniqueId;
    }

    /**
     * @return the transferExten
     */
    public String getTransferExten() {
        return transferExten;
    }

    /**
     * @param transferExten the transferExten to set
     */
    public void setTransferExten(String transferExten) {
        this.transferExten = transferExten;
    }

    /**
     * @return the transferContext
     */
    public String getTransferContext() {
        return transferContext;
    }

    /**
     * @param transferContext the transferContext to set
     */
    public void setTransferContext(String transferContext) {
        this.transferContext = transferContext;
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
        doc.append("privilege",this.privilege);
        doc.append("tranferMethod",this.tranferMethod);
        doc.append("tranferType",this.tranferType);
        doc.append("channel",this.channel);
        doc.append("uniqueID",this.uniqueID);
        doc.append("sipCallId",this.sipCallId);
        doc.append("targetChannel",this.targetChannel);
        doc.append("targetUniqueId",this.targetUniqueId);
        doc.append("transferExten",this.transferExten);
        doc.append("transferContext",this.transferContext);
        doc.append("transferClosed",false);
        doc.append("eventTime", this.eventTime);
        return doc;
    }
}
