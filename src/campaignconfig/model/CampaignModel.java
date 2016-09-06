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
public class CampaignModel {
    
    public CampaignModel(){}
    
    private ArrayList<DispositionModel> dispositions;
    private ArrayList<DispositionModel> dispositionsFilterValue;
    private ArrayList<ShowFieldModel> showFields;
    private ArrayList<ShowFieldModel> showFieldsFilterValue;
    private String campaignName;
    private String dialMethod;
    private String status;
    private String campaignID;
    private long openedLeadCount;
    private long totalLeadCount;
    private long closedLeadCount;
    private long callBackLeadCount;
    private int wrapUpTime;
    private int timeBetweenCall;
    private boolean isGeneralPolling;
    private boolean isDefault;
    private Date listCreatedOn;
    private Date campaignCreatedOn;
    private String listID;
    private String trunkValue;
    private String serverID;
    private int noOfClientTries;
    private int noOfAgentTries;
    private String didNumber;
    private String selectedMode;
    private String campaignSource;
    private String moduleLinked;
    private PremptedModulesModel premptedModule; 
    private String callDirection;
    private String linkedModuleCollection;
    private boolean branchWise;
    private String sortField;
    private String moduleForNewClients;
    private String resourceURL;
    private String popupIFrameUrl;
    private int slaTime;
    private boolean callBackCampaign;
    private String newClientURL;
    private String exisitingClientURL;
    private String cdrURL;
    private int retryAfter;

    public String getCdrURL() {
        return cdrURL;
    }

    public void setCdrURL(String cdrURL) {
        this.cdrURL = cdrURL;
    }
    public String getNewClientURL() {
        return newClientURL;
    }

    public void setNewClientURL(String newClientURL) {
        this.newClientURL = newClientURL;
    }

    public int getSlaTime() {
        return slaTime;
    }

    public void setSlaTime(int slaTime) {
        this.slaTime = slaTime;
    }

    public String getExisitingClientURL() {
        return exisitingClientURL;
    }

    public void setExisitingClientURL(String exisitingClientURL) {
        this.exisitingClientURL = exisitingClientURL;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }
    
    
    public boolean isBranchWise() {
        return branchWise;
    }

    public void setBranchWise(boolean branchWise) {
        this.branchWise = branchWise;
    }
    
    
    public ArrayList<DispositionModel> getDispositions() {
        return dispositions;
    }

    public void setDispositions(ArrayList<DispositionModel> dispositions) {
        this.dispositions = dispositions;
    }
    
    public void setCampaignID(String campaignID) {
        this.campaignID = campaignID;
    }

