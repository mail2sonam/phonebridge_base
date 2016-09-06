/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridge.missedcall.dao;

import campaignconfig.model.CampaignModel;
import com.mongodb.MongoClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.joda.time.DateTime;
import phonebridge.missedcall.model.MissedCallRequestModel;
import phonebridge.originate.OriginateRequestModel;
import phonebridge.missedcall.db.MissedCallDB;
import phonebridge.sms.SMSDao;
import phonebridge.util.util;
import processdbevents.model.CdrModel;
import processdbevents.model.HangupModel;

/**
 *
 * @author venky
 */
public class MissedCallDAO {
    public ArrayList<MissedCallRequestModel> getAllMissedCallReq(String listID,MissedCallDB missedCallDb,String branchID,MongoClient mongoConn){
        return missedCallDb.getAllMissedCallRequest(listID,branchID,mongoConn);
    }
    
    public ArrayList<MissedCallRequestModel> getAllRetryMissedCallReq(String listID,MissedCallDB missedCallDb,String branchID,MongoClient mongoConn){
        return missedCallDb.getAllRetryMissedCallRequest(listID,branchID,mongoConn);
    }
    
    public boolean checkIfAlreadyCalled(Date timeToCheck,String serverPrefix,String phoneNumber,MongoClient mongoConn){
        boolean alreadySpoken = false;
        alreadySpoken = new MissedCallDB().checkIfAlreadyCalled(timeToCheck, serverPrefix, phoneNumber, mongoConn);
        return alreadySpoken;
    }
    
    public HashMap getNoOfAgentAndClientTries(String listID,String originateReqID,MongoClient mongoConn){
        HashMap<String,Integer> noOfTries = new HashMap<>();
        try{
            noOfTries = new MissedCallDB().getNoOfAgentAndClientTries(listID, originateReqID, mongoConn);
        }
        catch(Exception ex){
            
        }
        return noOfTries;
    }
    
    public void originateCall(String prefix, String phoneNumber,String userExtension, String typeOfDialer, 
        String serverNamePrefix,int delayBeforeCall,String popupObjectID,String campaignID,String dialOutContext,
        String campaignName,String customerID,boolean followMeExists,String callID,MissedCallDB missedCallDb,
        String extensionType,String followNumber){
        try{
            missedCallDb.originateCall(prefix, phoneNumber, userExtension, typeOfDialer, serverNamePrefix, delayBeforeCall, 
                popupObjectID, campaignID, dialOutContext, campaignName, customerID,followMeExists,callID,extensionType,followNumber);
        }
        catch(Exception ex){
            ex.getMessage();
        }
    }
    
    public String getExtensionToOrignateMissedCall(String lastTriedExtension,MissedCallDB missedCallDb,String campaignID,String branchID,MongoClient mongoConn){
        String extension = null;
        try{
            ArrayList<String> extensionDetails = missedCallDb.getExtensionToOrignateMissedCall(campaignID,branchID,mongoConn);
            
            for(String eachExt : extensionDetails){
                //change by venky
                if(lastTriedExtension==null || lastTriedExtension.length()==0){
                    extension = eachExt;
                }
                //End
                if(eachExt.equals(lastTriedExtension)){//Checking if current extension in loop equals last tried extension
                    //If satisfied, checking if the last tried extension is in the last position of the array,
                    // so as to get the first element from the array
                    if(lastTriedExtension.equals(extensionDetails.get(extensionDetails.size()-1)))
                    {
                        extension = extensionDetails.get(0);
                    }
                    // Else adding 1 to the current index and getting the value
                    else
                    {
                        int indexOfCurrentExt = extensionDetails.indexOf(eachExt);
                        extension = extensionDetails.get(indexOfCurrentExt+1);
                    }
                }
                if(extension!=null)
                    break;
            }
        }
        catch(Exception ex){
            ex.getMessage();
        }
        return extension;
    }
        
