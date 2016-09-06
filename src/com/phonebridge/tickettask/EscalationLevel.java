/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridge.tickettask;

import java.util.ArrayList;

/**
 *
 * @author bharath
 */
public class EscalationLevel {
    private String levelID;
    private String escalationLevel;
    private double TATTime;
    private ArrayList<AssociateResponsible> associatesResponsible;
    private int escalationLevelNo;

    public String getEscalationLevel() {
        return escalationLevel;
    }

    public void setEscalationLevel(String escalationLevel) {
        this.escalationLevel = escalationLevel;
    }

    public double getTATTime() {
        return TATTime;
    }

    public void setTATTime(double TATTime) {
        this.TATTime = TATTime;
    }

    public ArrayList<AssociateResponsible> getAssociatesResponsible() {
        return associatesResponsible;
    }

    public void setAssociatesResponsible(ArrayList<AssociateResponsible> associatesResponsible) {
        this.associatesResponsible = associatesResponsible;
    }
    
    public EscalationLevel(){
        
    }
    
    public EscalationLevel(String levelID,String escalationLevel,int escalationLevelNo,double TATTime,ArrayList<AssociateResponsible> associatesResponsible){
        this.levelID = levelID;
        this.escalationLevel = escalationLevel;
        this.TATTime = TATTime;
        this.escalationLevelNo = escalationLevelNo;
        this.associatesResponsible = associatesResponsible;
    }

    public String getLevelID() {
        return levelID;
    }

    public void setLevelID(String levelID) {
        this.levelID = levelID;
    }

    public int getEscalationLevelNo() {
        return escalationLevelNo;
    }

    public void setEscalationLevelNo(int escalationLevelNo) {
        this.escalationLevelNo = escalationLevelNo;
    }
}
