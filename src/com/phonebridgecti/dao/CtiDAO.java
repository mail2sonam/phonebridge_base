/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridgecti.dao;

import com.mongodb.MongoClient;
import phonebridge.util.LogClass;
import phonebridge.util.util;
import com.phonebridgecti.db.CtiDB;
import phonebridge.postdata.CurlDataToURL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.bson.Document;
import org.bson.types.ObjectId;
import agentperformance.dao.AgentPerformanceDAO;
import campaignconfig.db.CampaignDB;
import campaignconfig.model.CampaignModel;
import campaignconfig.model.DependantForSave;
import campaignconfig.model.UserModel;
import com.mongodb.BasicDBList;
import com.phonebridge.callmodel.CallDao;
import com.settings.AdditionalSettings;
import com.settings.AdditionalSettingsDao;
import phonebridge.originate.OriginateRequestModel;
import phonebridge.postdata.CurlDataForAPI;
import processdbevents.db.ProcessAMIDB;
import processdbevents.db.ProcessForReportsDB;

/**
 *
 * @author bharath
 */
public class CtiDAO {
    private String callDirection;
    private String extension;
    private String phoneNumber;
    private String queueNumber;
    private String campaignID;
    private String serverNamePrefix;
    private String serverID;
    private String uniqueID;
    private String callDocID;
    private Date dateToUpdate;
    private String callStatus;
    private String prefix;
    private String callType;
    private String callContext;
    private MongoClient mongoConn;
    
    public CtiDAO(){
        
    }
    
    public CtiDAO(String callDirection,String extension,String phoneNumber,String queueNumber,
            String campaignID,String serverNamePrefix,String serverID,
            String uniqueID,String callDocID,Date dateToUpdate,String callStatus,MongoClient mongoConn,
            String prefix,String callType,String callContext){
        this.callDirection = callDirection;
        this.extension = extension;
        this.phoneNumber = new util().stripAdditionalInfoFromNumber(phoneNumber);//phoneNumber;
        this.queueNumber = queueNumber;
        this.campaignID = campaignID;
        this.serverNamePrefix = serverNamePrefix;
        this.serverID = serverID;
        this.uniqueID = uniqueID;
        this.callDocID = callDocID;
        this.dateToUpdate = dateToUpdate;
        this.callStatus = callStatus;
        this.prefix = prefix;
        this.callType = callType;
        this.callContext = callContext;
        this.mongoConn = mongoConn;
    }
    
