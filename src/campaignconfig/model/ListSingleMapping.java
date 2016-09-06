/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campaignconfig.model;

/**
 *
 * @author harini
 */
public class ListSingleMapping {

    public ListSingleMapping(String excelField, String campaignHeader) {
        this.excelField = excelField;
        this.campaignHeader = campaignHeader;
    }
 String excelField,campaignHeader;

    public String getExcelField() {
        return excelField;
    }

    public void setExcelField(String excelField) {
        this.excelField = excelField;
    }

    public String getCampaignHeader() {
        return campaignHeader;
    }

    public void setCampaignHeader(String CampaignHeader) {
        this.campaignHeader = CampaignHeader;
    }
 
}
