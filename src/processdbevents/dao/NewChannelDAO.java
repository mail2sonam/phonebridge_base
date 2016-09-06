/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import processdbevents.model.NewChannelModel;
import phonebridge.util.LogClass;
import org.bson.Document;

/**
 *
 * @author bharath
 */
public class NewChannelDAO {
    public NewChannelModel createNewChannelObject(Document newChannelEvents){
        NewChannelModel newChannel = new NewChannelModel();
        try{
            newChannel.setEventName(newChannelEvents.getString("event"));
            newChannel.setPrivilege(newChannelEvents.getString("privilege"));
            newChannel.setChannel(newChannelEvents.getString("channel"));
            newChannel.setChannelState(newChannelEvents.getString("channelstate"));
            newChannel.setChannelStateDesc(newChannelEvents.getString("channelstatedesc"));
            newChannel.setCallerIdNum(newChannelEvents.getString("calleridnum"));
            newChannel.setCallerIdName(newChannelEvents.getString("calleridname"));
            newChannel.setAccountCode(newChannelEvents.getString("accountcode"));
            newChannel.setExten(newChannelEvents.getString("exten"));
            newChannel.setContext(newChannelEvents.getString("context"));
            newChannel.setUniqueID(newChannelEvents.getString("uniqueid"));
            newChannel.setEventTime(newChannelEvents.getDate("eventtime"));
        }
        catch(Exception ex){
            LogClass.logMsg("NewChannelDAO", "createNewChannelObject", ex.getMessage());
        }
        return newChannel;
    }
}
