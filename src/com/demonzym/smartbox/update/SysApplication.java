package com.demonzym.smartbox.update;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class SysApplication extends Application{

	private List mList = new LinkedList(); 
    private static SysApplication instance; 
 
    private SysApplication() {
    } 
    public synchronized static SysApplication getInstance() { 
        if (null == instance) { 
            instance = new SysApplication(); 
        } 
        return instance; 
    } 
    // add Activity  
    public void addActivity(Activity activity) { 
        mList.add(activity); 
    } 
 
    public void exit() { 
        try { 
        	for(int i=0;i<mList.size();i++)
        	{
        		Activity activity = (Activity)mList.get(i);
        		activity.finish();
        	}
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            System.exit(0); 
        } 
    } 
    public void onLowMemory() { 
        super.onLowMemory();     
        System.gc(); 
    }  
}
