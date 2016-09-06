/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridgelogger.model;

import java.io.Serializable;
import org.bson.Document;

/**
 *
 * @author sonamuthu
 */
public class Trunk implements Serializable{
    private String prefix;
    private String trunkValue;
    private String cdrTrunkValue;
    
    public Trunk(){
        
    }
    
    public Trunk(String prefix,String trunkValue,String cdrTrunkValue){
        this.prefix = prefix;
        this.trunkValue = trunkValue;
        this.cdrTrunkValue =  cdrTrunkValue;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return the trunkValue
     */
    public String getTrunkValue() {
        return trunkValue;
    }

    /**
     * @param trunkValue the trunkValue to set
     */
    public void setTrunkValue(String trunkValue) {
        this.trunkValue = trunkValue;
    }

    /**
     * @return the cdrTrunkValue
     */
    public String getCdrTrunkValue() {
        return cdrTrunkValue;
    }

    /**
     * @param cdrTrunkValue the cdrTrunkValue to set
     */
    public void setCdrTrunkValue(String cdrTrunkValue) {
        this.cdrTrunkValue = cdrTrunkValue;
    }
    
    public Document getDocument(Trunk trunkUsed){
        Document doc = new Document();
        try{
            doc.append("trunkValue", trunkUsed.getTrunkValue());
            doc.append("prefix", trunkUsed.getPrefix());
            doc.append("cdrTrunkValue", trunkUsed.getCdrTrunkValue());
        }
        catch(NullPointerException nexc){
            doc.append("trunkValue", null);
            doc.append("prefix", null);
            doc.append("cdrTrunkValue", null);
        }
        return doc;
    }
}
