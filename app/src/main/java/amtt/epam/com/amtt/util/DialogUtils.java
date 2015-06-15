package amtt.epam.com.amtt.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import amtt.epam.com.amtt.R;
import amtt.epam.com.amtt.http.Request;
import amtt.epam.com.amtt.os.Task;
import amtt.epam.com.amtt.os.Task.AsyncTaskCallback;

/**
 * Created by Artsiom_Kaliaha on 29.04.2015.
 */
public class DialogUtils {

    /**
     * Builds dialogs for ExceptionHandler class
     */
    public static class Builder <ResultType> {

        private AlertDialog.Builder mBuilder;

        public Builder(Context context) {
            mBuilder = new AlertDialog.Builder(context);
        }

        public Builder setTitle(int titleId) {
            mBuilder.setTitle(titleId);
            return this;
        }

        public Builder setMessage(int messageId) {
            mBuilder.setMessage(messageId);
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder setPositiveButton(int textId, final Request request, final AsyncTaskCallback callback) {
            if (textId != Constants.Dialog.EMPTY_FIELD) {
                DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Task.Builder<>()
                                .setExecutable(request)
                                .setCallback(callback)
                                .createAndExecute();
                    }
                };
                mBuilder.setPositiveButton(textId, positiveListener);
            }
            return this;

        }

        public Builder setNeutralButton(int textId, DialogInterface.OnClickListener listener) {
            if (textId != Constants.Dialog.EMPTY_FIELD && listener != null) {
                mBuilder.setNeutralButton(textId, listener);
            }
            return this;
        }

        public Builder setNegativeButton() {
            mBuilder.setNegativeButton(R.string.error_button_close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            return this;
        }

        public void createAndShow() {
            mBuilder.create().show();
        }

    }

}
