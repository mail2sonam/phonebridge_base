/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import processdbevents.model.CoreShowChannelsCompleteModel;
import org.bson.Document;
import phonebridge.util.LogClass;
/**
 *
 * @author sonamuthu
 */
public class CoreShowChannelsCompleteDAO {
    public CoreShowChannelsCompleteModel createCoreShowChannelCompleteObject(Document coreShowChannelCompleteEvents){
        CoreShowChannelsCompleteModel coreShowChannelComplete = new CoreShowChannelsCompleteModel();
        try{
            coreShowChannelComplete.setEventName(coreShowChannelCompleteEvents.getString("event"));
            coreShowChannelComplete.setEventList(coreShowChannelCompleteEvents.getString("eventlist"));
            coreShowChannelComplete.setListItems(coreShowChannelCompleteEvents.getString("listitems"));
            coreShowChannelComplete.setEventTime(coreShowChannelCompleteEvents.getDate("eventtime"));
        }
        catch(Exception ex){
            LogClass.logMsg("BridgeDAO", "createBridgeObject", ex.getMessage());
        }
        return coreShowChannelComplete;
    }
}
