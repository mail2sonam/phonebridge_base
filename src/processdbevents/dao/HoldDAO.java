/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import processdbevents.model.HoldModel;
import phonebridge.util.LogClass;
import org.bson.Document;

/**
 *
 * @author bharath
 */
public class HoldDAO {
    public HoldModel createHoldObject(Document holdEvents){
        HoldModel hold = new HoldModel();
        try{
            hold.setEventName(holdEvents.getString("event"));
            hold.setPrivilege(holdEvents.getString("privilege"));
            hold.setState(holdEvents.getString("state"));
            hold.setChannel(holdEvents.getString("channel"));
            hold.setUniqueID(holdEvents.getString("uniqueid"));
            hold.setHoldClass(holdEvents.getString("class"));
            hold.setEventTime(holdEvents.getDate("eventtime"));
        }
        catch(Exception ex){
            LogClass.logMsg("HoldDAO", "createHoldObject", ex.getMessage());
        }
        return hold;
    }
}
