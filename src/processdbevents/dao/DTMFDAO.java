/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import processdbevents.model.DTMFModel;
import phonebridge.util.LogClass;
import org.bson.Document;

/**
 *
 * @author bharath
 */
public class DTMFDAO {
    public DTMFModel createDTMFObject(Document dtmfEvents){
        DTMFModel dtmf = new DTMFModel();
        try{
            dtmf.setEventName(dtmfEvents.getString("event"));
            dtmf.setPrivilege(dtmfEvents.getString("privilege"));
            dtmf.setChannel(dtmfEvents.getString("channel"));
            dtmf.setUniqueID(dtmfEvents.getString("uniqueid"));
            dtmf.setDigit(dtmfEvents.getString("digit"));
            dtmf.setDirection(dtmfEvents.getString("direction"));
            dtmf.setBegin(dtmfEvents.getString("begin"));
            dtmf.setEnd(dtmfEvents.getString("end"));
            dtmf.setEventTime(dtmfEvents.getDate("eventtime"));
        }
        catch(Exception ex){
            LogClass.logMsg("DTMFDAO", "createDTMFObject", ex.getMessage());
        }
        return dtmf;
    }
}
