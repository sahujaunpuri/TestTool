package amtt.epam.com.amtt.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import amtt.epam.com.amtt.R;
import amtt.epam.com.amtt.topbutton.service.TopButtonService;
import amtt.epam.com.amtt.util.FileUtil;
import amtt.epam.com.amtt.util.UIUtil;

/**
 * Created by Ivan_Bakach on 09.06.2015.
 */
public class PreviewActivity extends Activity{

    public static final String FILE_PATH = "filePath";
    public static final double RATIO_SCALE_DIALOG_MARGIN = 0.8;
    private ImageView imagePreview;
    private TextView textPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater factory = LayoutInflater.from(getBaseContext());
        @SuppressLint("InflateParams")
        final View view = factory.inflate(R.layout.activity_preview, null);
        imagePreview = (ImageView) view.findViewById(R.id.image_preview);
        textPreview = (TextView) view.findViewById(R.id.text_preview);

        initPreviewImage();

        String title = "";
        Bundle extra = getIntent().getExtras();
        if (extra!=null) {
            String filePath = extra.getString(FILE_PATH);
            title = FileUtil.getFileName(filePath);
            showPreview(filePath);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(title)
                .setView(view)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TopButtonService.sendActionChangeVisibilityTopbutton(false);
    }

    private void initPreviewImage() {
        if (UIUtil.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            imagePreview.getLayoutParams().width = (int) (width * RATIO_SCALE_DIALOG_MARGIN);
            imagePreview.getLayoutParams().height = (int) (height * RATIO_SCALE_DIALOG_MARGIN);
        }else{
            imagePreview.setScaleType(ImageView.ScaleType.FIT_START);
        }
    }

    private CharSequence readLogFromFile(String filePath){
        File file = new File(filePath);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if(line.contains("E/")){
                    append(builder, line, new ForegroundColorSpan(Color.RED), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append("\n");
                } else if (line.contains("W/")) {
                    append(builder, line, new ForegroundColorSpan(Color.BLUE), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append("\n");
                }
                else {
                    builder.append(line);
                    builder.append("\n");
                }
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return builder;
    }

    public void showPreview(String filePath){
        if(filePath.contains(".png")||filePath.contains(".jpg")||filePath.contains(".jpeg")){
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage("file:///"+filePath, imagePreview);
        }else if (filePath.contains(".txt")) {
            int sizeDp = 8;
            textPreview.setPadding(sizeInDp(sizeDp), sizeInDp(sizeDp), sizeInDp(sizeDp), sizeInDp(sizeDp));
            textPreview.setText(readLogFromFile(filePath));
        }
    }

    public int sizeInDp(int px){
        float scale = getResources().getDisplayMetrics().density;
        return (int) (px*scale + 0.5f);
    }

    public SpannableStringBuilder append(SpannableStringBuilder spannableString, CharSequence text, Object what, int flags) {
        int start = spannableString.length();
        spannableString.append(text);
        spannableString.setSpan(what, start, spannableString.length(), flags);
        return spannableString;
    }
}