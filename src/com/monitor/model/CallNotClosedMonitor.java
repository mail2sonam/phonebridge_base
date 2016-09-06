/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.monitor.model;

import java.util.ArrayList;
import java.util.Date;
import processdbevents.model.CdrModel;
import processdbevents.model.HangupModel;

/**
 *
 * @author bharath
 */
public class CallNotClosedMonitor {
    private String docID;
    private Date eventDateTime;
    private String callDirection;
    private ArrayList<HangupModel> hangUp;
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
     * @return the hangUp
     */
    public ArrayList<HangupModel> getHangUp() {
        return hangUp;
    }

    /**
     * @param hangUp the hangUp to set
     */
    public void setHangUp(ArrayList<HangupModel> hangUp) {
        this.hangUp = hangUp;
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

    /**
     * @return the callDirection
     */
    public String getCallDirection() {
        return callDirection;
    }

    /**
     * @param callDirection the callDirection to set
     */
    public void setCallDirection(String callDirection) {
        this.callDirection = callDirection;
    }
}
