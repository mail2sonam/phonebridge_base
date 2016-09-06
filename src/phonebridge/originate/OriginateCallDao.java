/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridge.originate;

import campaignconfig.model.CampaignModel;
import campaignconfig.model.UserModel;
import com.mongodb.MongoClient;
import phonebridgelogger.model.Server;

/**
 *
 * @author sonamuthu
 */
public class OriginateCallDao {
    public OriginateRequestModel getDetailsFromOriginateCollForID(String originateID,String serverNamePrefix,MongoClient mongoConn){
        OriginateRequestModel detailsFromOriginate = null;
        detailsFromOriginate = new OriginateCallDb().getDetailsFromOriginateCollForID(originateID, serverNamePrefix, mongoConn);
        return detailsFromOriginate;
    }
    
    public String insertOriginateCallReq(Server serverDetails,String phoneNumber,String extension,
        CampaignModel campaignModel,String popupID,String custID,UserModel user){
        return new OriginateCallDb().insertOriginateCallReq(serverDetails, phoneNumber, extension,campaignModel, popupID, custID, user);
    }
}
