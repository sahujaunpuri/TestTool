package com.example.ivan_bakach.testpermission;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.test.InstrumentationTestCase;
import android.util.Log;

import junit.framework.Assert;

/**
 * Created by Ivan_Bakach on 29.06.2015.
 */
public class MonitorTest extends InstrumentationTestCase implements Application.ActivityLifecycleCallbacks{

    public Application mApplication;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    TestBroadcastReceiver receiver = new TestBroadcastReceiver();

    public void testMonitor() {
        final Context context = getInstrumentation().getTargetContext();
        LogManger.writeMultipleLogs();
        mApplication = (Application)getInstrumentation().getTargetContext().getApplicationContext();
        mApplication.registerActivityLifecycleCallbacks(this);
        IntentFilter filterReceiver = new IntentFilter();
        filterReceiver.addCategory(TestBroadcastReceiver.CATEGORY);
        filterReceiver.addAction(TestBroadcastReceiver.PING_ANSWER);
        filterReceiver.addAction(TestBroadcastReceiver.CLOSE_TEST);
        filterReceiver.addAction(TestBroadcastReceiver.TAKE_SCREENSHOT);
        context.registerReceiver(receiver, filterReceiver);
        receiver.setCloseUnitTest(false);

        while (!receiver.needCloseUnitTest()) {
            try {
                Thread.sleep(1000);
                if (receiver.needCloseUnitTest()) {
                    context.unregisterReceiver(receiver);
                    mApplication.unregisterActivityLifecycleCallbacks(this);
                    Assert.assertTrue(true);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
//        Log.d("TAG","Scream "+activity.getComponentName().getClassName());
        receiver.setActivity(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
