/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.model;

import java.util.Date;
import org.bson.Document;

/**
 *
 * @author bharath
 */
public class QueueCallerAbandonModel {
    private String eventName;
    private String privilege;
    private String queue;
    private String uniqueID;
    private String position;
    private String originalPosition;
    private int holdTime;
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
     * @return the originalPosition
     */
    public String getOriginalPosition() {
        return originalPosition;
    }

    /**
     * @param originalPosition the originalPosition to set
     */
    public void setOriginalPosition(String originalPosition) {
        this.originalPosition = originalPosition;
    }

    /**
     * @return the holdTime
     */
    public int getHoldTime() {
        return holdTime;
    }

    /**
     * @param holdTime the holdTime to set
     */
    public void setHoldTime(int holdTime) {
        this.holdTime = holdTime;
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
        doc.append("queue",this.queue);
        doc.append("uniqueID",this.uniqueID);
        doc.append("position",this.position);
        doc.append("originalPosition",this.originalPosition);
        doc.append("holdTime",this.holdTime);
        doc.append("eventTime", this.eventTime);
        return doc;
    }
}