    public void incrementAgentOrClientRetry(String fieldToInc,String listID,String missedCallID,MongoClient mongoConn){
        new MissedCallDB().incrementAgentOrClientRetry(fieldToInc, listID, missedCallID, mongoConn);
    }
    
    public void updateMissedCallStatusBasedOnCdr(CampaignModel campaignDetails,CdrModel cdr,String originateReqID,
        String cdrType,OriginateRequestModel detailsToOriginate,MongoClient mongoConn){
        try{
            MissedCallDB missedCallDb = new MissedCallDB();
            if(!cdr.getDisposition().equals("ANSWERED") || 
                cdr.getBillableSeconds()==0 || cdr.getAnswerTime()==null || 
                    cdr.getLastApplication().equalsIgnoreCase("Congestion")){
                this.updateRetriesAndStatusInMissedCall(campaignDetails,cdrType,originateReqID,detailsToOriginate,mongoConn);
            }
            else if(cdr.getDisposition().equals("ANSWERED") && (cdr.getBillableSeconds()>0 || cdr.getAnswerTime()!=null) && cdrType.equals("Customer")){
                missedCallDb.updateMissedCallReqStatus(campaignDetails.getListID(),originateReqID, detailsToOriginate.getUserExtension(), "Completed"
                    , "Call Answered",null,mongoConn);
            }
        }
        catch(Exception ex){
            
        }
    }
    
    public void deleteRecFromOriginateCallWithID(String originateCallID,String serverNamePrefix,MongoClient mongoConn){
        new MissedCallDB().deleteRecFromOriginateCallWithID(originateCallID, serverNamePrefix, mongoConn);
    }
    
    public String getOriginateReqIDUsingPhoneNumber(String listID,String phoneNumber,String prefix,MongoClient mongoConn){
        String originateReqID = null;
        try{
            originateReqID = new MissedCallDB().getOriginateReqIDUsingPhoneNumber(listID, phoneNumber, prefix, mongoConn);
        }
        catch(Exception ex){
            
        }
        return originateReqID;
    }
    
    public void updateRetriesAndStatusInMissedCall(CampaignModel campaignDetails,String whichSideEvent,String originateReqID,
        OriginateRequestModel detailsFromOriginate,MongoClient mongoConn){
        try{
            MissedCallDB missedCallDb = new MissedCallDB();
            HashMap<String,Integer> noOfTries = new MissedCallDAO().getNoOfAgentAndClientTries(campaignDetails.getListID(),originateReqID,mongoConn);    
            switch(whichSideEvent){
                case "Customer":
                    if(campaignDetails.getNoOfClientTries()>noOfTries.get("clientTries")){
                        DateTime retryOn = new DateTime().plusMinutes(campaignDetails.getRetryAfter());
                        this.incrementAgentOrClientRetry("noOfClientTries", campaignDetails.getListID(), originateReqID, mongoConn);
                        missedCallDb.updateMissedCallReqStatus(campaignDetails.getListID(),originateReqID, null, "Not Initiated", null,retryOn.toDate(),mongoConn);
                    }
                    else
                        missedCallDb.updateMissedCallReqStatus(campaignDetails.getListID(),originateReqID,null, "Completed","Customer Retry Exceeded",null,mongoConn);
                    break;
                case "Agent":
                    if(campaignDetails.getNoOfAgentTries()>noOfTries.get("agentTries")){
                        new MissedCallDAO().incrementAgentOrClientRetry("noOfAgentTries", campaignDetails.getListID(), originateReqID, mongoConn);
                        missedCallDb.updateMissedCallReqStatus(campaignDetails.getListID(),originateReqID, null, "Not Initiated", null,null,mongoConn);
                    }
                    else
                        missedCallDb.updateMissedCallReqStatus(campaignDetails.getListID(),originateReqID,null, "Completed","Agent Retry Exceeded",null,mongoConn);    
                    break;
            }
        }
        catch(Exception ex){
            
        }
    }
    
