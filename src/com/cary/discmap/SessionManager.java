package com.cary.discmap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
 

/**
 * Manage all user login sessions
 * @author mike
 *
 */
public class SessionManager {
	private static SessionManager self;
    private SharedPreferences pref;
    private Editor editor; 
    private Context mContext;
     
    // Shared pref mode
    int PRIVATE_MODE = 0;
     
    private static final String PREF_NAME = "DiscPrefs";
     
    private static final String IS_LOGGED_IN = "isLoggedIn";
     
    public static final String KEY_USER = "user";
    
    public static final String KEY_ID = "id";
     
    private SessionManager(Context context){
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    
    /**
     * Create login session
     * */
    public void createLoginSession(String user, int id){
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_USER, user);
        editor.putInt(KEY_ID, id);

        editor.commit();
    }
    
    public String getUser() {
    	return pref.getString(KEY_USER, null);
    }
    
    public Integer getId() {
    	return pref.getInt(KEY_ID, 0);
    }
    
    public boolean isLoggedIn() {
    	return pref.getBoolean(IS_LOGGED_IN, false);
    }
    
    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else won't do anything. Should be used in onCreate()
     * of all activities.
     **/
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            Intent i = new Intent(mContext, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
        }
    }
    
    /**
     * Clear session details
     * */
    public void logoutUser(){
        editor.clear();
        editor.commit();
         
        checkLogin();
    }
    
    /**
     * Get singleton instance. Context isn't always needed,
     * but must be passed in case instantiation is needed.
     */
    public static SessionManager get(Context context) {
    	if (self == null) {
    		self = new SessionManager(context);
    	}
    	return self;
    }
    
    
}