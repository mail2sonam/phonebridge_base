/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import com.mongodb.MongoClient;
import processdbevents.model.NewStateModel;
import phonebridge.util.LogClass;
import org.bson.Document;
import processdbevents.db.NewStateDB;

/**
 *
 * @author bharath
 */
public class NewStateDAO {
    public NewStateModel createNewStateObject(Document newStateEvents){
        NewStateModel newState = new NewStateModel();
        try{
            newState.setEventName(newStateEvents.getString("event"));
            newState.setPrivilege(newStateEvents.getString("privilege"));
            newState.setChannel(newStateEvents.getString("channel"));
            newState.setChannelState(newStateEvents.getString("channelstate"));
            newState.setChannelStateDesc(newStateEvents.getString("channelstatedesc"));
            newState.setCallerIdNum(newStateEvents.getString("calleridnum"));
            newState.setCallerIdName(newStateEvents.getString("calleridname"));
            newState.setConnectedLineNum(newStateEvents.getString("connectedlinenum"));
            newState.setConnectedLineName(newStateEvents.getString("connectedlinename"));
            newState.setUniqueID(newStateEvents.getString("uniqueid"));
            newState.setEventTime(newStateEvents.getDate("eventtime"));
        }
        catch(Exception ex){
            LogClass.logMsg("NewStateDAO", "createNewStateObject", ex.getMessage());
        }
        return newState;
    }
    
    public NewStateModel getNewStateDataFromDB(String docID,String uniqueID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        NewStateModel newState = null;
        try{
            newState = new NewStateDB().getNewStateDataFromDB(docID, uniqueID,mmYYYY, serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("NewStateDAO", "getNewStateDataFromDB", ex.getMessage());
        }
        return newState;
    }
}