    public boolean checkIfExtensionFreeFromCurrentCalls(String extension,String serverNamePrefix,MongoClient mongoConn){
        boolean extensionFree = false;
        try{
            extensionFree = new MissedCallDB().checkIfExtensionFreeFromCurrentCalls(extension, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            
        }
        return extensionFree;
    }
    
    public ArrayList<String> getTotalExtensionsForMissedCallCampaign(String campaignID,String branchID,MongoClient mongoConn){
        return new MissedCallDB().getTotalExtensionsForMissedCallCampaign(campaignID,branchID, mongoConn);
    }
    
    /*
        1) Used to get the extension sorted in order of attended call count to route the call to the least 
            called extension
    */
    public ArrayList<String> getExtensionBasedOnNoOfCallsAnswered(String listID,String branchID,
            ArrayList<String> extensionsToCheck,MongoClient mongoConn){
        return new MissedCallDB().getExtensionBasedOnNoOfCallsAnswered(listID, branchID,extensionsToCheck,mongoConn);
    }
    
    /*
        1) Check if cdr belongs to a missed call given by the customer
        2) If so, then insert request, if a request is not already pending
    */
    public boolean checkIfMissedCallCdr(CdrModel cdr,String mmYYYY,String serverID,String serverNamePrefix,
        MongoClient mongoConn){
        return new MissedCallDB().checkIfMissedCallCdr(cdr, mmYYYY, serverID, serverNamePrefix, mongoConn);
    }
    
    /*
        Added By Bharath 17/04/2016
        Added to integrate both mccb and bccb in hangup
        Function tests whether the hangup belongs to a missed call and processes it
        Function is called only when the hangup is not processed in bccb
    */
    public boolean checkIfMissedCallHangUp(HangupModel hangup,String mmYYYY,String serverID,String serverNamePrefix,
        MongoClient mongoConn){
        System.out.println("ENTERING MCCB Identification Function");
        boolean missedCallHangUp = false;
        MissedCallDB missedCallDB = new MissedCallDB();
        try{
            String phoneNumber = null;
            ArrayList<CampaignModel> missedCallCampaigns = missedCallDB.getAllMissedCallCampaigns(mongoConn);
            for(CampaignModel campaignDetails : missedCallCampaigns){
                /*
                    Added try catch because, trunk value from campaign might be null if we use mode as DID in campaign
                */
                try{
                    System.out.println("MCCB - Comparing Hangup Channel "+hangup.getChannel()+" and Trunk from Campaign "+campaignDetails.getTrunkValue());
                    if(hangup.getChannel().contains(campaignDetails.getTrunkValue())){
                        phoneNumber = new util().stripAdditionalInfoFromNumber(hangup.getCallerIdName());
                        if(phoneNumber.matches("-?\\d+(\\.\\d+)?")){
                            System.out.println("MCCB - Hangup Channel and Trunk from Campaign Matched. So considering MCCB");
                            missedCallHangUp = true;
                            /*
                                CallerIDName key in hangup consists of phonenumber
                                So taking callerIdName for phoneNumber
                            */
                            missedCallDB.checkAndInsertForMissedCall(campaignDetails, phoneNumber, mongoConn);
                            new SMSDao().insertMissedCallsmsData(phoneNumber, "", campaignDetails, "ReceivedMissedCall",serverNamePrefix);
                        }
                    }
                }
                catch(Exception ex){
                    System.out.println("MCCB - Exception while checking Channel in Hangup and trunk from campaign");
                }
            }
        }
        catch(Exception ex){
            
        }
        return missedCallHangUp;
    }
    
    public void updateLastExtensionTriedInMissedCall(String listID,String missedCallID,String extensionTried,MongoClient mongoConn){
        new MissedCallDB().updateLastExtensionTriedInMissedCall(listID, missedCallID, extensionTried, mongoConn);
    }
}
