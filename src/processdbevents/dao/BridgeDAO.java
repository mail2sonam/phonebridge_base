/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import com.mongodb.MongoClient;
import processdbevents.model.BridgeModel;
import phonebridge.util.LogClass;
import org.bson.Document;
import processdbevents.db.BridgeDB;

/**
 *
 * @author bharath
 */
public class BridgeDAO {
    public BridgeModel createBridgeObject(Document bridgeEvents){
        BridgeModel bridge = new BridgeModel();
        try{
            //ami - Event 
            //db - eventName
            bridge.setEventName(bridgeEvents.getString("event"));
            bridge.setPrivilege(bridgeEvents.getString("privilege"));
            bridge.setBridgeState(bridgeEvents.getString("bridgestate"));
            bridge.setBridgeType(bridgeEvents.getString("bridgetype"));
            bridge.setChannel1(bridgeEvents.getString("channel1"));
            bridge.setChannel2(bridgeEvents.getString("channel2"));
            bridge.setUniqueID1(bridgeEvents.getString("uniqueid1"));
            bridge.setUniqueID2(bridgeEvents.getString("uniqueid2"));
            bridge.setCallerID1(bridgeEvents.getString("callerid1"));
            bridge.setCallerID2(bridgeEvents.getString("callerid2"));
            bridge.setEventTime(bridgeEvents.getDate("eventtime"));
        }
        catch(Exception ex){
            LogClass.logMsg("BridgeDAO", "createBridgeObject", ex.getMessage());
        }
        return bridge;
    }
    
    public BridgeModel getBridgeDataForDB(String docID,String mmYYYY,String serverNamePrefix,MongoClient mongoConn){
        BridgeModel bridge = null;
        try{
            bridge = new BridgeDB().getFirstBridgeTime(docID, mmYYYY,serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("BridgeDAO", "BridgeModel", ex.getMessage());
        }
        return bridge;
    }
}
