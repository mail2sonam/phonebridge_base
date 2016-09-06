/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.monitor.model;

import java.util.ArrayList;
import java.util.Date;
import processdbevents.model.CdrModel;

/**
 *
 * @author bharath
 */
public class CallsWithOutRecordingMonitor {
    private String docID;
    private Date eventDateTime;
    private ArrayList<CdrModel> cdr;
    private boolean callClosed;

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public Date getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(Date eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public ArrayList<CdrModel> getCdr() {
        return cdr;
    }

    public void setCdr(ArrayList<CdrModel> cdr) {
        this.cdr = cdr;
    }

    public boolean isCallClosed() {
        return callClosed;
    }

    public void setCallClosed(boolean callClosed) {
        this.callClosed = callClosed;
    }
}