    public String getCampaignID() {
        return campaignID;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
   
    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getDialMethod() {
        return dialMethod;
    }

    public void setDialMethod(String dialMethod) {
        this.dialMethod = dialMethod;
    }

    public int getWrapUpTime() {
        return wrapUpTime;
    }

    public void setWrapUpTime(int wrapUpTime) {
        this.wrapUpTime = wrapUpTime;
    }

    public int getTimeBetweenCall() {
        return timeBetweenCall;
    }

    public void setTimeBetweenCall(int timeBetweenCall) {
        this.timeBetweenCall = timeBetweenCall;
    }

    public boolean isIsGeneralPolling() {
        return isGeneralPolling;
    }

    public void setIsGeneralPolling(boolean isGeneralPolling) {
        this.isGeneralPolling = isGeneralPolling;
    }

     public ArrayList<ShowFieldModel> getShowFields() {
        return showFields;
    }

    public void setShowFields(ArrayList<ShowFieldModel> showFields) {
        this.showFields = showFields;
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public long getOpenedLeadCount() {
        return openedLeadCount;
    }

    public void setOpenedLeadCount(long openedLeadCount) {
        this.openedLeadCount = openedLeadCount;
    }

    public long getTotalLeadCount() {
        return totalLeadCount;
    }

    public void setTotalLeadCount(long totalLeadCount) {
        this.totalLeadCount = totalLeadCount;
    }

    public long getClosedLeadCount() {
        return closedLeadCount;
    }

    public void setClosedLeadCount(long closedLeadCount) {
        this.closedLeadCount = closedLeadCount;
    }

    public long getCallBackLeadCount() {
        return callBackLeadCount;
    }

    public void setCallBackLeadCount(long callBackLeadCount) {
        this.callBackLeadCount = callBackLeadCount;
    }

    public String getTrunkValue() {
        return trunkValue;
    }

    public void setTrunkValue(String trunkValue) {
        this.trunkValue = trunkValue;
    }

    public int getNoOfClientTries() {
        return noOfClientTries;
    }

    public void setNoOfClientTries(int noOfClientTries) {
        this.noOfClientTries = noOfClientTries;
    }

    public int getNoOfAgentTries() {
        return noOfAgentTries;
    }

    public void setNoOfAgentTries(int noOfAgentTries) {
        this.noOfAgentTries = noOfAgentTries;
    }

    public int getRetryAfter() {
        return retryAfter;
    }

    public void setRetryAfter(int retryAfter) {
        this.retryAfter = retryAfter;
    }
    

    public CampaignModel(String campaignID,String campaignName,String dialMethod,String status,
        int wrapUpTime,int timeBetweenCall,boolean isGeneralPolling,String listID,boolean isDefault,Date listCreatedOn,
        Date campaignCreatedOn,String trunkValue,String serverID,int noOfClientTries,int noOfAgentTries,
        String didNumber,String typeOfCampaign,String moduleLinked,String callDirection,String linkedModuleCollection,
        boolean branchWise,String sortField,String resourceURL,String iFrameUrl,boolean callBackCampaign,
        String newClientURL,String exisitingClientURL,String cdrURL,int retryAfter){
        this.sortField=sortField;
        this.campaignID = campaignID;
        this.campaignName = campaignName;
        this.dialMethod = dialMethod;
        this.wrapUpTime = wrapUpTime;
        this.timeBetweenCall = timeBetweenCall;
        this.isGeneralPolling = isGeneralPolling;
        this.status = status;
        this.isDefault = isDefault;
        this.listCreatedOn = listCreatedOn;
        this.campaignCreatedOn = campaignCreatedOn;
        this.listID = listID;
        this.trunkValue = trunkValue;
        this.serverID = serverID;
        this.noOfClientTries = noOfClientTries;
        this.noOfAgentTries = noOfAgentTries;
        this.didNumber = didNumber;
        this.campaignSource = typeOfCampaign;
        this.moduleLinked = moduleLinked;
        this.callDirection = callDirection;
        this.linkedModuleCollection = linkedModuleCollection;
        this.branchWise = branchWise;
        this.resourceURL = resourceURL;
        this.popupIFrameUrl = iFrameUrl;
        this.callBackCampaign = callBackCampaign;
        this.newClientURL = newClientURL;
        this.exisitingClientURL = exisitingClientURL;
        this.cdrURL = cdrURL;
        this.retryAfter = retryAfter;
    }

    /**
     * @return the listCreatedOn
     */
    public Date getListCreatedOn() {
        return listCreatedOn;
    }

    /**
     * @param listCreatedOn the listCreatedOn to set
     */
    public void setListCreatedOn(Date listCreatedOn) {
        this.listCreatedOn = listCreatedOn;
    }

    /**
     * @return the campaignCreatedOn
     */
    public Date getCampaignCreatedOn() {
        return campaignCreatedOn;
    }

    /**
     * @param campaignCreatedOn the campaignCreatedOn to set
     */
    public void setCampaignCreatedOn(Date campaignCreatedOn) {
        this.campaignCreatedOn = campaignCreatedOn;
    }

    public ArrayList<DispositionModel> getDispositionsFilterValue() {
        return dispositionsFilterValue;
    }

    public void setDispositionsFilterValue(ArrayList<DispositionModel> dispositionsFilterValue) {
        this.dispositionsFilterValue = dispositionsFilterValue;
    }

    public ArrayList<ShowFieldModel> getShowFieldsFilterValue() {
        return showFieldsFilterValue;
    }

    public void setShowFieldsFilterValue(ArrayList<ShowFieldModel> showFieldsFilterValue) {
        this.showFieldsFilterValue = showFieldsFilterValue;
    }

    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public String getDidNumber() {
        return didNumber;
    }

    public void setDidNumber(String didNumber) {
        this.didNumber = didNumber;
    }

    public String getSelectedMode() {
        return selectedMode;
    }

    public void setSelectedMode(String selectedMode) {
        this.selectedMode = selectedMode;
    }

    /**
     * @return the campaignSource
     */
    public String getCampaignSource() {
        return campaignSource;
    }

    /**
     * @param campaignSource the campaignSource to set
     */
    public void setCampaignSource(String campaignSource) {
        this.campaignSource = campaignSource;
    }

    /**
     * @return the moduleLinked
     */
    public String getModuleLinked() {
        return moduleLinked;
    }

    /**
     * @param moduleLinked the moduleLinked to set
     */
    public void setModuleLinked(String moduleLinked) {
        this.moduleLinked = moduleLinked;
    }

    public PremptedModulesModel getPremptedModule() {
        return premptedModule;
    }

    public void setPremptedModule(PremptedModulesModel premptedModule) {
        this.premptedModule = premptedModule;
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

    /**
     * @return the linkedModuleCollection
     */
    public String getLinkedModuleCollection() {
        return linkedModuleCollection;
    }

    /**
     * @param linkedModuleCollection the linkedModuleCollection to set
     */
    public void setLinkedModuleCollection(String linkedModuleCollection) {
        this.linkedModuleCollection = linkedModuleCollection;
    }

    public String getModuleForNewClients() {
        return moduleForNewClients;
    }

    public void setModuleForNewClients(String moduleForNewClients) {
        this.moduleForNewClients = moduleForNewClients;
    }

    public String getResourceURL() {
        return resourceURL;
    }

    public void setResourceURL(String resourceURL) {
        this.resourceURL = resourceURL;
    }

    public String getPopupIFrameUrl() {
        return popupIFrameUrl;
    }

    public void setPopupIFrameUrl(String popupIFrameUrl) {
        this.popupIFrameUrl = popupIFrameUrl;
    }

    public boolean isCallBackCampaign() {
        return callBackCampaign;
    }

    public void setCallBackCampaign(boolean callBackCampaign) {
        this.callBackCampaign = callBackCampaign;
    }
}
