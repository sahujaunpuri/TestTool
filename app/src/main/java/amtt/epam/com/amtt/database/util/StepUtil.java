package amtt.epam.com.amtt.database.util;

import android.content.ComponentName;

import java.util.List;

import amtt.epam.com.amtt.bo.database.ActivityMeta;
import amtt.epam.com.amtt.bo.database.Step;
import amtt.epam.com.amtt.bo.user.JUserInfo;
import amtt.epam.com.amtt.database.object.DbObjectManager;
import amtt.epam.com.amtt.database.object.IResult;
import amtt.epam.com.amtt.database.table.UsersTable;

/**
 @author Artsiom_Kaliaha
 @version on 16.05.2015
 */

public class StepUtil {

    public static void saveStep(ComponentName componentName, String mScreenPath){
        Step step = new Step(componentName, mScreenPath);
        DbObjectManager.INSTANCE.add(step, null);
    }

    public static void saveActivityMeta(ActivityMeta activityMeta){
        DbObjectManager.INSTANCE.add(activityMeta, null);
    }

    public static void cleanStep() {
        Step.restartStepNumber();
        DbObjectManager.INSTANCE.removeAll(new Step());
    }

    public static void cleanActivityMeta() {
        DbObjectManager.INSTANCE.removeAll(new ActivityMeta());
    }

    public static void clearAllStep(){
        cleanStep();
        cleanActivityMeta();
    }

    public static void checkUser(String userName, IResult<List<JUserInfo>> result) {
        DbObjectManager.INSTANCE.query(new JUserInfo(), null, new String[]{UsersTable._USER_NAME}, new String[]{userName}, result);
    }

}