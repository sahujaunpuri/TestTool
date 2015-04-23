package amtt.epam.com.amtt.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.ImageView;

/**
 * Created by Artsiom_Kaliaha on 03.04.2015.
 */
public class ImageLoadingTask extends AsyncTask<Void, Void, Bitmap> {

    private final String mPath;
    private final ImageView mImageView;
    private final ImageLoadingCallback mCallback;
    private final int mImageViewWidth;
    private final int mImageViewHeight;

    public ImageLoadingTask(String path, ImageView imageView, int imageViewWidth, int imageViewHeight, @NonNull ImageLoadingCallback callback) {
        mPath = path;
        mImageView = imageView;
        mImageViewWidth = imageViewWidth;
        mImageViewHeight = imageViewHeight;
        mCallback = callback;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPath, options);
        options.inSampleSize = calculateInSampleSize(options);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(mPath, options);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        mCallback.onLoadingFinished(mPath, bitmap);
    }

    private int calculateInSampleSize(BitmapFactory.Options options) {
        int trimmedWidth = options.outWidth;
        int trimmedHeight = options.outHeight;
        int newInSampleSize = 1;

        while (trimmedWidth > mImageViewWidth && trimmedHeight > mImageViewHeight) {
            trimmedWidth /= 2;
            trimmedHeight /= 2;
            newInSampleSize *= 2;
        }

        return newInSampleSize;
    }

}