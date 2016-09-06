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
public class HoldModel implements Serializable{
    private String eventName;
    private String privilege;
    private String state;
    private String channel;
    private String uniqueID;
    private String holdClass;
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
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
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
     * @return the holdClass
     */
    public String getHoldClass() {
        return holdClass;
    }

    /**
     * @param holdClass the holdClass to set
     */
    public void setHoldClass(String holdClass) {
        this.holdClass = holdClass;
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
        doc.append("state",this.state);
        doc.append("channel",this.channel);
        doc.append("uniqueID",this.uniqueID);
        doc.append("holdClass",this.holdClass);
        doc.append("eventTime", this.eventTime);
        return doc;
    }
}
