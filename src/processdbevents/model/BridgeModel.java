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
public class BridgeModel implements Serializable{
    private String eventName;
    private String privilege;
    private String bridgeState;
    private String bridgeType;
    private String channel1;
    private String channel2;
    private String uniqueID1;
    private String uniqueID2;
    private String callerID1;
    private String callerID2;
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
     * @return the bridgeState
     */
    public String getBridgeState() {
        return bridgeState;
    }

    /**
     * @param bridgeState the bridgeState to set
     */
    public void setBridgeState(String bridgeState) {
        this.bridgeState = bridgeState;
    }

    /**
     * @return the bridgeType
     */
    public String getBridgeType() {
        return bridgeType;
    }

    /**
     * @param bridgeType the bridgeType to set
     */
    public void setBridgeType(String bridgeType) {
        this.bridgeType = bridgeType;
    }

    /**
     * @return the channel1
     */
    public String getChannel1() {
        return channel1;
    }

    /**
     * @param channel1 the channel1 to set
     */
    public void setChannel1(String channel1) {
        this.channel1 = channel1;
    }

    /**
     * @return the channel2
     */
    public String getChannel2() {
        return channel2;
    }

    /**
     * @param channel2 the channel2 to set
     */
    public void setChannel2(String channel2) {
        this.channel2 = channel2;
    }

    /**
     * @return the uniqueID1
     */
    public String getUniqueID1() {
        return uniqueID1;
    }

    /**
     * @param uniqueID1 the uniqueID1 to set
     */
    public void setUniqueID1(String uniqueID1) {
        this.uniqueID1 = uniqueID1;
    }

    /**
     * @return the uniqueID2
     */
    public String getUniqueID2() {
        return uniqueID2;
    }

    /**
     * @param uniqueID2 the uniqueID2 to set
     */
    public void setUniqueID2(String uniqueID2) {
        this.uniqueID2 = uniqueID2;
    }

    /**
     * @return the callerID1
     */
    public String getCallerID1() {
        return callerID1;
    }

    /**
     * @param callerID1 the callerID1 to set
     */
    public void setCallerID1(String callerID1) {
        this.callerID1 = callerID1;
    }

    /**
     * @return the callerID2
     */
    public String getCallerID2() {
        return callerID2;
    }

    /**
     * @param callerID2 the callerID2 to set
     */
    public void setCallerID2(String callerID2) {
        this.callerID2 = callerID2;
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
        doc.append("bridgeState",this.bridgeState);
        doc.append("bridgeType",this.bridgeType);
        doc.append("channel1",this.channel1);
        doc.append("channel2",this.channel2);
        doc.append("uniqueID1",this.uniqueID1);
        doc.append("uniqueID2",this.uniqueID2);
        doc.append("callerID1",this.callerID1);
        doc.append("callerID2",this.callerID2);
        doc.append("eventTime", this.eventTime);
        return doc;
    }
}
