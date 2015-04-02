package amtt.epam.com.amtt.app;

import amtt.epam.com.amtt.R;
import amtt.epam.com.amtt.authorization.AuthorizationCallback;
import amtt.epam.com.amtt.authorization.AuthorizationResult;
import amtt.epam.com.amtt.authorization.AuthorizationTask;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity implements AuthorizationCallback {

    private EditText userName;
    private EditText password;
    private EditText url;
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";
    public static final String URL = "url";
    public static final String NAME_SP = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.password);
        url = (EditText) findViewById(R.id.jira_url);
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AuthorizationTask(LoginActivity.this, userName.getText().toString(), password.getText().toString(), url.getText().toString(), LoginActivity.this).execute();
            }
        });
    }

    @Override
    public void onAuthorizationResult(AuthorizationResult result) {

        SharedPreferences sharedPreferences = getSharedPreferences(NAME_SP, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME, userName.getText().toString());
        editor.putString(PASSWORD, password.getText().toString());
        editor.putString(URL, url.getText().toString());
        editor.apply();

        String resultMessage = result == AuthorizationResult.AUTHORIZATION_DENIED ? getResources().getString(R.string.authorization_denied) :
            getResources().getString(R.string.authorization_success);
        Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT).show();
    }
}
