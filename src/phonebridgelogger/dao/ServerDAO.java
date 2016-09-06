/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridgelogger.dao;

import com.mongodb.MongoClient;
import phonebridgelogger.db.ServerDB;
import phonebridgelogger.model.Server;
import phonebridge.util.LogClass;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author sonamuthu
 */
public class ServerDAO {
    
    public Server getServerByID(String serverID){
        Server serverDetails = null;
        serverDetails = new ServerDB().getServerByID(serverID);
        return serverDetails;
    }
    
    public void updateLastEventTime(String serverID,Date lastEventTime,ServerDB serverDBObj,MongoClient mongoConn){
        try{
            serverDBObj.updateLastEventTime(serverID,lastEventTime,mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ServerDAO", "updateLastEventTime", ex.getMessage());
        }
    }
    
    public String checkForStopRequest(String serverID,ServerDB serverDbObj,String whatToCheckFor,MongoClient mongoConn){
        String requestID = null;
        try{
            requestID = serverDbObj.checkForStopRequest(serverID,whatToCheckFor,mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ServerDAO", "checkForStopRequest", ex.getMessage());
        }
        return requestID;
    }
    
    public void insertOrUpdateUpTime(String serverID,String eventName,String reason,MongoClient mongoConn){
        try{
            switch(eventName){
                case "Connect":
                    new ServerDB().insertIntoUptime(serverID, mongoConn);
                    break;
                case "Disconnect":
                    new ServerDB().updateUptime(serverID,reason,mongoConn);
                    break;
            }
        }
        catch(Exception ex){
            LogClass.logMsg("ServerDAO", "insertOrUpdateUpTime", ex.getMessage());
        }
    }
    
    public void updateRequestCompletedOn(String requestID,MongoClient mongoConn){
        new ServerDB().updateRequestCompletedOn(requestID, mongoConn);
    }
    
    public void updateRequestStartedOn(String requestID,MongoClient mongoConn){
        new ServerDB().updateRequestStartedOn(requestID, mongoConn);
    }
    
    public ArrayList<Server> getAllServerDetails(){
        ArrayList<Server> serverDetails = new ArrayList<>();
        try{
            serverDetails = new ServerDB().getAllServerDetails();
        }
        catch(Exception ex){
            LogClass.logMsg("ServerDAO","getAllServerDetails", ex.getMessage());
        }
        return serverDetails;
    }
    
    public void updateCallProcessStatus(String valToUpdate,String serverID,MongoClient mongoConn){
        try{
            new ServerDB().updateCallProcessStatus(valToUpdate,serverID,mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ServerDAO","updateCallProcessStatus", ex.getMessage());
        }
    }
    
    public String getServerNamePrefixForID(String serverID,MongoClient mongoConn){
        String serverNamePrefix = null;
        try{
            serverNamePrefix = new ServerDB().getServerNamePrefixForID(serverID, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("ServerDAO","getServerNamePrefixForID", ex.getMessage());
        }
        return serverNamePrefix;
    }
}