    public void createPopup(){
        try{
            Document insertDoc = null;
            CampaignModel campaignDetails = null;
            String tempPhoneNumber = new util().stripAdditionalInfoFromNumber(phoneNumber);
            UserModel users = new UserDAO().checkExtensionExistInUsers(extension, mongoConn);
            if(users==null)
                return;
            extension = users.getExtension();
            String docID = null;
            String popupDocID = new UserDAO().checkIfPopUpObjectAlreadyExists(extension, tempPhoneNumber, callDirection, serverNamePrefix,mongoConn);
            boolean isDefault = false;
            switch(callDirection){
                case "outgoing":
                    /*
                    1) Check if Popup data already exists for specific extension and number
                    */
                    if(popupDocID==null){
                        if(campaignID!=null){
                            campaignDetails = new CampaignDAO().getCampaignDetailsForID(campaignID);
                            insertDoc = new CampaignDAO().checkAndGetDetailsForNumber(tempPhoneNumber,callDirection,
                                campaignDetails,extension,queueNumber,serverID,mongoConn);
                            if(insertDoc.isEmpty()){
                                isDefault = campaignDetails.isIsDefault();
                            }
                            docID = this.appendDataToDocAndInsert(campaignDetails,insertDoc,"Next Call",isDefault,false);
                            if(uniqueID!=null && callDocID!=null)
                                updateDetailsInPopupAndCall(docID);
                        }
                        else{
                            CampaignModel APICampaign = new CampaignDAO().getAllAPICampaigns(mongoConn, callDirection);
                            if(APICampaign==null){
                                CampaignModel popupInCRMCampaigns = new CampaignDAO().getAllPopupInCRMCampaigns(mongoConn, callDirection);
                                if(popupInCRMCampaigns==null){
                                    CampaignModel moduleLinkedCampaign = new CampaignDAO().getDefaultModuleLinkedCampaign(mongoConn,callDirection);
                                    if(moduleLinkedCampaign!=null){
                                        insertDoc = new CampaignDAO().checkAndGetDetailsForNumber(tempPhoneNumber,callDirection,
                                            moduleLinkedCampaign,extension,queueNumber,serverID,mongoConn);
                                        if(!insertDoc.isEmpty()){
                                            campaignDetails = moduleLinkedCampaign;
                                            isDefault = campaignDetails.isIsDefault();
                                        }
                                    }
                                }else{
                                insertDoc = new CampaignDAO().checkAndGetDetailsForNumber(tempPhoneNumber,callDirection,
                                    popupInCRMCampaigns,extension,queueNumber,serverID,mongoConn);
                                    if(!insertDoc.isEmpty()){
                                        campaignDetails = popupInCRMCampaigns;
                                        isDefault = campaignDetails.isIsDefault();
                                    }
                                }
                            }else{
                                insertDoc = new CampaignDAO().checkAndGetDetailsForNumber(tempPhoneNumber,callDirection,
                                    APICampaign,extension,queueNumber,serverID,mongoConn);
                                    if(!insertDoc.isEmpty()){
                                        campaignDetails = APICampaign;
                                        isDefault = campaignDetails.isIsDefault();
                                    }
                            }
                            if(insertDoc == null || insertDoc.isEmpty()){
                                campaignDetails = new CampaignDAO().getDefaultCampaignDetails(callDirection, mongoConn);
                                insertDoc = new Document();
                                BasicDBList dispositionData = new DispositionDAO().getDispositionAsBasicDBList(campaignDetails.getCampaignID(),mongoConn);
                                insertDoc.append("disposition", dispositionData);
                                insertDoc.append("newClientURL", campaignDetails.getNewClientURL());
                                isDefault = campaignDetails.isIsDefault();
                            }
                            if(!insertDoc.isEmpty()){
                                docID = this.appendDataToDocAndInsert(campaignDetails,insertDoc,"Dialing",isDefault,false);
                                if(uniqueID!=null && callDocID!=null)
                                    updateDetailsInPopupAndCall(docID);
                            }
                        }
                    }
                    else{
                        this.updateDetailsInPopupAndCall(popupDocID);
                        this.updateCallStatusAndTime();
                    }
                    break;
                case "incoming":
                    if(popupDocID==null){
                        CampaignModel APICampaign = new CampaignDAO().getAllAPICampaigns(mongoConn, callDirection);
                        if(APICampaign==null){
                            CampaignModel popupInCRMCampaigns = new CampaignDAO().getAllPopupInCRMCampaigns(mongoConn, callDirection);
                            if(popupInCRMCampaigns==null){
                                CampaignModel moduleLinkedCampaign = new CampaignDAO().getDefaultModuleLinkedCampaign(mongoConn,callDirection);
                                insertDoc = new CampaignDAO().checkAndGetDetailsForNumber(tempPhoneNumber,callDirection,
                                    moduleLinkedCampaign,extension,queueNumber,serverID,mongoConn);
                                if(!insertDoc.isEmpty()){
                                    campaignDetails = moduleLinkedCampaign;
                                    isDefault = campaignDetails.isIsDefault();
                                }
                            }else{
                                insertDoc = new CampaignDAO().checkAndGetDetailsForNumber(tempPhoneNumber,callDirection,
                                    popupInCRMCampaigns,extension,queueNumber,serverID,mongoConn);
                                if(!insertDoc.isEmpty()){
                                    campaignDetails = popupInCRMCampaigns;
                                    isDefault = campaignDetails.isIsDefault();
                                }
                            }
                        }else{
                            insertDoc = new CampaignDAO().checkAndGetDetailsForNumber(tempPhoneNumber,callDirection,
                                    APICampaign,extension,queueNumber,serverID,mongoConn);
                                if(!insertDoc.isEmpty()){
                                    campaignDetails = APICampaign;
                                    isDefault = campaignDetails.isIsDefault();
                                }
                        }
                        if( insertDoc == null || insertDoc.isEmpty()){
                            campaignDetails = new CampaignDAO().getDefaultCampaignDetails(callDirection, mongoConn);
                            insertDoc = new Document();
                            BasicDBList dispositionData = new DispositionDAO().getDispositionAsBasicDBList(campaignDetails.getCampaignID(),mongoConn);
                            insertDoc.append("disposition", dispositionData);
                            insertDoc.append("newClientURL", campaignDetails.getNewClientURL());
                            isDefault = campaignDetails.isIsDefault();
                        }
                        if(!insertDoc.isEmpty()){
                            docID = this.appendDataToDocAndInsert(campaignDetails,insertDoc,"Ringing",isDefault,false);
                            if(uniqueID!=null && callDocID!=null)
                                updateDetailsInPopupAndCall(docID);
                        }
                    }
                    else{
                        this.updateDetailsInPopupAndCall(popupDocID);
                        this.updateCallStatusAndTime();
                    }
                    break;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "createPopup", ex.getMessage());
        }
    }
    
