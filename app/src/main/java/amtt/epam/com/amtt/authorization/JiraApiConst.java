package amtt.epam.com.amtt.authorization;

/**
 * Created by Artsiom_Kaliaha on 15.04.2015.
 */
public interface JiraApiConst {

    //Paths
    String BASE_PATH = "/rest/auth/latest/";
    String LOGIN_PATH = BASE_PATH + "session";
    String ISSUE_PATH = "/rest/api/2/issue/";
    String USER_PROJECTS_PATH = "/rest/api/2/issue/createmeta";

    //Header keys
    String AUTH = "Authorization";
    String CONTENT_TYPE = "content-type";

    //Header values
    String APPLICATION_JSON = "application/json";
    String BASIC_AUTH = "Basic ";

    //Response codes
    int BAD_GATE_WAY = 502;

}
