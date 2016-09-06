/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridge.callmodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.settings.AdditionalSettings;
import com.settings.AdditionalSettingsDao;
import phonebridge.postdata.SendCallDataToCRM;

/**
 *
 * @author bharath
 */
public class CallDao {
    public CallModel getCallDataForId(String callId,String serverNamePrefix,String mmYYYY,String serverId){
        CallModel callDetails = null;
        try{
            callDetails = new CallsDb().getCallDataForId(callId, serverNamePrefix, mmYYYY);
            if(callDetails!=null)
                callDetails.setServerId(serverId);
        }
        catch(Exception ex){
            System.out.println("ERROR IN GETTING CALL DETAILS FOR CRM CURL "+ex.getMessage());
        }
        return callDetails;
    }
    
    public CallModel getDispositionAndDependantForId(String callId,String serverNamePrefix,String mmYYYY){
        CallModel callDetails = null;
        try{
            callDetails = new CallsDb().getDispositionAndDependantData(callId, mmYYYY, serverNamePrefix);
        }
        catch(Exception ex){
            System.out.println("ERROR IN GETTING DISPOSITION AND DEPENDANT DETAILS DETAILS FOR CRM CURL "+ex.getMessage());
        }
        return callDetails;
    }
    
    public void callBackURL(CallModel callDetails,String serverNamePrefix,String mmYYYY,String serverId){
        String jsonInString = null;
        AdditionalSettings settings = new AdditionalSettingsDao().getCurrentSettings();
        if(settings.getCallBackURL()==null || settings.getCallBackURL().length()<1)
            return;
        if(callDetails!=null){
            ObjectMapper mapper = new ObjectMapper();
            try{
                jsonInString = mapper.writeValueAsString(callDetails);
                if(jsonInString!=null){
                    System.out.println("JSON TO SEND "+jsonInString);
                    new SendCallDataToCRM(jsonInString.trim(),settings.getCallBackURL(),callDetails.getCallId(), serverNamePrefix, mmYYYY).run();
                }
            } 
            catch(JsonProcessingException ex){
                System.out.println("ERROR CONVERTING CALL DETAILS TO JSON STRING "+ex.getMessage());
            }            
        }
        if(callDetails==null || jsonInString==null){
            String status = "FAILED: CALLDETAILS NULL OR ERROR IN JSON CONVERSION";
            new CallsDb().updateCurlStatus(mmYYYY, callDetails.getCallId(), serverNamePrefix, mmYYYY);
        }
    }
    
    public void callBackURLForSendingPopupData(String callId,String serverNamePrefix,String mmYYYY){
        AdditionalSettings settings = new AdditionalSettingsDao().getCurrentSettings();
        if(settings.getPopupDataUpdateURL()==null || settings.getPopupDataUpdateURL().length()<1)
            return;
        CallModel callDetails = this.getDispositionAndDependantForId(callId, serverNamePrefix, mmYYYY);
        String jsonInString = null;
        if(callDetails!=null){
            ObjectMapper mapper = new ObjectMapper();
            try{
                jsonInString = mapper.writeValueAsString(callDetails);
                if(jsonInString!=null){
                    System.out.println("JSON TO SEND "+jsonInString);
                    new SendCallDataToCRM(jsonInString.trim(),settings.getPopupDataUpdateURL(),callId, serverNamePrefix, mmYYYY).run();
                }
            } 
            catch(JsonProcessingException ex){
                System.out.println("ERROR CONVERTING CALL DETAILS TO JSON STRING "+ex.getMessage());
            }            
        }
        if(callDetails==null || jsonInString==null){
            String status = "FAILED: CALLDETAILS NULL OR ERROR IN JSON CONVERSION";
            new CallsDb().updateCurlStatus(mmYYYY, callId, serverNamePrefix, mmYYYY);
        }
    }
}
