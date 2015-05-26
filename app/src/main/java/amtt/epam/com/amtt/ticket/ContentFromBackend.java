package amtt.epam.com.amtt.ticket;

import amtt.epam.com.amtt.api.JiraApi;
import amtt.epam.com.amtt.api.JiraApiConst;
import amtt.epam.com.amtt.api.JiraCallback;
import amtt.epam.com.amtt.api.JiraTask;
import amtt.epam.com.amtt.api.exception.AmttException;
import amtt.epam.com.amtt.api.rest.RestMethod;
import amtt.epam.com.amtt.api.rest.RestResponse;
import amtt.epam.com.amtt.bo.JProjectsResponse;
import amtt.epam.com.amtt.bo.JPriorityResponse;
import amtt.epam.com.amtt.bo.JUserAssignableResponse;
import amtt.epam.com.amtt.bo.JVersionsResponse;
import amtt.epam.com.amtt.processing.PriorityProcessor;
import amtt.epam.com.amtt.processing.ProjectsProcessor;
import amtt.epam.com.amtt.processing.UsersAssignableProcessor;
import amtt.epam.com.amtt.processing.VersionsProcessor;

/**
 * Created by Iryna_Monchanka on 15.05.2015.
 */
public class ContentFromBackend {

    private static class ContentFromBackendHolder {
        public static final ContentFromBackend INSTANCE = new ContentFromBackend();
    }

    public static ContentFromBackend getInstance() {
        return ContentFromBackendHolder.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public void getMetaAsynchronously(final ContentLoadingCallback<JProjectsResponse> contentLoadingCallback,
                                      final JiraGetContentCallback jiraGetContentCallback) {
        RestMethod<JProjectsResponse> searchMethod = JiraApi.getInstance().buildDataSearch(JiraApiConst.USER_PROJECTS_PATH,
            new ProjectsProcessor(),
            null,
            null,
            null);
        new JiraTask.Builder<JProjectsResponse>()
            .setRestMethod(searchMethod)
            .setCallback(new JiraCallback() {
                @Override
                public void onRequestStarted() {
                }

                @Override
                public void onRequestPerformed(RestResponse restResponse) {
                    contentLoadingCallback.resultFromBackend((JProjectsResponse) restResponse.getResultObject(),
                            JiraContentConst.META_RESPONSE, jiraGetContentCallback);
                }

                @Override
                public void onRequestError(AmttException e) {
                    contentLoadingCallback.resultFromBackend(null, JiraContentConst.META_RESPONSE, jiraGetContentCallback);
                }
            })
            .createAndExecute();
    }

    @SuppressWarnings("unchecked")
    public void getVersionsAsynchronously(String projectsKey,
                                          final ContentLoadingCallback<JVersionsResponse> contentLoadingCallback,
                                          final JiraGetContentCallback jiraGetContentCallback) {
        String path = JiraApiConst.PROJECT_VERSIONS_PATH + projectsKey + JiraApiConst.PROJECT_VERSIONS_PATH_V;
        RestMethod<JVersionsResponse> searchMethod = JiraApi.getInstance().buildDataSearch(path, new VersionsProcessor(), null, null, null);
        new JiraTask.Builder<JVersionsResponse>()
            .setRestMethod(searchMethod)
            .setCallback(new JiraCallback() {
                @Override
                public void onRequestStarted() {
                }

                @Override
                public void onRequestPerformed(RestResponse restResponse) {
                    contentLoadingCallback.resultFromBackend((JVersionsResponse) restResponse.getResultObject(),
                            JiraContentConst.VERSIONS_RESPONSE, jiraGetContentCallback);
                }

                @Override
                public void onRequestError(AmttException e) {
                    contentLoadingCallback.resultFromBackend(null, JiraContentConst.VERSIONS_RESPONSE, jiraGetContentCallback);
                }
            })
            .createAndExecute();
    }

    @SuppressWarnings("unchecked")
    public void getUsersAssignableAsynchronously(String projectKey, String userName,
                                                 final ContentLoadingCallback<JUserAssignableResponse> contentLoadingCallback,
                                                 final JiraGetContentCallback jiraGetContentCallback) {
        String path = JiraApiConst.USERS_ASSIGNABLE_PATH + projectKey + JiraApiConst.USERS_ASSIGNABLE_PATH_UN + userName + JiraApiConst.USERS_ASSIGNABLE_PATH_MR;
        RestMethod<JUserAssignableResponse> searchMethod = JiraApi.getInstance().buildDataSearch(path, new UsersAssignableProcessor(), null, null, null);
        new JiraTask.Builder<JUserAssignableResponse>()
            .setRestMethod(searchMethod)
            .setCallback(new JiraCallback() {
                @Override
                public void onRequestStarted() {
                }

                @Override
                public void onRequestPerformed(RestResponse restResponse) {
                    contentLoadingCallback.resultFromBackend((JUserAssignableResponse) restResponse.getResultObject(),
                            JiraContentConst.USERS_ASSIGNABLE_RESPONSE, jiraGetContentCallback);
                }

                @Override
                public void onRequestError(AmttException e) {
                    contentLoadingCallback.resultFromBackend(null, JiraContentConst.USERS_ASSIGNABLE_RESPONSE, jiraGetContentCallback);
                }
            })
            .createAndExecute();
    }

    @SuppressWarnings("unchecked")
    public void getPriorityAsynchronously(final ContentLoadingCallback<JPriorityResponse> contentLoadingCallback,
                                          final JiraGetContentCallback jiraGetContentCallback) {
        String path = JiraApiConst.PROJECT_PRIORITY_PATH;
        RestMethod<JPriorityResponse> searchMethod = JiraApi.getInstance().buildDataSearch(path, new PriorityProcessor(), null, null, null);
        new JiraTask.Builder<JPriorityResponse>()
            .setRestMethod(searchMethod)
            .setCallback(new JiraCallback() {
                @Override
                public void onRequestStarted() {
                }

                @Override
                public void onRequestPerformed(RestResponse restResponse) {
                    contentLoadingCallback.resultFromBackend((JPriorityResponse) restResponse.getResultObject(),
                            JiraContentConst.PRIORITIES_RESPONSE, jiraGetContentCallback);
                }

                @Override
                public void onRequestError(AmttException e) {
                    contentLoadingCallback.resultFromBackend(null, JiraContentConst.PRIORITIES_RESPONSE, jiraGetContentCallback);
                }

            })
            .createAndExecute();
    }


    @SuppressWarnings("unchecked")
    public void createIssueAsynchronously(String issueJson, final ContentLoadingCallback<Boolean> contentLoadingCallback,
                                          final JiraGetContentCallback jiraGetContentCallback) {
        RestMethod<JProjectsResponse> createIssue = JiraApi.getInstance().buildIssueCreating(issueJson);
        new JiraTask.Builder<JProjectsResponse>()
            .setRestMethod(createIssue)
            .setCallback(new JiraCallback() {
                @Override
                public void onRequestStarted() {
                }

                @Override
                public void onRequestPerformed(RestResponse restResponse) {
                    contentLoadingCallback.resultFromBackend(true, JiraContentConst.CREATE_ISSUE_RESPONSE, jiraGetContentCallback);
                }

                @Override
                public void onRequestError(AmttException e) {
                    contentLoadingCallback.resultFromBackend(false, JiraContentConst.CREATE_ISSUE_RESPONSE, jiraGetContentCallback);
                }
            })
            .createAndExecute();
    }

}