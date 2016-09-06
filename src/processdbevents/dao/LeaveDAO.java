/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import processdbevents.model.LeaveModel;
import phonebridge.util.LogClass;
import org.bson.Document;

/**
 *
 * @author bharath
 */
public class LeaveDAO {
    public LeaveModel createLeaveObject(Document leaveEvents){
        LeaveModel leave = new LeaveModel();
        try{
            leave.setEventName(leaveEvents.getString("event"));
            leave.setPrivilege(leaveEvents.getString("privilege"));
            leave.setChannel(leaveEvents.getString("channel"));
            leave.setQueue(leaveEvents.getString("queue"));
            leave.setCount(leaveEvents.getString("count"));
            leave.setPosition(leaveEvents.getString("position"));
            leave.setUniqueID(leaveEvents.getString("uniqueid"));
            leave.setEventTime(leaveEvents.getDate("eventtime"));
        }
        catch(Exception ex){
            LogClass.logMsg("LeaveDAO", "createLeaveObject", ex.getMessage());
        }
        return leave;
    }
}
