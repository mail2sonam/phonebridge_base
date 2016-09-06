/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import processdbevents.model.DialEndModel;
import phonebridge.util.LogClass;
import org.bson.Document;

/**
 *
 * @author bharath
 */
public class DialEndDAO {
    public DialEndModel createDialEndObject(Document dialEndEvents){
        DialEndModel dialEnd = new DialEndModel();
        try{
            dialEnd.setEventName(dialEndEvents.getString("event"));
            dialEnd.setPrivilege(dialEndEvents.getString("privilege"));
            dialEnd.setSubEvent(dialEndEvents.getString("subevent"));
            dialEnd.setChannel(dialEndEvents.getString("channel"));
            dialEnd.setUniqueID(dialEndEvents.getString("uniqueid"));
            dialEnd.setDialStatus(dialEndEvents.getString("dialstatus"));
            dialEnd.setEventTime(dialEndEvents.getDate("eventtime"));
        }
        catch(Exception ex){
            LogClass.logMsg("DialEndDAO", "createDialEndObject", ex.getMessage());
        }
        return dialEnd;
    }
}
