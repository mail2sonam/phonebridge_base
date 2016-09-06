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
import campaignconfig.model.ShowFieldModel;
import org.bson.Document;
import org.bson.types.ObjectId;
import singleton.db.DBClass;

/**
 *
 * @author harini
 */
public class ShowFieldDB {
    
    public ArrayList<ShowFieldModel> getallshowFieldDetails(String CampaignID){
        ArrayList<ShowFieldModel> allcampaignpopupdetails = new ArrayList<ShowFieldModel>();
        MongoClient conn = null;
        try{
            conn = DBClass.getInstance().getConnection();
            MongoDatabase mdb = conn.getDatabase(DBClass.MDATABASE);
            MongoCollection mc = mdb.getCollection("campaign");
            Document wherequery = new Document("deleted",0);
            wherequery.append("_id",new ObjectId(CampaignID));
            Document selectQuery = new Document();
            selectQuery.append("showFields", 1);
            MongoCursor cursor = mc.find(wherequery).projection(selectQuery).iterator();
            while(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                Object ob = dc.get("showFields");
                ArrayList<Object> showFields=(ArrayList<Object>)ob;
                for(Object sf:showFields){
                    Document singleShowFieldDocument=(Document)sf;
                    ShowFieldModel sm=this.convertDBObjecttoJavaObject(singleShowFieldDocument);
                    allcampaignpopupdetails.add(sm);
                }
            }
            System.out.println("details are:"+allcampaignpopupdetails);
        }
        catch(Exception mex){
            mex.printStackTrace();
        }
        finally{
            //conn.close();
        }
        return allcampaignpopupdetails;
    }
   
    
    public ShowFieldModel convertDBObjecttoJavaObject(Document dc){
        ShowFieldModel showfielddetails  = null;
        if(dc==null)
            return showfielddetails;
        //
        String id="";
        String fieldValue=(dc.getString("fieldValue")==null)?"":dc.getString("fieldValue");
        String fieldLabel=(dc.getString("fieldLabel")==null)?"":dc.getString("fieldLabel");
        boolean url=dc.getBoolean("isUrl");
        
        try{
        id=dc.getObjectId("id").toString();
        }
        catch(Exception ne)
        {ne.printStackTrace();
        }
        showfielddetails = new ShowFieldModel(id,fieldLabel,fieldValue,url);
        
        return showfielddetails;
    }

}
