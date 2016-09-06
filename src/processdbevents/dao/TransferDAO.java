/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import processdbevents.model.TransferModel;
import phonebridge.util.LogClass;
import org.bson.Document;

/**
 *
 * @author bharath
 */
public class TransferDAO {
    public TransferModel createTransferObject(Document transferEvents){
        TransferModel transfer = new TransferModel();
        try{
            transfer.setEventName(transferEvents.getString("event"));
            transfer.setPrivilege(transferEvents.getString("privilege"));
            transfer.setTranferMethod(transferEvents.getString("transfermethod"));
            transfer.setTranferType(transferEvents.getString("transfertype"));
            transfer.setChannel(transferEvents.getString("channel"));
            transfer.setUniqueID(transferEvents.getString("uniqueid"));
            transfer.setSipCallId(transferEvents.getString("sip-callid"));
            transfer.setTargetChannel(transferEvents.getString("targetchannel"));
            transfer.setTargetUniqueId(transferEvents.getString("targetuniqueid"));
            transfer.setTransferExten(transferEvents.getString("transferexten"));
            transfer.setTransferContext(transferEvents.getString("transfercontext"));
            transfer.setEventTime(transferEvents.getDate("eventtime"));
        }
        catch(Exception ex){
            LogClass.logMsg("TransferDAO", "createTransferObject", ex.getMessage());
        }
        return transfer;
    }
}
