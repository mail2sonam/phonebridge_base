/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campaignconfig.model;

/**
 *
 * @author sonamuthu
 */
public class DependantForSave {
String strLabel,strValue;
boolean isConversionDependant;

    public String getStrLabel() {
        return strLabel;
    }

    public void setStrLabel(String strLabel) {
        this.strLabel = strLabel;
    }

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public boolean isIsConversionDependant() {
        return isConversionDependant;
    }

    public void setIsConversionDependant(boolean isConversionDependant) {
        this.isConversionDependant = isConversionDependant;
    }

public DependantForSave(String strLabel,String strValue,boolean isConversionDependant)
    {
    this.strLabel=strLabel;
    this.strValue=strValue;
    this.isConversionDependant=isConversionDependant;
    }
}
