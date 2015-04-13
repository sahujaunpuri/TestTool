package amtt.epam.com.amtt.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import amtt.epam.com.amtt.R;
import amtt.epam.com.amtt.asynctask.ShowUserDataTask;
import amtt.epam.com.amtt.bo.issue.createmeta.JMetaResponse;
import amtt.epam.com.amtt.callbacks.ShowUserDataCallback;
import amtt.epam.com.amtt.crash.AmttExceptionHandler;
import amtt.epam.com.amtt.database.task.DataBaseOperationType;
import amtt.epam.com.amtt.database.task.DataBaseTask;
import amtt.epam.com.amtt.database.task.DbClearTask;
import amtt.epam.com.amtt.database.task.StepSavingCallback;
import amtt.epam.com.amtt.database.task.DataBaseTaskResult;
import amtt.epam.com.amtt.service.TopButtonService;
import amtt.epam.com.amtt.util.Converter;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends BaseActivity implements StepSavingCallback, ShowUserDataCallback {
    private SharedPreferences sharedPreferences;
    private Boolean accessCreateIssue;
    private Button issueButton;
    private int mScreenNumber = 1;
    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";
    private static final String URL = "url";
    private static final String NAME_SP = "data";
    private static final String VOID = "";
    private static final String PROJECTS_NAMES = "projectsNames";
    private static final String ACCESS = "access";
    private static final String PROJECTS_KEYS = "projectsKeys";

    private static int sStepNumber;
    private DataBaseTask mDataBaseClearTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        mDataBaseClearTask = new DataBaseTask.Builder()
                .setOperationType(DataBaseOperationType.CLEAR)
                .setContext(MainActivity.this)
                .create();
        mDataBaseClearTask.execute();

        startService(new Intent(this, TopButtonService.class));
        TopButtonService.show(this);
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        Button crashButton = (Button) findViewById(R.id.crash_button);
        crashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new IllegalStateException("stakkato caused crash");
            }
        });

        Thread.currentThread().setUncaughtExceptionHandler(new AmttExceptionHandler(this));


        Button clearDbButton = (Button) findViewById(R.id.clear_db_button);
        clearDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sStepNumber = 0;
                mDataBaseClearTask.execute();
            }
        });
        sharedPreferences = getSharedPreferences(NAME_SP, MODE_PRIVATE);
        accessCreateIssue = sharedPreferences.getBoolean(ACCESS, false);
        issueButton = (Button) findViewById(R.id.issue_act_button);
        issueButton.setEnabled(accessCreateIssue);
        
        Button stepButton = (Button) findViewById(R.id.step_button);
        stepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rootView = getWindow().getDecorView();
                rootView.setDrawingCacheEnabled(true);
                Bitmap bitmap = rootView.getDrawingCache();
                Rect rect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                sStepNumber++;

                new DataBaseTask.Builder()
                        .setOperationType(DataBaseOperationType.SAVE_STEP)
                        .setContext(MainActivity.this)
                        .setBitmap(bitmap)
                        .setRect(rect)
                        .setComponentName(MainActivity.this.getComponentName())
                        .setStepNumber(sStepNumber)
                        .create()
                        .execute();
            }
        });

        Button showStepsButton = (Button) findViewById(R.id.show_steps_button);
        showStepsButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StepsActivity.class));
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        accessCreateIssue = sharedPreferences.getBoolean(ACCESS, false);
        issueButton = (Button) findViewById(R.id.issue_act_button);
        issueButton.setEnabled(accessCreateIssue);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    @Override
    public int getScreenNumber() {
        return mScreenNumber;
    }


    @Override
    public void onDataBaseActionDone(DataBaseTaskResult result) {
        int resultMessage = result == DataBaseTaskResult.ERROR ? R.string.data_base_action_error : R.string.data_base_action_done;
        Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void incrementScreenNumber() {
        mScreenNumber++;
    }

    public void onIssueClick(View view) {
        String username, password, url;
        username = sharedPreferences.getString(USER_NAME, VOID);
        password = sharedPreferences.getString(PASSWORD, VOID);
        url = sharedPreferences.getString(URL, VOID);
        new ShowUserDataTask(username, password, url, MainActivity.this).execute();
    }


    @Override
    public void onShowUserDataResult(JMetaResponse result) {
        ArrayList<String> projectsNames = result.getProjectsNames();
        ArrayList<String> projectsKeys = result.getProjectsKeys();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(PROJECTS_NAMES, Converter.arrayListToSet(projectsNames));
        editor.putStringSet(PROJECTS_KEYS, Converter.arrayListToSet(projectsKeys));
        editor.apply();
        startActivity(new Intent(this, CreateIssueActivity.class));
    }
}
