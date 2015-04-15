package amtt.epam.com.amtt.authorization;

import android.util.Base64;

import org.apache.http.HttpEntity;

import java.util.HashMap;
import java.util.Map;

import amtt.epam.com.amtt.processing.AuthResponseProcessor;

/**
 * Created by Artsiom_Kaliaha on 24.03.2015.
 */
public class JiraApi {

    private AuthorizationResult mAuthResult;

    public RestResponse authorize(final String userName, final String password, final String url) {
        String credentials = JiraApiConst.BASIC_AUTH + Base64.encodeToString((userName + ":" + password).getBytes(), Base64.NO_WRAP);
        Map<String, String> headers = new HashMap<>();
        headers.put(JiraApiConst.AUTH, credentials);

        RestResponse<AuthorizationResult> restResponse;
        try {
            restResponse = new RestMethod.Builder()
                    .setType(RestMethodType.GET)
                    .setUrl(url + JiraApiConst.LOGIN_PATH)
                    .setHeadersMap(headers)
                    .setResponseProcessor(new AuthResponseProcessor())
                    .create()
                    .execute();
        } catch (Exception e) {
            mAuthResult = AuthorizationResult.DENIED;
            restResponse = new RestResponse<AuthorizationResult>();
            restResponse.setResult();
            restResponse.setMessage("Authorization isn't passed: " + e.getMessage());
            return restResponse;
        }

        //Bad gate way is considered as a httpResponse, not an exception
        if (restResponse != null && restResponse.getStatusCode() == JiraApiConst.BAD_GATE_WAY) {
            restResponse.setMessage("Authorization isn't passed: bad gateway");
            return restResponse;
        }

        mAuthResult = AuthorizationResult.SUCCESS;
        return restResponse;
    }

    public RestResponse createIssue(final String userName, final String password, final String url, final String jsonString) {
        String credentials = JiraApiConst.BASIC_AUTH + Base64.encodeToString((userName + ":" + password).getBytes(), Base64.NO_WRAP);
        Map<String, String> headers = new HashMap<>();
        headers.put(JiraApiConst.AUTH, credentials);
        headers.put(JiraApiConst.CONTENT_TYPE, JiraApiConst.APPLICATION_JSON);

        RestResponse restResponse;
        try {
            restResponse = new RestMethod.Builder()
                    .setType(RestMethodType.POST)
                    .setUrl(url + JiraApiConst.ISSUE_PATH)
                    .setHeadersMap(headers)
                    .setJsonString(jsonString)
                    .setResponseProcessor(new AuthResponseProcessor())
                    .create()
                    .execute();
        } catch (Exception e) {
            restResponse = new RestResponse();
            restResponse.setMessage("Authorization isn't passed: " + e.getMessage());
        }
        return restResponse;
    }

    public HttpEntity searchIssue(final String userName, final String password, final String url) throws Exception {
        String credentials = JiraApiConst.BASIC_AUTH + Base64.encodeToString((userName + ":" + password).getBytes(), Base64.NO_WRAP);
        Map<String, String> headers = new HashMap<>();
        headers.put(JiraApiConst.AUTH, credentials);

        RestResponse restResponse;
        try {
            restResponse = new RestMethod.Builder()
                    .setType(RestMethodType.GET)
                    .setUrl(url + JiraApiConst.USER_PROJECTS_PATH)
                    .setHeadersMap(headers)
                    .setResponseProcessor(new AuthResponseProcessor())
                    .create()
                    .execute();
        } catch (Exception e) {
            restResponse = new RestResponse();
            restResponse.setMessage(e);
        }
        return restResponse.getEntity();
    }

}


