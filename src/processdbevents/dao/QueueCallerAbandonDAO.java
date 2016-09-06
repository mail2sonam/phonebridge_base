/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.dao;

import processdbevents.model.QueueCallerAbandonModel;
import phonebridge.util.LogClass;
import phonebridge.util.util;
import org.bson.Document;

/**
 *
 * @author bharath
 */
public class QueueCallerAbandonDAO {
    public QueueCallerAbandonModel createQueueCallerAbandonObject(Document queueCallerEvents){
        QueueCallerAbandonModel queueCallerAbandon = new QueueCallerAbandonModel();
        try{
            queueCallerAbandon.setEventName(queueCallerEvents.getString("event"));
            queueCallerAbandon.setPrivilege(queueCallerEvents.getString("privilege"));
            queueCallerAbandon.setQueue(queueCallerEvents.getString("queue"));
            queueCallerAbandon.setUniqueID(queueCallerEvents.getString("uniqueid"));
            queueCallerAbandon.setPosition(queueCallerEvents.getString("position"));
            queueCallerAbandon.setOriginalPosition(queueCallerEvents.getString("originalposition"));
            queueCallerAbandon.setHoldTime(new util().returnIntegerValueFromObject(queueCallerEvents.getString("holdtime")));
            queueCallerAbandon.setEventTime(queueCallerEvents.getDate("eventtime"));
        }
        catch(Exception ex){
            LogClass.logMsg("QueueCallerAbandonDAO", "createQueueCallerAbandonObject", ex.getMessage());
        }
        return queueCallerAbandon;
    }
}
