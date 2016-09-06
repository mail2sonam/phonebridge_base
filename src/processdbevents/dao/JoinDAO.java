/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import com.mongodb.MongoClient;
import processdbevents.model.JoinModel;
import phonebridge.util.LogClass;
import java.util.Date;
import org.bson.Document;
import processdbevents.db.JoinDB;

/**
 *
 * @author bharath
 */
public class JoinDAO {
    public JoinModel createJoinObject(Document joinEvents){
        JoinModel join = new JoinModel();
        try{
            join.setEventName(joinEvents.getString("event"));
            join.setPrivilege(joinEvents.getString("privilege"));
            join.setChannel(joinEvents.getString("channel"));
            join.setCallerIdNum(joinEvents.getString("calleridnum"));
            join.setCallerIdName(joinEvents.getString("calleridname"));
            join.setConnectedLineNum(joinEvents.getString("connectedlinenum"));
            join.setConnectedLineName(joinEvents.getString("connectedlinename"));
            join.setQueue(joinEvents.getString("queue"));
            join.setPosition(joinEvents.getString("position"));
            join.setCount(joinEvents.getString("count"));
            join.setUniqueID(joinEvents.getString("uniqueid"));
            join.setEventTime(joinEvents.getDate("eventtime"));
        }
        catch(Exception ex){
            LogClass.logMsg("JoinDAO", "createJoinObject", ex.getMessage());
        }
        return join;
    }
    
    public Date getQueueJoinTime(String docID, String mmYYYY,String serverNamePrefix, MongoClient mongoConn){
        Date joinTime = null;
        try{
            joinTime = new JoinDB().getQueueJoinTime(docID,mmYYYY ,serverNamePrefix, mongoConn);
        }
        catch(Exception ex){
            LogClass.logMsg("JoinDAO", "getQueueJoinTime", ex.getMessage());
        }
        return joinTime;
    }
}
