package com.swinblockchain.bluetoothbeacon;

import android.app.Application;
import android.content.Context;

/**
 * The App class is used to get the application context in awkward situations
 *
 * @author John Humphrys
 */
public class App extends Application{


        private static Application sApplication;

        public static Application getApplication() {
            return sApplication;
        }

        public static Context getContext() {
            return getApplication().getApplicationContext();
        }

        @Override
        public void onCreate() {
            super.onCreate();
            sApplication = this;

    }
}
