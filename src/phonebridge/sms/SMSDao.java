/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridge.sms;

import campaignconfig.model.CampaignModel;
import com.settings.AdditionalSettings;
import com.settings.AdditionalSettingsDao;
import java.util.ArrayList;

/**
 *
 * @author venky
 */
public class SMSDao {
    public void insertSMSData(String phoneNumber,String smsContent,String serverNamePrefix){
        new SmsDB().insertSMSDetails(phoneNumber, smsContent, "After Call SMS", serverNamePrefix);
    }
    
    public void insertMissedCallsmsData(String phoneNumber,String whichSideEvent,CampaignModel campaignDetails,String hangUpType,String serverNamePrefix){
        ArrayList<SMSModel> allSMSTemplates = new SmsDB().getAllSMSTemplates(campaignDetails.getCampaignID());
        for(SMSModel smsTemplate:allSMSTemplates)
        {
            if(whichSideEvent.equals("Agent") && hangUpType.equals("NotAnswered") && smsTemplate.getTriggerOn().equals("AgentNotAnswered"))
                new SmsDB().insertMissedCallSMSData(phoneNumber, campaignDetails.getListID(),false,smsTemplate,serverNamePrefix);
            if(hangUpType.equals(smsTemplate.getTriggerOn()))
                new SmsDB().insertMissedCallSMSData(phoneNumber, campaignDetails.getListID(),false,smsTemplate,serverNamePrefix);
            
        }    
    }
}
