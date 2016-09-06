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
public class ShowFieldModel {
    
    public ShowFieldModel(){}
    //urlFieldName
    private String fieldLabel,fieldValue,displayValue,showFieldID;
    
    private boolean isReportKey;

    public boolean isIsReportKey() {
        return isReportKey;
    }

    public void setIsReportKey(boolean isReportKey) {
        this.isReportKey = isReportKey;
    }
    
    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
    
    private boolean isUrl;

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

   public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

  
    public boolean isIsUrl() {
        return isUrl;
    }

    public void setIsUrl(boolean isUrl) {
        this.isUrl = isUrl;
    }

    public String getShowFieldID() {
        return showFieldID;
    }

    public void setShowFieldID(String showFieldID) {
        this.showFieldID = showFieldID;
    }
    
    
    //this.urlFieldName = urlFieldName;
    public ShowFieldModel(String showFieldID,String fieldLabel,String fieldValue,boolean isReportKey){
        this.showFieldID = showFieldID;
        this.fieldLabel = fieldLabel;
        this.fieldValue = fieldValue;
        this.isUrl = isReportKey;
        
    }
    
}
