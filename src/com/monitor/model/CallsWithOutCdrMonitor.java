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
public class CallsWithOutCdrMonitor {
    private String docID;
    private Date eventDateTime;
    private String callDirection;
    private boolean callClosed;

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
     * @return the callClosed
     */
    public boolean isCallClosed() {
        return callClosed;
    }

    /**
     * @param callClosed the callClosed to set
     */
    public void setCallClosed(boolean callClosed) {
        this.callClosed = callClosed;
    }

    public String getCallDirection() {
        return callDirection;
    }

    public void setCallDirection(String callDirection) {
        this.callDirection = callDirection;
    }
}
