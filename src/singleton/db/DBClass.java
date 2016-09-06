/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package singleton.db;

import com.mongodb.MongoClient;

/**
 *
 * @author sonamuthu
 */
public class DBClass {
    private static DBClass singleton = new DBClass();
    private MongoClient mongoClient;
    public static final String MDATABASE = "phonebridge_basic";
    
    private DBClass() {
    }

    public static DBClass getInstance() {
        return singleton;
    }

    public MongoClient getConnection(){
        String mongoIpAddress = "localhost";//"192.168.10.197";
        Integer mongoPort = 27017;
        try{
            if(mongoClient==null)
            {
                mongoClient = new MongoClient(mongoIpAddress, mongoPort);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return mongoClient;
    } 
}
