package com.ruitzei.app;

import com.parse.Parse;
import com.parse.PushService;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static MyApplication mInstance;
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        this.setAppContext(getApplicationContext());
        
		Parse.initialize(this, "VMAZUcL4dQslhLyBBuQui4Shhv7igRdbUXqR0Z3w", "sgu55tEFLOXmu3nEdndTDNPHRUwDWcfaHRA1Co7N");
		PushService.setDefaultPushCallback(this, MainActivity.class);
		
		//ParseObject parseTestObject = new ParseObject("parseTestObject");
		//parseTestObject.put("Foo", "bar");
		//parseTestObject.saveInBackground();

    }

    public static MyApplication getInstance(){
        return mInstance;
    }
    public static Context getAppContext() {
        return mAppContext;
    }
    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }
}
