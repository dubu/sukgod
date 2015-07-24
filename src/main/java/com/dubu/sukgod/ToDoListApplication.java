package com.dubu.sukgod;

import com.parse.Parse;
import com.parse.ParseACL;

import com.parse.ParseCrashReporting;
import com.parse.ParseUser;

import android.app.Application;

public class ToDoListApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

//        //Parse.initialize(this, "2H847uPdm5TNwkAlUHB0igF5kw3xXSdcjXzFUtGt", "DkW7bUqd1PRcj4CjyzN0rZd2YFM5VycaCF8yndEx");
//        Parse.initialize(this, "zXF58XXifbzQVwdoQNtqMtrf1n4jHGRx5G2MLren", "INAtL4ejjJ5W9cEpsukvxen4mhgBhxIO3wkyDpGI");
//		ParseUser.enableAutomaticUser();
//		ParseACL defaultACL = new ParseACL();
//		// Optionally enable public read access.
//		defaultACL.setPublicReadAccess(true);
//		ParseACL.setDefaultACL(defaultACL, true);



        // Initialize Crash Reporting.
        ParseCrashReporting.enable(this);
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(this, "2H847uPdm5TNwkAlUHB0igF5kw3xXSdcjXzFUtGt", "DkW7bUqd1PRcj4CjyzN0rZd2YFM5VycaCF8yndEx");


        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
	}

}
