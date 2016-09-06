/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import processdbevents.model.HangupModel;
import phonebridge.util.LogClass;
import org.bson.Document;

/**
 *
 * @author bharath
 */
public class HangUpDAO {
    public HangupModel createHangupObject(Document hangUpEvents){
        HangupModel hangUp = new HangupModel();
        try{
            hangUp.setEventName(hangUpEvents.getString("event"));
            hangUp.setPrivilege(hangUpEvents.getString("privilege"));
            hangUp.setChannel(hangUpEvents.getString("channel"));
            hangUp.setUniqueID(hangUpEvents.getString("uniqueid"));
            hangUp.setCallerIdNum(hangUpEvents.getString("calleridnum"));
            hangUp.setCallerIdName(hangUpEvents.getString("calleridname"));
            hangUp.setConnectedLineNum(hangUpEvents.getString("connectedlinenum"));
            hangUp.setConnectedLineName(hangUpEvents.getString("connectedlinename"));
            hangUp.setAccountCode(hangUpEvents.getString("accountcode"));
            hangUp.setCause(hangUpEvents.getString("cause"));
            hangUp.setCauseTxt(hangUpEvents.getString("cause-txt"));
            hangUp.setEventTime(hangUpEvents.getDate("eventtime"));
        }
        catch(Exception ex){
            LogClass.logMsg("HangUpDAO", "createHangupObject", ex.getMessage());
        }
        return hangUp;
    }
    
    public HangupModel createHangupObjectFromCurrentCalls(Document hangUpEvents){
        HangupModel hangUp = new HangupModel();
        try{
            hangUp.setEventName(hangUpEvents.getString("eventName"));
            hangUp.setPrivilege(hangUpEvents.getString("privilege"));
            hangUp.setChannel(hangUpEvents.getString("channel"));
            hangUp.setUniqueID(hangUpEvents.getString("uniqueID"));
            hangUp.setCallerIdNum(hangUpEvents.getString("callerIdNum"));
            hangUp.setCallerIdName(hangUpEvents.getString("callerIdName"));
            hangUp.setConnectedLineNum(hangUpEvents.getString("connectedLineNum"));
            hangUp.setConnectedLineName(hangUpEvents.getString("connectedLineName"));
            hangUp.setAccountCode(hangUpEvents.getString("accountCode"));
            hangUp.setCause(hangUpEvents.getString("cause"));
            hangUp.setCauseTxt(hangUpEvents.getString("causeTxt"));
            hangUp.setEventTime(hangUpEvents.getDate("eventTime"));
        }
        catch(Exception ex){
            LogClass.logMsg("HangUpDAO", "createHangupObject", ex.getMessage());
        }
        return hangUp;
    }
}
