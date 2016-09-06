/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import processdbevents.model.CoreShowChannelModel;
import org.bson.Document;
import phonebridge.util.LogClass;

/**
 *
 * @author sonamuthu
 */
public class CoreShowChannelDAO {
    public CoreShowChannelModel createCoreShowChannelObject(Document coreShowChannelEvents){
        CoreShowChannelModel coreShowChannel = new CoreShowChannelModel();
        try{
            coreShowChannel.setEventName(coreShowChannelEvents.getString("event"));
            coreShowChannel.setChannel(coreShowChannelEvents.getString("channel"));
            coreShowChannel.setUniqueID(coreShowChannelEvents.getString("uniqueid"));
            coreShowChannel.setContext(coreShowChannelEvents.getString("context"));
            coreShowChannel.setExtension(coreShowChannelEvents.getString("extension"));
            coreShowChannel.setPriority(coreShowChannelEvents.getString("priority"));
            coreShowChannel.setChannelState(coreShowChannelEvents.getString("channelstate"));
            coreShowChannel.setChannelStateDesc(coreShowChannelEvents.getString("channelstatedesc"));
            coreShowChannel.setApplication(coreShowChannelEvents.getString("application"));
            coreShowChannel.setApplicationData(coreShowChannelEvents.getString("applicationdata"));
            coreShowChannel.setCallerIDNum(coreShowChannelEvents.getString("calleridnum"));
            coreShowChannel.setCallerIDName(coreShowChannelEvents.getString("calleridname"));
            coreShowChannel.setConnectedLineNum(coreShowChannelEvents.getString("connectedlinenum"));
            coreShowChannel.setConnectedLineName(coreShowChannelEvents.getString("connectedlinename"));
            coreShowChannel.setDuration(coreShowChannelEvents.getString("duration"));
            coreShowChannel.setAccountCode(coreShowChannelEvents.getString("accountcode"));
            coreShowChannel.setBridgedChannel(coreShowChannelEvents.getString("bridgedchannel"));
            coreShowChannel.setBridgedUniqueID(coreShowChannelEvents.getString("bridgeduniqueid"));
            coreShowChannel.setEventTime(coreShowChannelEvents.getDate("eventtime"));
        }
        catch(Exception ex){
            LogClass.logMsg("CoreShowChannelDAO", "createCoreShowChannelObject", ex.getMessage());
        }
        return coreShowChannel;
    }
}
