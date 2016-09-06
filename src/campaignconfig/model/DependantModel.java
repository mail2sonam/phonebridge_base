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

public class DependantModel {
    public DependantModel(){}
    
    private String fieldValue;
    private String fieldLabel;
    private String fieldType;
    private String dependantID;
    private String dropDownValue;
    private Date dateValue;
    private String slotName;
    private String slotSlab;
    private String selectedSlotName;
    private String selectedSlotSlab;
    private boolean timeSlotGlobal;
    private boolean conversionDependant; 
    private ArrayList<String> slotNameArr;
    private ArrayList<String> slotSlabArr;
    private String[] dropDownArr;

    public String[] getDropDownArr() {
        return dropDownArr;
    }

    public void setDropDownArr(String[] dropDownArr) {
        this.dropDownArr = dropDownArr;
    }
    
    
    public String getDependantID() {
        return dependantID;
    }

    public void setDependantID(String dependantID) {
        this.dependantID = dependantID;
    }

    
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

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getDropDownValue() {
        return dropDownValue;
    }

    public void setDropDownValue(String dropDownValue) {
        this.dropDownValue = dropDownValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }
    
    public DependantModel(String dependantID,String fieldValue,String fieldLabel,String fieldType,String dropDownValue,
        Date dateValue,String slotName,String slotSlab,boolean timeSlotGlobal,boolean conversionDependant){
        this.fieldLabel = fieldLabel;
        this.fieldType = fieldType;
        this.fieldValue = fieldValue;
        this.dependantID = dependantID;
        this.dropDownValue = dropDownValue;
        this.dateValue = dateValue;
        this.dropDownArr = splitByComma(dropDownValue);
        this.slotName = slotName;
        this.slotSlab = slotSlab;
        this.timeSlotGlobal = timeSlotGlobal;
        this.conversionDependant = conversionDependant;
    }

    private String[] splitByComma(String dropDownValue) {
        String arr[]=dropDownValue.split(",");
        for(int i=0;i<arr.length;i++)
                arr[i]=arr[i].replace("{#CONVERSION}", "");
        return arr;
    }

    /**
     * @return the slotName
     */
    public String getSlotName() {
        return slotName;
    }

    /**
     * @param slotName the slotName to set
     */
    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    /**
     * @return the slotSlab
     */
    public String getSlotSlab() {
        return slotSlab;
    }

    /**
     * @param slotSlab the slotSlab to set
     */
    public void setSlotSlab(String slotSlab) {
        this.slotSlab = slotSlab;
    }

    /**
     * @return the selectedSlotName
     */
    public String getSelectedSlotName() {
        return selectedSlotName;
    }

    /**
     * @param selectedSlotName the selectedSlotName to set
     */
    public void setSelectedSlotName(String selectedSlotName) {
        this.selectedSlotName = selectedSlotName;
    }

    /**
     * @return the selectedSlotSlab
     */
    public String getSelectedSlotSlab() {
        return selectedSlotSlab;
    }

    /**
     * @param selectedSlotSlab the selectedSlotSlab to set
     */
    public void setSelectedSlotSlab(String selectedSlotSlab) {
        this.selectedSlotSlab = selectedSlotSlab;
    }

    /**
     * @return the timeSlotGlobal
     */
    public boolean isTimeSlotGlobal() {
        return timeSlotGlobal;
    }

    /**
     * @param timeSlotGlobal the timeSlotGlobal to set
     */
    public void setTimeSlotGlobal(boolean timeSlotGlobal) {
        this.timeSlotGlobal = timeSlotGlobal;
    }

    /**
     * @return the slotNameArr
     */
    public ArrayList<String> getSlotNameArr() {
        return slotNameArr;
    }

    /**
     * @param slotNameArr the slotNameArr to set
     */
    public void setSlotNameArr(ArrayList<String> slotNameArr) {
        this.slotNameArr = slotNameArr;
    }

    /**
     * @return the slotSlabArr
     */
    public ArrayList<String> getSlotSlabArr() {
        return slotSlabArr;
    }

    /**
     * @param slotSlabArr the slotSlabArr to set
     */
    public void setSlotSlabArr(ArrayList<String> slotSlabArr) {
        this.slotSlabArr = slotSlabArr;
    }

    public boolean isConversionDependant() {
        return conversionDependant;
    }

    public void setConversionDependant(boolean conversionDependant) {
        this.conversionDependant = conversionDependant;
    }
    
}
