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
 * @author venky
 */
public class PeerStatusModel implements Serializable{
    private String eventName;
    private String privilege;
    private String channelType;
    private String peer;
    private String peerStatus;
    private String address;
    private String cause;
    private Date eventTime;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getPeer() {
        return peer;
    }

    public void setPeer(String peer) {
        this.peer = peer;
    }

    public String getPeerStatus() {
        return peerStatus;
    }

    public void setPeerStatus(String peerStatus) {
        this.peerStatus = peerStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }
    
    public Document getDocument(){
        Document doc = new Document();
        doc.append("eventName", this.eventName);
        doc.append("privilege", this.privilege);
        doc.append("channelType", this.channelType);
        doc.append("peer", this.peer);
        doc.append("peerStatus", this.peerStatus);
        doc.append("address", this.address);
        doc.append("cause", this.cause);
        doc.append("eventTime", this.eventTime);
        return doc;
    }
}