    /*
    Update Different Call Status and corressponding time in Popup doc
    Curl data to extension
    If Hangup update hangup time in open popup collection(Auto Close)
    Delete Doc From Open Calls Collection(Live monitoring)
    */
    public void updateCallStatusAndTime(){
        try{
            String mmYYYY = new util().getMMYYYYForJavaDate(dateToUpdate.getTime());
            CtiDB ctiDb = new CtiDB();
            AdditionalSettings settings = new AdditionalSettingsDao().getCurrentSettings();
            String campaignID = new ProcessForReportsDB().getCampaignID(callDocID, mmYYYY, serverNamePrefix, mongoConn);
            CampaignModel campaign = new CampaignDB().getCampaignDetailsUsingID(campaignID);
            if(campaign.getCampaignSource().equals("apicampaign"))
            {
                if(callStatus.equals("Connected"))
                    new CurlDataForAPI(campaign.getResourceURL(), "apipopup", extension, callDocID, phoneNumber, callStatus,
                            callDirection, null, null, null, null, null, null, null, null, null, null, null, 0, 0,null,null,null,null).curl();
            }
            if(ctiDb.updateCallStatus(uniqueID,callDocID,callStatus, dateToUpdate,extension, serverNamePrefix, mongoConn)){
                if(campaign.getCampaignSource().equals("popupincrm"))
                    new CurlDataToURL(campaign.getResourceURL(), extension, "XYZUPDATECURRENTCALLINFO","crmpopup",callDocID,phoneNumber,callStatus,"").run();
                if(campaign.getCampaignSource().equals("base"))
                    new CurlDataToURL(settings.getPopupURL(),extension, "XYZUPDATECURRENTCALLINFO","popup",callDocID,phoneNumber,callStatus,"").run();
                if(campaign.getCampaignSource().equalsIgnoreCase("modulelinked") && campaign.getDialMethod().equalsIgnoreCase("missedcall"))
                    new CurlDataToURL(settings.getPopupURL(),extension, "XYZUPDATECURRENTCALLINFO","popup",callDocID,phoneNumber,callStatus,"").run();
                if(campaign.getDialMethod().equalsIgnoreCase("progressive") && campaign.getCampaignSource().equalsIgnoreCase("modulelinked"))
                    new CurlDataToURL(settings.getPopupURL(),extension, "XYZUPDATECURRENTCALLINFO","popup",callDocID,phoneNumber,callStatus,"").run();
                //new CurlDataToURL(extension, "XYZUPDATECURRENTCALLINFO","livemonitoring").run();
                if(callStatus.equals("HangUp")){
                    boolean deletePopup = ctiDb.checkIfPopupToBeDeleted(callDocID, mmYYYY, serverNamePrefix, mongoConn);
                    if(deletePopup){
                        String popupDocID = ctiDb.getPopupIDForCallID(callDocID, extension, serverNamePrefix, mongoConn);
                        this.deleteFromOpenPopup(popupDocID, ctiDb, mongoConn);
                        ctiDb.updatePopupFreeInUsers(extension, serverID, mongoConn);
                        ctiDb.deletePopupRecord(extension, serverNamePrefix,popupDocID, mongoConn);
                        ctiDb.deleteDocFromOpenCalls(popupDocID, serverNamePrefix,mongoConn);
                        /*if(campaign.getCampaignSource().equals("apicampaign"))
                            new CurlDataForAPI(campaign.getResourceURL(), "apipopup", extension, callDocID, phoneNumber, callStatus,
                                    callDirection, null, null, null, null, null, null, null, null, null, null, null, 0, 0).curl();*/
                        if(campaign.getCampaignSource().equals("popupincrm"))
                            new CurlDataToURL(campaign.getResourceURL(), extension, "XYZPOPUPCLOSED","crmpopup",callDocID,phoneNumber,callStatus,"").run();
                        else
                            new CurlDataToURL(settings.getPopupURL(),extension, "XYZPOPUPCLOSED","popup",callDocID,phoneNumber,callStatus,"").run();
                    }
                    else
                        ctiDb.updateHangUpTimeInOpenPopup(uniqueID, callDocID, dateToUpdate, mongoConn);
                }
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "updateCallStatus", ex.getMessage());
        }
    }
    
