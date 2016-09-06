/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridgecti.dao;

import com.mongodb.MongoClient;
import com.phonebridgecti.db.CheckCampaignActiveDB;
import phonebridgelogger.model.Server;
import campaignconfig.model.CampaignModel;
import campaignconfig.model.DisplayCallModel;

/**
 *
 * @author bharath
 */
public class CheckCampActivewitoutThread {
    private String extension;
    private Server serverDetails;
    boolean userStatus;
    private String prefix;
    private CampaignModel campDetails;
    private String mobileNumber; 
    private int delayForCall;
    private String callType;
    private MongoClient mongoConn;
    private String callContext;
    private String MMYYYY;
    
    public CheckCampActivewitoutThread(String extension,Server serverDetails,String prefix,String MMYYYY,MongoClient mongoConn){
        this.extension = extension;
        this.serverDetails = serverDetails;
        this.mongoConn = mongoConn;
        this.prefix = prefix;
        this.MMYYYY = MMYYYY;
    }
    
    public boolean getOriginateDetails(){
        CheckCampaignActiveDB checkCampaignActiveDB = new CheckCampaignActiveDB();
        userStatus = checkCampaignActiveDB.checkUserAvailablityForDialer(serverDetails.getServerID(), extension,mongoConn);
        boolean extensionStatusFree = checkCampaignActiveDB.checkIfExtensionFree(extension,serverDetails.getServerID(),mongoConn);
        if(!extensionStatusFree)
            extensionStatusFree = !(checkCampaignActiveDB.hasOpenCallsInCurrentCallsForExtension(extension, serverDetails.getServerNamePrefix(), mongoConn));
        if(!userStatus || !extensionStatusFree)
            return false;
        
        DisplayCallModel callBackDetails = new CheckCampaignActiveDB().getMobileNumberCallBackForPDialer(serverDetails.getServerNamePrefix(), extension, mongoConn);
        if(callBackDetails!=null){
            mobileNumber = callBackDetails.getPhone1();
            campDetails = new CampaignDAO().getCampaignDetailsForID(callBackDetails.getCampaignID());
            callType = "CallBack Call";
        }
        
        if(mobileNumber==null)
            campDetails = new CheckCampaignActiveDB().getActiveProgressiveCampaignForExtension(extension,serverDetails, mongoConn);

        if(campDetails==null)
            return false;
        
        callContext = new CheckCampaignActiveDB().getContextForPDialer(extension, mongoConn);
        if(callContext==null || callContext.length()==0){
            callContext = serverDetails.getDialOutContext();
        }
        
        /*
            Added By Bharath on 23/04/2016
            For dynamic campaign for aquapure
            Adding additional IF condition
            Variable added for preventing additional DB processing if no number returned
            PopupIframeURL will contain the url for querying number for the campaign
        */
        //>>>>>>START<<<<<<<<<
        boolean crmDialerCampaign = false;
        if(mobileNumber==null && 
            "modulelinked".equalsIgnoreCase(campDetails.getCampaignSource()) &&
            "progressive".equalsIgnoreCase(campDetails.getDialMethod())){
            mobileNumber = new CRMDialerCampaignSr().getClientDetailsFromRestServiceForCRMDialerCampaign(
                campDetails.getCampaignName(), campDetails.getPopupIFrameUrl());
            crmDialerCampaign = true;
        }
        //>>>>>>>END<<<<<<<<<<
        /*
            Changed By Bharath on 23/04/2016
            Adding another boolean check in the following IF conditions
            Adding to prevent querying DB if it CRMDialerCampaign and no number was returned
        */
        if(mobileNumber==null && !crmDialerCampaign){
            mobileNumber = new CheckCampaignActiveDB().getMobileNoForTryNextNoPDialer(campDetails.getListID(),extension,campDetails.isIsGeneralPolling(),mongoConn);
            callType = "Trying Another Number";
        }
        if(mobileNumber==null && !crmDialerCampaign){
            mobileNumber = new CheckCampaignActiveDB().getMobileNoForTryAgainPDialer(campDetails.getListID(),extension,campDetails.isIsGeneralPolling(),mongoConn);
            callType = "Trying Again";
        }
        if(mobileNumber==null && !crmDialerCampaign){
            mobileNumber = new CheckCampaignActiveDB().getMobileNoForOpenedPDialer(campDetails.getListID(),extension,campDetails.isIsGeneralPolling(),mongoConn);
            callType = "Fresh Call";
        }
        if(mobileNumber==null && !crmDialerCampaign){
            mobileNumber = new CheckCampaignActiveDB().getMobileNoForTryAgainLaterPDialer(campDetails.getListID(),extension,campDetails.isIsGeneralPolling(), mongoConn);
            callType = "Try Again Later";
        }
        if(mobileNumber==null)
            return false;
        delayForCall = new CheckCampaignActiveDB().getDelaybeforeCallForPDialer(campDetails.getCampaignID(),mongoConn);
        new CtiDAO("outgoing",extension,mobileNumber,null,campDetails.getCampaignID(),
            serverDetails.getServerNamePrefix(), serverDetails.getServerID(),null, null, null, null, mongoConn,
            prefix,callType,callContext).createPopup();
        return true;
    }  
}
