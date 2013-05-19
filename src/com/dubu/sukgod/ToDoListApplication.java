package com.dubu.sukgod;

import com.parse.Parse;
import com.parse.ParseACL;

import com.parse.ParseUser;

import android.app.Application;

public class ToDoListApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

        Parse.initialize(this, "2H847uPdm5TNwkAlUHB0igF5kw3xXSdcjXzFUtGt", "DkW7bUqd1PRcj4CjyzN0rZd2YFM5VycaCF8yndEx");
		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		// Optionally enable public read access.
		defaultACL.setPublicReadAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);
	}

}
