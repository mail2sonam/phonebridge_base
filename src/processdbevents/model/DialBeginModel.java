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
 * @author sonamuthu
 */
public class DialBeginModel implements Serializable{
    private String eventName;
    private String privilege;
    private String subEvent;
    private String channel;
    private String destination;
    private String callerIDNum;
    private String callerIDName;
    private String connectedLineNum;
    private String connectedLineName;
    private String uniqueID;
    private String destUniqueID;
    private String dialString;
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
     * @return the subEvent
     */
    public String getSubEvent() {
        return subEvent;
    }

    /**
     * @param subEvent the subEvent to set
     */
    public void setSubEvent(String subEvent) {
        this.subEvent = subEvent;
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
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(String destination) {
        this.destination = destination;
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
     * @return the destUniqueID
     */
    public String getDestUniqueID() {
        return destUniqueID;
    }

    /**
     * @param destUniqueID the destUniqueID to set
     */
    public void setDestUniqueID(String destUniqueID) {
        this.destUniqueID = destUniqueID;
    }

    /**
     * @return the dialString
     */
    public String getDialString() {
        return dialString;
    }

    /**
     * @param dialString the dialString to set
     */
    public void setDialString(String dialString) {
        this.dialString = dialString;
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
        doc.append("subEvent",this.subEvent);
        doc.append("channel",this.channel);
        doc.append("destination",this.destination);
        doc.append("callerIDNum",this.callerIDNum);
        doc.append("callerIDName",this.callerIDName);
        doc.append("connectedLineNum",this.connectedLineNum);
        doc.append("connectedLineName",this.connectedLineName);
        doc.append("uniqueID",this.uniqueID);
        doc.append("destUniqueID",this.destUniqueID);
        doc.append("dialString",this.dialString);
        doc.append("eventTime", this.eventTime);
        return doc;
    }
}
