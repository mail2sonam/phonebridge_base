/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonebridgelogger.dao;

import phonebridgelogger.db.TrunkDB;
import phonebridgelogger.model.Trunk;
import phonebridge.util.LogClass;
import java.util.ArrayList;

/**
 *
 * @author sonamuthu
 */
public class TrunkDAO {
    public ArrayList<Trunk> getTrunksForServer(String serverID) {
        ArrayList<Trunk> trunkDetails = new ArrayList<Trunk>();
        try{
            TrunkDB trunkDB = new TrunkDB();
            trunkDetails = trunkDB.getTrunkForServerID(serverID);
        }
        catch(Exception ex){
            LogClass.logMsg("TrunkDAO", "getTrunksForServer", ex.getMessage());
        }
        return trunkDetails;
    }
}
