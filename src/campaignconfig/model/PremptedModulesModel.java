/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campaignconfig.model;

import java.util.ArrayList;

/**
 *
 * @author sonamuthu
 */
public class PremptedModulesModel {
    private String moduleName;
    private String moduleCollection;
    private ArrayList<String> phoneFields;
    private String nameFieldKey;
    private ArrayList<String> sortFields;
    /**
     * @return the moduleName
     */
    public String getModuleName() {
        return moduleName;
    }

    public ArrayList<String> getSortFields() {
        return sortFields;
    }

    public void setSortFields(ArrayList<String> sortFields) {
        this.sortFields = sortFields;
    }

    /**
     * @param moduleName the moduleName to set
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * @return the moduleCollection
     */
    public String getModuleCollection() {
        return moduleCollection;
    }

    /**
     * @param moduleCollection the moduleCollection to set
     */
    public void setModuleCollection(String moduleCollection) {
        this.moduleCollection = moduleCollection;
    }

    /**
     * @return the phoneFields
     */
    public ArrayList<String> getPhoneFields() {
        return phoneFields;
    }

    /**
     * @param phoneFields the phoneFields to set
     */
    public void setPhoneFields(ArrayList<String> phoneFields) {
        this.phoneFields = phoneFields;
    }

    /**
     * @return the nameFieldKey
     */
    public String getNameFieldKey() {
        return nameFieldKey;
    }

    /**
     * @param nameFieldKey the nameFieldKey to set
     */
    public void setNameFieldKey(String nameFieldKey) {
        this.nameFieldKey = nameFieldKey;
    }
}
