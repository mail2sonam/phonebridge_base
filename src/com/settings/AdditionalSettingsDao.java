/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settings;

/**
 *
 * @author bharath
 */
public class AdditionalSettingsDao {
    public AdditionalSettings getCurrentSettings(){
        return new AdditionalSettingsDb().getCurrentSetting();
    }
}
