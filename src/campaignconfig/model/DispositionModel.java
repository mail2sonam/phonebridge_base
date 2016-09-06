/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campaignconfig.model;

import java.util.ArrayList;

/**
 *
 * @author harini
 */

public class DispositionModel {
   
    public DispositionModel(){}
    private String fieldValue,fieldLabel,callWorkFlow,dispositionID;
    private ArrayList<DependantModel> dependant;
    private ArrayList<DependantModel> dependantFilterValue;
    private boolean sendSMS;
    private String smsText;
    private boolean conversion;

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public String getDispositionID() {
        return dispositionID;
    }

    public void setDispositionID(String dispositionID) {
        this.dispositionID = dispositionID;
    }

    public ArrayList<DependantModel> getDependant() {
        return dependant;
    }

    public void setDependant(ArrayList<DependantModel> dependant) {
        this.dependant = dependant;
    }

    public String getCallWorkFlow() {
        return callWorkFlow;
    }

    public void setCallWorkFlow(String callWorkFlow) {
        this.callWorkFlow = callWorkFlow;
    }

    public boolean isConversion() {
        return conversion;
    }

    public void setConversion(boolean conversion) {
        this.conversion = conversion;
    }
    
        
    public DispositionModel(String dispositionID,String fieldLabel,String fieldValue,String callWorkFlow,boolean sendSMS,
            String smsText,boolean conversion){
        this.dispositionID = dispositionID;
        this.fieldValue = fieldValue;
        this.fieldLabel = fieldLabel;
        this.callWorkFlow = callWorkFlow;
        this.sendSMS = sendSMS;
        this.smsText = smsText;
        this.conversion = conversion;
    }

    /**
     * @return the sendSMS
     */
    public boolean isSendSMS() {
        return sendSMS;
    }

    /**
     * @param sendSMS the sendSMS to set
     */
    public void setSendSMS(boolean sendSMS) {
        this.sendSMS = sendSMS;
    }

    /**
     * @return the smsText
     */
    public String getSmsText() {
        return smsText;
    }

    /**
     * @param smsText the smsText to set
     */
    public void setSmsText(String smsText) {
        this.smsText = smsText;
    }

    public ArrayList<DependantModel> getDependantFilterValue() {
        return dependantFilterValue;
    }

    public void setDependantFilterValue(ArrayList<DependantModel> dependantFilterValue) {
        this.dependantFilterValue = dependantFilterValue;
    }
    
}
