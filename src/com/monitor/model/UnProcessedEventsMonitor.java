/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.monitor.model;

import java.util.Date;

/**
 *
 * @author bharath
 */
public class UnProcessedEventsMonitor {
    private String docID;
    private Date eventDateTime;
    private String eventName;

    /**
     * @return the docID
     */
    public String getDocID() {
        return docID;
    }

    /**
     * @param docID the docID to set
     */
    public void setDocID(String docID) {
        this.docID = docID;
    }

    /**
     * @return the eventDateTime
     */
    public Date getEventDateTime() {
        return eventDateTime;
    }

    /**
     * @param eventDateTime the eventDateTime to set
     */
    public void setEventDateTime(Date eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

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
}
