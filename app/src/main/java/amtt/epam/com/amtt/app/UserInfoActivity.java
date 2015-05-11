package amtt.epam.com.amtt.app;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import amtt.epam.com.amtt.CoreApplication;
import amtt.epam.com.amtt.R;
import amtt.epam.com.amtt.api.JiraApi;
import amtt.epam.com.amtt.api.JiraApiConst;
import amtt.epam.com.amtt.api.JiraCallback;
import amtt.epam.com.amtt.api.JiraTask;
import amtt.epam.com.amtt.api.exception.AmttException;
import amtt.epam.com.amtt.api.exception.ExceptionHandler;
import amtt.epam.com.amtt.api.rest.RestMethod;
import amtt.epam.com.amtt.api.rest.RestResponse;
import amtt.epam.com.amtt.api.result.JiraOperationResult;
import amtt.epam.com.amtt.bo.issue.user.JiraUserInfo;
import amtt.epam.com.amtt.contentprovider.AmttContentProvider;
import amtt.epam.com.amtt.database.table.UsersTable;
import amtt.epam.com.amtt.processing.UserInfoProcessor;
import amtt.epam.com.amtt.service.TopButtonService;
import amtt.epam.com.amtt.util.ActiveUser;
import amtt.epam.com.amtt.util.Constants.Str;
import amtt.epam.com.amtt.view.TextView;

/**
 * Created by Artsiom_Kaliaha on 07.05.2015.
 */
public class UserInfoActivity extends BaseActivity implements JiraCallback<JiraUserInfo>, LoaderCallbacks<Cursor> {

    private TextView mName;
    private TextView mEmailAddress;
    private TextView mDisplayName;
    private TextView mTimeZone;
    private TextView mLocale;
    private ActiveUser mUser;
    private ImageView mUserImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        TopButtonService.close(this);
        initViews();
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TopButtonService.start(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_change_user:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_refresh_user_info:
                showProgress(true);
                String requestSuffix = JiraApiConst.USER_INFO_PATH + ActiveUser.getInstance().getUserName() + JiraApiConst.EXPAND_GROUPS;
                RestMethod<JiraUserInfo> userInfoMethod = JiraApi.getInstance().buildDataSearch(requestSuffix,
                        new UserInfoProcessor(),
                        null,
                        null,
                        null);
                new JiraTask.Builder<JiraUserInfo>()
                        .setRestMethod(userInfoMethod)
                        .setCallback(UserInfoActivity.this)
                        .createAndExecute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initViews() {
        mName = (TextView) findViewById(R.id.user_name);
        mEmailAddress = (TextView) findViewById(R.id.user_email);
        mDisplayName = (TextView) findViewById(R.id.user_display_name);
        mTimeZone = (TextView) findViewById(R.id.user_time_zone);
        mLocale = (TextView) findViewById(R.id.user_locale);
        mUserImage = (ImageView) findViewById(R.id.user_image);
    }

    private void updateUserInfo(JiraUserInfo user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UsersTable._DISPLAY_NAME, user.getDisplayName());
        contentValues.put(UsersTable._TIME_ZONE, user.getTimeZone());
        contentValues.put(UsersTable._LOCALE, user.getLocale());
        contentValues.put(UsersTable._KEY, user.getKey());
        contentValues.put(UsersTable._EMAIL, user.getEmailAddress());
        contentValues.put(UsersTable._AVATAR_16, user.getAvatarUrls().getAvatarXSmallUrl());
        contentValues.put(UsersTable._AVATAR_24, user.getAvatarUrls().getAvatarSmallUrl());
        contentValues.put(UsersTable._AVATAR_32, user.getAvatarUrls().getAvatarMediumUrl());
        contentValues.put(UsersTable._AVATAR_48, user.getAvatarUrls().getAvatarUrl());

        getContentResolver().update(AmttContentProvider.USER_CONTENT_URI,
                contentValues,
                UsersTable._USER_NAME + "=?",
                new String[] { mUser.getUserName()});
    }

    //Callback
    //Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                AmttContentProvider.USER_CONTENT_URI,
                UsersTable.PROJECTION,
                UsersTable._USER_NAME + "=?",
                new String[]{ActiveUser.getInstance().getUserName()},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mUser = ActiveUser.getInstance();
        data.moveToFirst();
        String email = data.getString(data.getColumnIndex(UsersTable._EMAIL));
        String displayName = data.getString(data.getColumnIndex(UsersTable._DISPLAY_NAME));
        String timeZone = data.getString(data.getColumnIndex(UsersTable._TIME_ZONE));
        String locale = data.getString(data.getColumnIndex(UsersTable._LOCALE));
        String imageUrl = data.getString(data.getColumnIndex(UsersTable._AVATAR_48));
        mName.setText(mUser.getUserName());
        mDisplayName.setText(displayName);
        mEmailAddress.setText(email);
        mTimeZone.setText(timeZone);
        mLocale.setText(locale);
        CoreApplication.getImageLoader().displayImage(imageUrl, mUserImage);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //Jira
    @Override
    public void onRequestStarted() {
        showProgress(true);
    }

    @Override
    public void onRequestPerformed(RestResponse<JiraUserInfo> restResponse) {
        if (restResponse.getOpeartionResult() == JiraOperationResult.REQUEST_PERFORMED) {
            JiraUserInfo user = restResponse.getResultObject();
            mName.setText(user.getName());
            mEmailAddress.setText(user.getEmailAddress());
            mDisplayName.setText(user.getDisplayName());
            mTimeZone.setText(user.getTimeZone());
            mLocale.setText(user.getLocale());
            updateUserInfo(user);
            showProgress(false);
        }
    }

    @Override
    public void onRequestError(AmttException e) {
        ExceptionHandler.getInstance().processError(e).showDialog(this, UserInfoActivity.this);
        showProgress(false);
    }

}
