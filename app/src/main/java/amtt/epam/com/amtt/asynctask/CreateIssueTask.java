package amtt.epam.com.amtt.asynctask;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;

import amtt.epam.com.amtt.authorization.JiraApi;
import amtt.epam.com.amtt.bo.issue.willrefactored.CreationIssueResult;
import amtt.epam.com.amtt.callbacks.CreationIssueCallback;

/**
 * Created by Irina Monchenko on 27.03.2015.
 */
public class CreateIssueTask extends AsyncTask<Void, Void, String> {

    private final CreationIssueCallback mCallback;
    private final String mJson;
    private final String mUserName;
    private final String mPassword;
    private final String mUrl;

    private Exception mException;

    public CreateIssueTask(String username, String userPassword, String url, String json, CreationIssueCallback callback) {
        mUserName = username;
        mPassword = userPassword;
        mCallback = callback;
        mJson = json;
        mUrl = url;
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpResponse httpResponse;
        try {
            httpResponse = new JiraApi().createIssue(mUserName, mPassword, mUrl, mJson);
//            if (JiraApi.STATUS_CREATED != new JiraApi().createIssue(mUserName, mPassword, mUrl, mJson)) {
//                throw new AuthenticationException("issue can`t be create");
//            }
        } catch (Exception e) {
            mException = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String responseMessage) {
        mCallback.onCreationIssueResult(responseMessage);
    }
}
