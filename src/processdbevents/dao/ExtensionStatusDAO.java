/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import processdbevents.model.ExtensionStatus;
import phonebridge.util.LogClass;
import org.bson.Document;

/**
 *
 * @author bharath
 */
public class ExtensionStatusDAO {
    public ExtensionStatus createExtensionStatusObject(Document extStatusEvents){
        ExtensionStatus extensionStatus = new ExtensionStatus();
        try{
            extensionStatus.setEventName(extStatusEvents.getString("event"));
            extensionStatus.setPrivilege(extStatusEvents.getString("privilege"));
            extensionStatus.setExten(extStatusEvents.getString("exten"));
            extensionStatus.setContext(extStatusEvents.getString("context"));
            extensionStatus.setHint(extStatusEvents.getString("hint"));
            extensionStatus.setStatus(extStatusEvents.getString("status"));
            extensionStatus.setEventTime(extStatusEvents.getDate("eventtime"));
        }
        catch(Exception ex){
            LogClass.logMsg("ExtensionStatusDAO", "createExtensionStatusObject", ex.getMessage());
        }
        return extensionStatus;
    }
}
