/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.monitor.model;

import java.util.ArrayList;
import java.util.Date;
import processdbevents.model.CdrModel;
import processdbevents.model.DialBeginModel;

/**
 *
 * @author bharath
 */
public class CallDirectionMonitor {
    private String docID;
    private Date eventDateTime;
    private ArrayList<DialBeginModel> dialBegin;
    private ArrayList<CdrModel> cdr;

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
     * @return the dialBegin
     */
    public ArrayList<DialBeginModel> getDialBegin() {
        return dialBegin;
    }

    /**
     * @param dialBegin the dialBegin to set
     */
    public void setDialBegin(ArrayList<DialBeginModel> dialBegin) {
        this.dialBegin = dialBegin;
    }

    /**
     * @return the cdr
     */
    public ArrayList<CdrModel> getCdr() {
        return cdr;
    }

    /**
     * @param cdr the cdr to set
     */
    public void setCdr(ArrayList<CdrModel> cdr) {
        this.cdr = cdr;
    }
}
