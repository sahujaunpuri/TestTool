package amtt.epam.com.amtt.api;

import android.os.AsyncTask;

import amtt.epam.com.amtt.api.rest.RestResponse;
import amtt.epam.com.amtt.processing.ProjectsProcessor;
import amtt.epam.com.amtt.processing.UserInfoProcessor;
import amtt.epam.com.amtt.util.CredentialsManager;

/**
 * AsyncTask which is directly used in code to perform async requests to Jira api
 * Created by Artsiom_Kaliaha on 17.04.2015.
 */
public class JiraTask<ResultType,ResultObjectType> extends AsyncTask<Object, Void, RestResponse<ResultType,ResultObjectType>> {

    public enum JiraSearchType {

        ISSUE,
        USER_INFO

    }

    public enum JiraTaskType {

        AUTH,
        CREATE_ISSUE,
        SEARCH

    }

    public static class Builder<ResultType,ResultObjectType> {

        private JiraTaskType mOperationType;
        private JiraCallback<ResultType,ResultObjectType> mCallback;
        private String mPostMessage;
        private JiraSearchType mSearchType;

        public Builder setOperationType(JiraTaskType operationType) {
            mOperationType = operationType;
            return this;
        }

        public Builder setCallback(JiraCallback<ResultType,ResultObjectType> callback) {
            mCallback = callback;
            return this;
        }

        public Builder setPostMessage(String json) {
            mPostMessage = json;
            return this;
        }

        public Builder setSearchType(JiraSearchType jiraSearchType) {
            mSearchType = jiraSearchType;
            return this;
        }

        public JiraTask create() {
            JiraTask<ResultType,ResultObjectType> jiraTask = new JiraTask<>();
            jiraTask.mOperationType = this.mOperationType;
            jiraTask.mCallback = this.mCallback;
            jiraTask.mJson = this.mPostMessage;
            jiraTask.mSearchType = this.mSearchType;
            return jiraTask;
        }

    }

    private JiraTaskType mOperationType;
    private JiraCallback<ResultType,ResultObjectType> mCallback;
    private String mJson;
    private JiraSearchType mSearchType;

    private JiraTask() {
    }

    @Override
    @SuppressWarnings("unchecked")
    protected RestResponse<ResultType,ResultObjectType> doInBackground(Object... params) {
        RestResponse<ResultType,ResultObjectType> restResponse = null;

        switch (mOperationType) {
            case AUTH:
                //TODO we create class everyTime we perform call? Why do you are afraid of singltones?
                restResponse = new JiraApi().authorize();
                break;
            case CREATE_ISSUE:
                restResponse = new JiraApi().createIssue(mJson);
                break;
            case SEARCH:

                switch (mSearchType) {
                    case ISSUE:
                        restResponse = new JiraApi().searchData(JiraApiConst.USER_PROJECTS_PATH, new ProjectsProcessor());
                        break;
                    case USER_INFO:
                        String requestSuffix = JiraApiConst.USER_INFO_PATH +  CredentialsManager.getInstance().getUserName()  + JiraApiConst.EXPAND_GROUPS);
                        restResponse = new JiraApi().searchData(requestSuffix, new UserInfoProcessor());
                        break;
                }
                break;
        }
        return restResponse;
    }

    @Override
    protected void onPostExecute(RestResponse<ResultType,ResultObjectType> restResponse) {
        mCallback.onJiraRequestPerformed(restResponse);
    }

}
