/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processdbevents.model;

import java.io.Serializable;
import java.util.Date;
import org.bson.Document;

/**
 *
 * @author venky
 */
public class HopperModel implements Serializable{
    private String hoppingUser;
    private Date hoppingTime;
    private String uniqueID;
    private String destUniqueID;

    public String getHoppingUser() {
        return hoppingUser;
    }

    public void setHoppingUser(String hoppingUser) {
        this.hoppingUser = hoppingUser;
    }

    public Date getHoppingTime() {
        return hoppingTime;
    }

    public void setHoppingTime(Date hoppingTime) {
        this.hoppingTime = hoppingTime;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getDestUniqueID() {
        return destUniqueID;
    }

    public void setDestUniqueID(String destUniqueID) {
        this.destUniqueID = destUniqueID;
    }
    
    public Document getDocument(){
        Document doc = new Document();
        doc.append("hoppedUser", this.hoppingUser);
        doc.append("hoppedTime", this.hoppingTime);
        doc.append("uniqueID", this.uniqueID);
        doc.append("destUniqueID", this.destUniqueID);
        //doc.append("cause", this.causeCode);
        return doc;
    }
}
