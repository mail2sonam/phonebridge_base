/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import processdbevents.model.DialBeginModel;
import phonebridge.util.LogClass;
import org.bson.Document;

/**
 *
 * @author bharath
 */
public class DialBeginDAO {
    public DialBeginModel createDialBeginObject(Document dialBeginEvents){
        DialBeginModel dialBegin = new DialBeginModel();
        try{
            dialBegin.setEventName(dialBeginEvents.getString("event"));
            dialBegin.setPrivilege(dialBeginEvents.getString("privilege"));
            dialBegin.setSubEvent(dialBeginEvents.getString("subevent"));
            dialBegin.setChannel(dialBeginEvents.getString("channel"));
            dialBegin.setDestination(dialBeginEvents.getString("destination"));
            dialBegin.setCallerIDNum(dialBeginEvents.getString("calleridnum"));
            dialBegin.setCallerIDName(dialBeginEvents.getString("calleridname"));
            dialBegin.setConnectedLineNum(dialBeginEvents.getString("connectedlinenum"));
            dialBegin.setConnectedLineName(dialBeginEvents.getString("connectedlinename"));
            dialBegin.setUniqueID(dialBeginEvents.getString("uniqueid"));
            dialBegin.setDestUniqueID(dialBeginEvents.getString("destuniqueid"));
            dialBegin.setDialString(dialBeginEvents.getString("dialstring"));
            dialBegin.setEventTime(dialBeginEvents.getDate("eventtime"));
        }
        catch(Exception ex){
            LogClass.logMsg("DialBeginDAO", "createDialBeginObject", ex.getMessage());
        }
        return dialBegin;
    }
    
    public String getPhoneNumberFromDialString(String dialString){
        String phoneNumber = null;
        try{
            String[] dialStringArr = dialString.split("/");
            phoneNumber = dialStringArr[dialStringArr.length-1];
        }
        catch(Exception ex){
            LogClass.logMsg("DialBeginDAO", "getPhoneNumberFromDialString", ex.getMessage());
        }
        return phoneNumber;
    }
    
    public String getExtensionFromChannelOrDestination(String extensionSource){
        String extension = null;
        try{
            String[] extensionSourceArr = extensionSource.split("-");
            String[] extensionArr = extensionSourceArr[0].split("/");
            if(extensionArr[extensionArr.length-1].contains("@")){
                extensionArr = extensionArr[extensionArr.length-1].split("@");
                extension = extensionArr[0];
            }
            else
                extension = extensionArr[extensionArr.length-1];
        }
        catch(Exception ex){
            LogClass.logMsg("DialBeginDAO", "getExtensionFromChannelOrDestination", ex.getMessage());
        }
        return extension;
    }
    
    public String getExtensionOrFollowNumberFromDestination(String destination){
        String extension = null;
        try{
            String[] extensionSourceArr = destination.split("@");
            String[] extensionArr = extensionSourceArr[0].split("-");
            if(extensionArr[extensionArr.length-1].contains("#")){
                extensionArr = extensionArr[extensionArr.length-1].split("#");
                extension = extensionArr[0];
            }
            else
                extension = extensionArr[extensionArr.length-1];
        }
        catch(Exception ex){
            
        }
        return extension;
    }
    
    public String getExtensionOrFollowNumberUsingConnectedLineNum(String connectedLineNum){
        String extension = null;
        try{
            String[] extensionArr = connectedLineNum.split("-");
            if(extensionArr[extensionArr.length-1].contains("#")){
                extensionArr = extensionArr[extensionArr.length-1].split("#");
                extension = extensionArr[0];
            }
            else
                extension = extensionArr[extensionArr.length-1];
        }
        catch(Exception ex){
            
        }
        return extension;
    }
    
    public DialBeginModel createDialBeginObjectFromCurrentCalls(Document dialBeginEvents){
        DialBeginModel dialBegin = new DialBeginModel();
        try{
            dialBegin.setEventName(dialBeginEvents.getString("eventName"));
            dialBegin.setPrivilege(dialBeginEvents.getString("privilege"));
            dialBegin.setSubEvent(dialBeginEvents.getString("subEvent"));
            dialBegin.setChannel(dialBeginEvents.getString("channel"));
            dialBegin.setDestination(dialBeginEvents.getString("destination"));
            dialBegin.setCallerIDNum(dialBeginEvents.getString("callerIDNum"));
            dialBegin.setCallerIDName(dialBeginEvents.getString("callerIDName"));
            dialBegin.setConnectedLineNum(dialBeginEvents.getString("connectedLineNum"));
            dialBegin.setConnectedLineName(dialBeginEvents.getString("connectedLineName"));
            dialBegin.setUniqueID(dialBeginEvents.getString("uniqueID"));
            dialBegin.setDestUniqueID(dialBeginEvents.getString("destUniqueID"));
            dialBegin.setDialString(dialBeginEvents.getString("dialString"));
            dialBegin.setEventTime(dialBeginEvents.getDate("eventTime"));
        }
        catch(Exception ex){
            LogClass.logMsg("DialBeginDAO", "createDialBeginObject", ex.getMessage());
        }
        return dialBegin;
    }
}
