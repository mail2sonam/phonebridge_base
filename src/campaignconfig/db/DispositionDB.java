/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package campaignconfig.db;

import campaignconfig.model.DependantModel;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import campaignconfig.model.DispositionModel;
import com.mongodb.BasicDBList;
import com.mongodb.MongoException;
import org.bson.Document;
import org.bson.types.ObjectId;
import phonebridge.util.LogClass;
import singleton.db.DBClass;

/**
 *
 * @author harini
 */
public class DispositionDB {
    //list all campaigns
    public ArrayList<DispositionModel> getalldispositiondetails(String campaignID){
        ArrayList<DispositionModel> alldispositiondetails = new ArrayList<DispositionModel>();
        MongoClient conn = null;
        try{
            conn = DBClass.getInstance().getConnection();
            MongoDatabase mdb = conn.getDatabase(DBClass.MDATABASE);
            MongoCollection mc = mdb.getCollection("campaign");
            Document wherequery = new Document("deleted",0);
            wherequery.append("_id",new ObjectId(campaignID));
            Document selectQuery = new Document();
            selectQuery.append("dispositions", 1);
            MongoCursor cursor = mc.find(wherequery).projection(selectQuery).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                Object ob = dc.get("dispositions");
                ArrayList<Object> dispositions = (ArrayList<Object>)ob;
                for(Object disp:dispositions){
                    Document singledispositionDocument = (Document) disp;
                    DispositionModel dm = this.convertDBObjecttoJavaObject(singledispositionDocument);
                    alldispositiondetails.add(dm);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        finally{
            //conn.close();
        }
        return alldispositiondetails;
    }

   
       //get dispositiondetails for edit using ID
    public DispositionModel getDispositionDetailsUsingID(String campaignID,String dispositionID){
        DispositionModel dispositionData = null;
        MongoClient conn = null;
        try{
            conn = DBClass.getInstance().getConnection();
            MongoDatabase mdb = conn.getDatabase(DBClass.MDATABASE);
            MongoCollection mc = mdb.getCollection("campaign");
            Document whereQuery = new Document();
            whereQuery.append("_id", new ObjectId(campaignID));
            whereQuery.append("dispositions.id", new ObjectId(dispositionID));
            Document selectQuery = new Document();
            selectQuery.append("dispositions.$", 1);
            //selectQuery.append("dispositions.$",new Document("id",new ObjectId(dispositionID)));
            MongoCursor cursor = mc.find(whereQuery).projection(selectQuery).iterator();
            if(cursor.hasNext())
            {
                Document dc = (Document) cursor.next();
                Object ob=dc.get("dispositions");
                ArrayList<Object> disposition = (ArrayList<Object>) ob;
                for(Object disp:disposition)
                {
                    Document dispositions = (Document) disp;
                    dispositionData = convertDBObjecttoJavaObject(dispositions);
                    dispositionData.setDependant(new DependantDB().getAllDependantDetails(campaignID, dispositionID));
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        finally{
            //conn.close();
        }
        return dispositionData;
    }
    
        
    

    
    private DispositionModel convertDBObjecttoJavaObject(Document dc){
        DispositionModel dispositiondetails  = null;
        if(dc==null)
            return dispositiondetails;
        //
        String campaignID = null;
        String fieldLabel = null;
        String fieldValue = null;
        String callWorkFlow = null;
        boolean sendSMS = false;
        String smsText = null;
        boolean conversion = false;
        try{
            campaignID = dc.getObjectId("id").toString();
        }
        catch(NullPointerException nexc){
            
        }
        try{
            fieldLabel = dc.getString("fieldLabel");
        }
        catch(NullPointerException nexc){
            
        }
        try{
            fieldValue = dc.getString("fieldValue");
        }
        catch(NullPointerException nexc){
            
        }
        try{
            callWorkFlow = dc.getString("callWorkFlow");
        }
        catch(NullPointerException nexc){
            
        }
        try{
            sendSMS = dc.getBoolean("sendSMS");
        }
        catch(NullPointerException nexc){
            
        }
        try{
            smsText = dc.getString("smsText");
        }
        catch(NullPointerException nexc){
            
        }
        try{
            conversion = dc.getBoolean("isConversion");
        }
        catch(NullPointerException nexc){
            
        }
        dispositiondetails = new DispositionModel(campaignID,fieldLabel,fieldValue,callWorkFlow,sendSMS,smsText,conversion);
        return dispositiondetails;
    }
    
    /*
        Return Disposition As BasicDBList FOr Inserting in CTI
    */
    public BasicDBList getDispositionAsBasicDBList(String campaignID,MongoClient mongoConn){
        BasicDBList dispositionData = null;
        try{
            MongoDatabase mdb = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection mc = mdb.getCollection("campaign");
            Document wherequery = new Document("deleted",0);
            wherequery.append("_id",new ObjectId(campaignID));
            Document selectQuery = new Document();
            selectQuery.append("dispositions", 1);
            MongoCursor cursor = mc.find(wherequery).projection(selectQuery).iterator();
            if(cursor.hasNext())
            {
                Document dc = (Document) cursor.next();
                dispositionData = this.returnDispositionDBList((ArrayList) dc.get("dispositions"));
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("DispositionDB", "getAllDispositionDetailsAsBasicDBList", mexc.getMessage());
        }
        return dispositionData;
    }
    
    /*
        Return Dependant As BasicDBList FOr Inserting in CTI
    */
    public BasicDBList returnDispositionDBList(ArrayList dispositions){
        BasicDBList dispositionDBList = new BasicDBList();
        try{
            for(Object eachObj : dispositions){
                dispositionDBList.add(eachObj);
            }
        }
        catch(Exception ex){
            LogClass.logMsg("DispositionDB", "returnDispositionDBList", ex.getMessage());
        }
        return dispositionDBList;
    }
    
    public ArrayList<DispositionModel> getAllDispositionWithDependentForCampaign(String campaignID){
        ArrayList<DispositionModel> allDispositionWithDependent = new ArrayList<>();
        try{
            MongoClient mongoConn = DBClass.getInstance().getConnection();
            MongoDatabase mdb = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection mc = mdb.getCollection("campaign");
            Document wherequery = new Document("deleted",0);
            wherequery.append("_id",new ObjectId(campaignID));
            Document selectQuery = new Document();
            selectQuery.append("dispositions", 1);
            MongoCursor cursor = mc.find(wherequery).projection(selectQuery).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                Object dispoOb = dc.get("dispositions");
                ArrayList<Object> disposition = (ArrayList<Object>) dispoOb;
                for(Object eachDispo : disposition){
                    Document singleDispDoc = (Document) eachDispo;
                    DispositionModel dispositionModel = this.convertDBObjecttoJavaObject(singleDispDoc);
                    Object allDependant = singleDispDoc.get("dependant");
                    ArrayList<Object> dependants = (ArrayList<Object>)allDependant;
                    ArrayList<DependantModel> dependantArray = new ArrayList<>();
                    for(Object eachDependant : dependants){
                        Document singleDependantDoc = (Document) eachDependant;
                        DependantModel dependantModel = new DependantDB().convertDBObjecttoJavaObject(singleDependantDoc);
                        dependantArray.add(dependantModel);
                    }
                    dispositionModel.setDependant(dependantArray);
                    allDispositionWithDependent.add(dispositionModel);
                }
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("DispositionDB", "getAllDispositionWithDependentForCampaign", mexc.getMessage());
        }
        return allDispositionWithDependent;
    }
    
    public ArrayList<String> getAllDispositionName(String campaignID,MongoClient mongoConn){
        ArrayList<String> allDispositionName = new ArrayList<>();
        try{
            MongoDatabase mongoDB = mongoConn.getDatabase(DBClass.MDATABASE);
            MongoCollection collection = mongoDB.getCollection("campaign");
            Document whereQry = new Document("_id", new ObjectId(campaignID));
            Document selectQry = new Document("dispositions.fieldLabel", 1);
            MongoCursor cursor = collection.find(whereQry).projection(selectQry).iterator();
            if(cursor.hasNext()){
                Document dc = (Document) cursor.next();
                Object ob = dc.get("dispositions");
                ArrayList<Object> dispositions = (ArrayList<Object>) ob;
                for(Object eachDisposition : dispositions){
                    Document dispositionDoc = (Document) eachDisposition;
                    allDispositionName.add(dispositionDoc.getString("fieldLabel"));
                }
            }
        }
        catch(MongoException mexc){
            LogClass.logMsg("DispositionDB", "getAllDispositionName", mexc.getMessage());
        }
        return allDispositionName;
    }
}
