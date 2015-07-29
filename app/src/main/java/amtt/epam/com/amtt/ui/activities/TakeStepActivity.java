package amtt.epam.com.amtt.ui.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import amtt.epam.com.amtt.R;
import amtt.epam.com.amtt.broadcastreceiver.GlobalBroadcastReceiver;
import amtt.epam.com.amtt.topbutton.service.TopButtonService;
import amtt.epam.com.amtt.util.PreferenceUtil;

/**
 * @author Ivan_Bakach
 * @version on 29.07.2015
 */
public class TakeStepActivity extends Activity {
    public static final String EXTERANL_ACTION_TAKE_SCREENSHOT = "TAKE_SCREENSHOT";
    public static final String TAKE_ONLY_INFO = "TAKE_ONLY_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TopButtonService.sendActionChangeTopButtonVisibility(false);
        LinearLayout rootLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_take_screen, null);
        final CheckBox takeActivityInfo = (CheckBox) rootLayout.findViewById(R.id.cb_take_activity_info);
        final CheckBox takeScreen = (CheckBox) rootLayout.findViewById(R.id.cb_take_screen);
        takeActivityInfo.setChecked(PreferenceUtil.getBoolean(getString(R.string.checkbox_activity_info_key)));
        takeScreen.setChecked(PreferenceUtil.getBoolean(getString(R.string.checkbox_screen_key)));

        final AlertDialog takeStepDialog = new AlertDialog.Builder(this, R.style.Dialog)
                .setTitle(getString(R.string.take_step_dialog_title))
                .setMessage(getString(R.string.take_step_dialog_message))
                .setView(rootLayout)
                .setNegativeButton(getString(android.R.string.cancel), null)
                .setPositiveButton(getString(R.string.take_step_dialog_positive_button), null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        TopButtonService.sendActionChangeTopButtonVisibility(true);
                    }
                }).create();
        takeStepDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button takeButton = takeStepDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                takeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!takeActivityInfo.isChecked() && takeScreen.isChecked()) {
                            Intent intent = new Intent();
                            intent.setAction(EXTERANL_ACTION_TAKE_SCREENSHOT);
                            sendBroadcast(intent);
                            TopButtonService.sendActionChangeTopButtonVisibility(false);
                            GlobalBroadcastReceiver.setStepWithoutActivityInfo(true);
                            saveCheckboxValue(takeActivityInfo.isChecked(),takeScreen.isChecked());
                            takeStepDialog.dismiss();
                        } else if (takeActivityInfo.isChecked() && !takeScreen.isChecked()) {
                            Intent intent = new Intent();
                            intent.setAction(TAKE_ONLY_INFO);
                            sendBroadcast(intent);
                            saveCheckboxValue(takeActivityInfo.isChecked(), takeScreen.isChecked());
                            takeStepDialog.dismiss();
                        } else if (takeActivityInfo.isChecked() && takeScreen.isChecked()) {
                            Intent intent = new Intent();
                            intent.setAction(EXTERANL_ACTION_TAKE_SCREENSHOT);
                            sendBroadcast(intent);
                            saveCheckboxValue(takeActivityInfo.isChecked(), takeScreen.isChecked());
                            takeStepDialog.dismiss();
                        } else {
                            Toast.makeText(getBaseContext(),getString(R.string.take_step_dialog_error_message), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Button closeButton = takeStepDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TopButtonService.sendActionChangeTopButtonVisibility(true);
                        takeStepDialog.dismiss();
                    }
                });
            }
        });
        takeStepDialog.show();
    }

    private void saveCheckboxValue(boolean activityInfoIsChecked, boolean screenIsChecked){
        PreferenceUtil.putBoolean(getString(R.string.checkbox_activity_info_key), activityInfoIsChecked);
        PreferenceUtil.putBoolean(getString(R.string.checkbox_screen_key), screenIsChecked);
    }
}
