/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.model;
import java.util.Date;
import org.bson.Document;

/**
 *
 * @author sonamuthu
 */
public class CoreShowChannelsCompleteModel {
    private String eventName;
    private String eventList;
    private String listItems;
    private Date eventTime;

    /**
     * @return the eventName
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * @param eventName the eventName to set
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * @return the eventList
     */
    public String getEventList() {
        return eventList;
    }

    /**
     * @param eventList the eventList to set
     */
    public void setEventList(String eventList) {
        this.eventList = eventList;
    }

    /**
     * @return the listItems
     */
    public String getListItems() {
        return listItems;
    }

    /**
     * @param listItems the listItems to set
     */
    public void setListItems(String listItems) {
        this.listItems = listItems;
    }

    /**
     * @return the eventTime
     */
    public Date getEventTime() {
        return eventTime;
    }

    /**
     * @param eventTime the eventTime to set
     */
    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }
    
    public Document getDocument(){
        Document doc = new Document();
        doc.append("eventName",this.eventName);
        doc.append("eventList",this.eventList);
        doc.append("listItems",this.listItems);
        doc.append("eventTime", this.eventTime);
        return doc;
    }
}
