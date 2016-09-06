/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campaignconfig.model;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author harini
 */
public class ListModel {
    
    public ListModel(){}
    
    private String campaignID,listname,fieldLabel,listID,campaignName,rowsAccepted,noDataRows,invaildPhoneNoRows,phoneNoExistRows;
    private String extensionUnAvailableRows;
    private Date dateTime;
    ArrayList<String> listmap = new ArrayList<>();

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public String getCampaignID() {
        return campaignID;
    }

    public void setCampaignID(String campaignID) {
        this.campaignID = campaignID;
    }

    public String getListname() {
        return listname;
    }

    public void setListname(String listname) {
        this.listname = listname;
    }

    

    public ArrayList<String> getListmap() {
        return listmap;
    }

    public void setListmap(ArrayList<String> listmap) {
        this.listmap = listmap;
    }
    
    
    public ListModel(String fieldLabel)
    {
        this.fieldLabel = fieldLabel;
        
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getRowsAccepted() {
        return rowsAccepted;
    }

    public void setRowsAccepted(String rowsAccepted) {
        this.rowsAccepted = rowsAccepted;
    }

    public String getNoDataRows() {
        return noDataRows;
    }

    public void setNoDataRows(String noDataRows) {
        this.noDataRows = noDataRows;
    }

    public String getInvaildPhoneNoRows() {
        return invaildPhoneNoRows;
    }

    public void setInvaildPhoneNoRows(String invaildPhoneNoRows) {
        this.invaildPhoneNoRows = invaildPhoneNoRows;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getPhoneNoExistRows() {
        return phoneNoExistRows;
    }

    public void setPhoneNoExistRows(String phoneNoExistRows) {
        this.phoneNoExistRows = phoneNoExistRows;
    }

    public String getExtensionUnAvailableRows() {
        return extensionUnAvailableRows;
    }

    public void setExtensionUnAvailableRows(String extensionUnAvailableRows) {
        this.extensionUnAvailableRows = extensionUnAvailableRows;
    }
    
    
    
    public ListModel(String rowsAccepted,String noDataRows,String invalidPhoneNoRows,String phoneNoExistRows,String extensionUnAvailableRows)
    {
        this.rowsAccepted = rowsAccepted;
        this.noDataRows = noDataRows;
        this.invaildPhoneNoRows = invalidPhoneNoRows;
        this.phoneNoExistRows = phoneNoExistRows;
        this.extensionUnAvailableRows = extensionUnAvailableRows;
    }
}