    /*
    Update CampaignID and Name in Calls
    */
    public void updateCmpgnIDNameTypeOfDialerAndCallType(String callDocID,HashMap campaignInfo,Date timeToUse,String custID){
        try{
            String mmYYYY = new util().getMMYYYYForJavaDate(timeToUse.getTime());
            new CtiDB().updateCmpgnIDNameTypeOfDialerAndCallType(callDocID,campaignInfo,custID, mmYYYY,serverNamePrefix,mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "updateCmpgnIDNameTypeOfDialerAndCallType", ex.getMessage());
        }
    }
    
    /*
    Append Additional Data To Popup Doc and unsert
    Curl New Popup to Extension
    Insert into open calls for live monitoring
    */
    public String appendDataToDocAndInsert(CampaignModel campaignDetails,Document insertDoc,String callStatus,boolean isDefault,
            boolean dialerCallOriginated){
        ObjectId docID = null;
        try{
            docID = new ObjectId();
            insertDoc = this.appendDataToDoc(campaignDetails,insertDoc, docID,callStatus, isDefault,dialerCallOriginated);
            new CtiDB().insertPopupObjTODB(insertDoc.getString("extension"), serverNamePrefix, insertDoc,mongoConn);
            AdditionalSettings settings = new AdditionalSettingsDao().getCurrentSettings();
            if(!campaignDetails.getCampaignSource().equals("externaldatasource")){
                if(campaignDetails.getCampaignSource().equals("apicampaign"))
                        new CurlDataForAPI(campaignDetails.getResourceURL(), "apipopup", extension, callDocID, phoneNumber, callStatus,
                                callDirection, null, null, null, null, null, null, null, null, null, null, null, 0, 0, null,null, null, null).curl();
                else if(campaignDetails.getCampaignSource().equals("popupincrm"))
                    new CurlDataToURL(campaignDetails.getResourceURL(), extension, "XYZSHOWONCALLINFO","crmpopup",callDocID,phoneNumber,callStatus,"").run();
                else
                    new CurlDataToURL(settings.getPopupURL(),extension, "XYZSHOWONCALLINFO","popup",callDocID,phoneNumber,callStatus,"").run();
            }
            new CtiDB().insertOpenCalls(extension,insertDoc,serverNamePrefix,mongoConn);
            new CurlDataToURL(settings.getPopupURL(),"admin", "XYZSHOWONCALLINFO","livemonitoring","",phoneNumber,callStatus,"").run();
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "appendDataToDocAndInsert", ex.getMessage());
        }
        return docID.toString();
    }
    
    /*
    Updating CallID(docID) and ami uniqueid in Popup DOc
    Updating CampaignID and CampaignName in Current Calls
    Inserting Data into Open Popup
    Adding Call Cnt in Agent Performance
    */
    public void updateDetailsInPopupAndCall(String popupID){
        try{
            new CtiDB().updateCallAndUniqueID(popupID, callDocID, uniqueID, extension, serverNamePrefix, mongoConn);
            HashMap<String,String> campaignInfo = new CampaignDAO().getCampaignIDNameTypeOfCalandListIDlFromPopup(popupID, serverNamePrefix, extension, mongoConn);
            String custID = new CtiDB().getCustIDFromPopup(popupID, extension, serverNamePrefix, mongoConn);
            if(campaignInfo.get("campaignID")!=null){
                this.updateCmpgnIDNameTypeOfDialerAndCallType(callDocID, campaignInfo, dateToUpdate,custID);
                this.insertOpenPopupDetails(popupID, campaignInfo.get("campaignName"),campaignInfo.get("campaignID"),custID,campaignInfo.get("listID"));
                new AgentPerformanceDAO("totalCallCnt", 0, extension, campaignInfo.get("campaignID"),serverNamePrefix,dateToUpdate,null,mongoConn).run();   
            }
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "updateDetailsInPopupAndCall", ex.getMessage());
        }
    }
    
    /*
    For Inserting Data For AutoClose Of Popup
    */
    private void insertOpenPopupDetails(String popupID,String campaignName,String campaignID,String custID,String listID){
        try{
            ObjectId tempCampaignID = new ObjectId(campaignID);
            int wrapUpTime = new CampaignDAO().getPopupWrapTimeFromCampaign(tempCampaignID, callDirection, mongoConn);
            String collectionName = new util().returnCollectionNameForPopup(serverNamePrefix, extension);
            new CtiDB().insertOpenPopupDetailsForAutoClose(popupID,wrapUpTime,extension,uniqueID,callDocID,serverID,serverNamePrefix, 
                dateToUpdate, custID, campaignID,campaignName,listID, mongoConn);
            
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "insertOpenPopupDetails", ex.getMessage());
        }
    }
    
    /*
    Appending Additional Data to Popup DOc
    */
    private Document appendDataToDoc(CampaignModel campaignDetails,Document insertDoc,ObjectId docID,String callStatus,boolean isDefault,
        boolean dialerCallOriginated){
        try{
            insertDoc.append("_id", docID);
            insertDoc.append("extension", extension);
            insertDoc.append("phoneNumber", phoneNumber);
            insertDoc.append("callDirection", callDirection);
            insertDoc.append("callStatus", callStatus);
            insertDoc.append("deleted", 0);
            insertDoc.append("isDefault", isDefault);
            insertDoc.append("typeOfDialer", campaignDetails.getDialMethod());
            insertDoc.append("prefix", prefix);
            insertDoc.append("listID", campaignDetails.getListID());
            insertDoc.append("serverID", new ObjectId(serverID));
            insertDoc.append("callType", callType);
            insertDoc.append("dialerCallOriginated", dialerCallOriginated);
            insertDoc.append("callContext", callContext);
            insertDoc.append("dateEntered", new util().generateNewDateInYYYYMMDDFormat());
            if(isDefault)
                insertDoc.append("startTime", dateToUpdate);
            if(campaignDetails!=null){
                insertDoc.append("campaignName", campaignDetails.getCampaignName());
                insertDoc.append("campaignID", new ObjectId(campaignDetails.getCampaignID()));
            }
            if(!insertDoc.containsKey("phone1"))
                insertDoc.append("phone1", phoneNumber);
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "appendDataToDoc", ex.getMessage());
        }
        return insertDoc;
    }
    
    public Date getHangUpTimeFromDoc(String callDocID,Date eventTime,String serverNamePrefix,MongoClient mongoConn){
        Date hangUpTime = null;
        try{
            hangUpTime = new CtiDB().getHangUpTimeFromDoc(callDocID, eventTime, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "getHangUpTimeFromDoc", ex.getMessage());
        }
        return hangUpTime;
    }
    
    public void updateWrapUpTime(String callDocID,String extension,String campaignID,String serverNamePrefix,
            MongoClient mongoConn){
        try{
            Date wrapUpTime = new util().generateNewDateInYYYYMMDDFormat("UTC");
            Date hangUpTime = this.getHangUpTimeFromDoc(callDocID, wrapUpTime, serverNamePrefix, mongoConn);
            double diffInSecs = new util().returnDiffBtwDateInSecsOrMinsOrHrs(wrapUpTime, hangUpTime, "secs");
            new CtiDB().updateWrapUpTime(callDocID, wrapUpTime,diffInSecs, serverNamePrefix, mongoConn);
            new AgentPerformanceDAO("totalWrapTime", diffInSecs, extension, campaignID, serverNamePrefix,hangUpTime,null,mongoConn).run();
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "updateWrapUpTime", ex.getMessage());
        }
    }
    
    public ArrayList<HashMap> getClosePopupDetails(String serverID,CtiDB ctiDb,MongoClient mongoConn){
        ArrayList<HashMap> closePopupDetails = new ArrayList<>();
        try{
            closePopupDetails = ctiDb.getClosePopupDetails(serverID, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "getClosePopupDetails", ex.getMessage());
        }
        return closePopupDetails;
    }
    
    
    
    public void deleteFromOpenPopup(String popupDocID,CtiDB ctiDb,MongoClient mongoConn){
        try{
            ctiDb.deleteFromOpenPopup(popupDocID, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "deleteFromOpenPopup", ex.getMessage());
        }
    }
    
    public void saveCti(String callID,String disposition,ArrayList<DependantForSave> dependent,String comments,String mmYYYY,
        String serverNamePrefix,String campaignID,String listID,String campaignName,String custID,String leadStatus,String extension,String serverID,
        String popupDocID,String nextNoField,Date followUpDate,String phoneNumber,String customerName,
        String phone1,String phone2,String phone3,String currentCallType,boolean conversion,MongoClient mongoConn) {
        try{
            CtiDB ctiDb = new CtiDB();
            String callCollectionName = new util().returnCollectionNameForCurrentCalls(mmYYYY,serverNamePrefix);
            Date startDate = ctiDb.insertDispoDependantInCalls(callID,disposition,dependent,comments, callCollectionName,extension,campaignID,serverNamePrefix, mongoConn);
            this.updateWrapUpTime(callID, extension, campaignID, serverNamePrefix, mongoConn);
            this.deleteFromOpenPopup(popupDocID, ctiDb, mongoConn);
            ctiDb.updatePopupFreeInUsers(extension, serverID, mongoConn);
            ctiDb.deletePopupRecord(extension, serverNamePrefix,popupDocID, mongoConn);
            ctiDb.deleteDocFromOpenCalls(popupDocID, serverNamePrefix,mongoConn);
            if(custID!=null && new ProcessAMIDB().checkIfObjectIdIsValid(custID)){
                if(nextNoField!=null){
                    ctiDb.updateTryNextNoInList(custID, listID, nextNoField, mongoConn);
                }
                ctiDb.updateCallHistoryInList(callID, callCollectionName,listID,custID,startDate,leadStatus, mongoConn);
                try{
                    if(currentCallType!=null && currentCallType.equals("CallBack Call")){
                        ctiDb.deleteOldCallBackEntryForNumberAndCampaign(phoneNumber, campaignID, extension, serverNamePrefix, mongoConn);
                    }
                    else if(disposition.equals("Wrap Up Time Exceeded") && ctiDb.checkIfCallBackCall(extension, serverNamePrefix, popupDocID, mongoConn)){
                        ctiDb.deleteOldCallBackEntryForNumberAndCampaign(phoneNumber, campaignID, extension, serverNamePrefix, mongoConn);
                    }
                }
                catch(NullPointerException nexc){
                
                }
                if(leadStatus.equals("Call Back")){
                    ctiDb.insertCallBackForCall(custID, campaignID, followUpDate, extension, phoneNumber, serverNamePrefix,
                        customerName,phone1,phone2,phone3,disposition,comments,listID,callID,campaignName,mongoConn);
                }
            }
            if(conversion)
                new AgentPerformanceDAO("conversionDisposition", 0.00, extension, campaignID, serverNamePrefix, null, disposition, mongoConn).run();
            if(callID!=null)
                new CallDao().callBackURLForSendingPopupData(callID, serverNamePrefix, mmYYYY);
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "saveCti", ex.getMessage());
        }
    }
    
    public ArrayList<OriginateRequestModel> getNumbersToOriginate(String serverNamePrefix,CtiDB ctiDb,MongoClient mongoConn){
        ArrayList<OriginateRequestModel> numbersToOriginate = new ArrayList<>();
        try{
            numbersToOriginate = ctiDb.getNumbersToOriginate(serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDB", "getNumbersToOriginate", ex.getMessage());
        }
        return numbersToOriginate;
    }
    
    public void updateCallInitiated(String docID,String serverNamePrefix,CtiDB ctiDb,MongoClient mongoConn){
        try{
            ctiDb.updateCallInitiated(docID, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "updateCallInitiated", ex.getMessage());
        }
    }
    
    public ArrayList<HashMap> getNumbersNotAttendedByAgent(String serverNamePrefix,CtiDB ctiDb,MongoClient mongoConn){
        ArrayList<HashMap> numbersNotAttendedByAgent = new ArrayList<>();
        try{
            numbersNotAttendedByAgent = ctiDb.getNumbersNotAttendedByAgent(serverNamePrefix, ctiDb, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "getNumbersNotAttendedByAgent", ex.getMessage());
        }
        return numbersNotAttendedByAgent;
    }
    
    public void updateHasAgentAnsweredInOriginateCall(String extension,String phoneNumber,String updateVal,
            String serverNamePrefix,CtiDB ctiDb,MongoClient mongoConn){
        try{
            ctiDb.updateHasAgentAnsweredInOriginateCall(extension, phoneNumber,updateVal, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "updateHasAgentAnsweredInOriginateCall", ex.getMessage());
        }
    }
    
    public void updateLeadStatusInList(String listID,String docID,CtiDB ctiDb,MongoClient mongoConn){
        try{
            ctiDb.updateLeadStatusInList(listID, docID, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "updateLeadStatusInList", ex.getMessage());
       
 }    }
    
    public void deletePopupIfExistsDuringNextCall(String phoneNumber,String extension,String serverNamePrefix,
        MongoClient mongoConn){
        try{
            new CtiDB().deletePopupIfExistsDuringNextCall(phoneNumber, extension, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("CtiDAO", "deletePopupIfExistsDuringNextCall", ex.getMessage());
        }
    }
    
}
