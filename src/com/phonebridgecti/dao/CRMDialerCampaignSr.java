/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.phonebridgecti.dao;

import phonebridge.postdata.CurlDataToURL;

/**
 *
 * @author bharath
 */
public class CRMDialerCampaignSr {
    /*
        Added By Bharath 0n 2404/2016
        Will call a REST service in aquapure CRM
        @Param CampaignName
        @Param ResourceURL - RESTful URL
        @Response REST will return a String containing phoneNumber
    */
    public String getClientDetailsFromRestServiceForCRMDialerCampaign(String campaignName,String iFrameURL){
        /*
            By Bharath on 23/04/2016
            Concatenating URL with campaignName as PathVariable
            Concatenating here because, need to add another variable in CurlDataToURL class if 
            we need to pass there and concat, which will create error in other places
        */
        iFrameURL = iFrameURL.replace("{campaignName}",campaignName);
        String phoneNumber = new CurlDataToURL(iFrameURL, null, null,"crmdialercampaign", null, null, null, null).run();
        return phoneNumber;
    }
}
