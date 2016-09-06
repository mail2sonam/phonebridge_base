/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campaignconfig.db;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import campaignconfig.model.DependantModel;
import org.bson.Document;
import org.bson.types.ObjectId;
import phonebridge.util.LogClass;
import singleton.db.DBClass;
/**
 *
 * @author harini
 */
public class DependantDB {
    //list all campaigns
    public ArrayList<DependantModel> getAllDependantDetails(String campaignID,String dispositionID){
        ArrayList<DependantModel> totaldependantdetails = new ArrayList<DependantModel>();
        MongoClient conn = null;
         try{
            conn = DBClass.getInstance().getConnection();
            MongoDatabase mdb = conn.getDatabase(DBClass.MDATABASE);
            MongoCollection mc = mdb.getCollection("campaign");
            Document wherequery = new Document("deleted",0);
            wherequery.append("_id",new ObjectId(campaignID));
            wherequery.append("dispositions.id",new ObjectId(dispositionID));
            Document selectQuery = new Document();
            selectQuery.append("dispositions.dependant.$", 1);
            MongoCursor cursor = mc.find(wherequery).projection(selectQuery).iterator();
            if(cursor.hasNext())
            {
                Document dc = (Document) cursor.next();
                Object ob=dc.get("dispositions");
                ArrayList<Object> disposition = (ArrayList<Object>) ob;
                for(Object disp:disposition)
                {
                    Document singledispositionDocument= (Document) disp;
                    Object dep = singledispositionDocument.get("dependant");
                    ArrayList<Object> dependants=(ArrayList<Object>)dep;
                    for(Object dependant:dependants)
                    {
                        Document singledependantDocument=(Document)dependant;
                        DependantModel dm=this.convertDBObjecttoJavaObject(singledependantDocument);
                        totaldependantdetails.add(dm);
                    }
                    
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        finally{
            //conn.close();
        }
        return totaldependantdetails;
    }
    
    
      public DependantModel convertDBObjecttoJavaObject(Document dc){
        
        DependantModel dependantdetails  = null;
        if(dc==null)
            return dependantdetails;
        //
        String slotName = null;
        String slotSlab = null;
        String selectedSlotName = null;
        String selectedSlotSlab = null;
        boolean timeSlotGlobal = false;
        boolean conversionDependant = false;
        try{
            slotName = dc.getString("slotName");
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("DependantDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            slotSlab = dc.getString("slotSlab");
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("DependantDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        try{
            timeSlotGlobal = dc.getBoolean("timeSlotGlobal");
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("DependantDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
       try{
            conversionDependant = dc.getBoolean("conversionDependant");
        }
        catch(NullPointerException nexc){
            LogClass.logMsg("DependantDB", "convertDBObjecttoJavaObject", nexc.getMessage());
        }
        dependantdetails = new DependantModel(dc.getObjectId("id").toString(),dc.getString("fieldValue"),
            dc.getString("fieldLabel"),dc.getString("fieldType"),dc.getString("dropDownValue"),null,slotName,slotSlab,
            timeSlotGlobal,conversionDependant);
        return dependantdetails;
    }    
        
    
    
}
