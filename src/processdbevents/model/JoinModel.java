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
public class JoinModel implements Serializable{
    private String eventName;
    private String privilege;
    private String channel;
    private String callerIdNum;
    private String callerIdName;
    private String connectedLineNum;
    private String connectedLineName;
    private String queue;
    private String position;
    private String count;
    private String uniqueID;
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
     * @return the callerIdNum
     */
    public String getCallerIdNum() {
        return callerIdNum;
    }

    /**
     * @param callerIdNum the callerIdNum to set
     */
    public void setCallerIdNum(String callerIdNum) {
        this.callerIdNum = callerIdNum;
    }

    /**
     * @return the callerIdName
     */
    public String getCallerIdName() {
        return callerIdName;
    }

    /**
     * @param callerIdName the callerIdName to set
     */
    public void setCallerIdName(String callerIdName) {
        this.callerIdName = callerIdName;
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
     * @return the queue
     */
    public String getQueue() {
        return queue;
    }

    /**
     * @param queue the queue to set
     */
    public void setQueue(String queue) {
        this.queue = queue;
    }

    /**
     * @return the position
     */
    public String getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * @return the count
     */
    public String getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(String count) {
        this.count = count;
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
        doc.append("channel",this.channel);
        doc.append("callerIdNum",this.callerIdNum);
        doc.append("callerIdName",this.callerIdName);
        doc.append("connectedLineNum",this.connectedLineNum);
        doc.append("connectedLineName",this.connectedLineName);
        doc.append("queue",this.queue);
        doc.append("position",this.position);
        doc.append("count",this.count);
        doc.append("uniqueID",this.uniqueID);
        doc.append("eventTime", this.eventTime);
        return doc;
    }
}
