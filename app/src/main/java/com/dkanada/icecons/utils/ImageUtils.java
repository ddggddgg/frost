package com.dkanada.icecons.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.dkanada.icecons.async.BitmapLoadTask;
import com.dkanada.icecons.interfaces.BitmapListener;

public class ImageUtils {

    public static void bitmapLoadAsync(final ImageView imageView, final Resources resources, int resId, int width, int height) {
        BitmapLoadTask task = new BitmapLoadTask(resources, resId, width, height, new BitmapListener() {
            @Override
            public void onBitmap(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
                imageView.setAlpha(0f);
                imageView.setVisibility(View.VISIBLE);
                imageView.animate()
                        .alpha(1f)
                        .setDuration(1000)
                        .setListener(null);
            }
        });

        task.execute();
    }

    public static Bitmap bitmapLoad(Resources res, int resId, int width, int height) {
        BitmapFactory.Options resOptions = new BitmapFactory.Options();
        resOptions.inJustDecodeBounds = true;

        // load appropriately sampled bitmap from given resource
        BitmapFactory.decodeResource(res, resId, resOptions);

        int resHeight = resOptions.outHeight;
        int resWidth = resOptions.outWidth;

        float xScale = (float) width / (float) resWidth;
        float yScale = (float) height / (float) resHeight;
        float scale = Math.max(xScale, yScale);
        if (scale > 1) {
            // do nothing
        }
        if (width == 0) {
            width = Math.round(resWidth / scale);
        } else if (height == 0) {
            height = Math.round(resHeight / scale);
        }

        resOptions.inSampleSize = sampleSize(scale);
        resWidth /= resOptions.inSampleSize;
        resHeight /= resOptions.inSampleSize;
        resOptions.inJustDecodeBounds = false;

        Bitmap rawBitmap = BitmapFactory.decodeResource(res, resId, resOptions);

        // compare aspect ratio and crop
        rawBitmap = bitmapCrop(rawBitmap, width, height, resWidth, resHeight);

        // scale to desired size
        return Bitmap.createScaledBitmap(rawBitmap, width, height, true);
    }

    // calc sample size for scaled resource loading
    private static int sampleSize(float scale) {
        int size = 1;
        while (scale < 0.5f) {
            size *= 2;
            scale *= 2;
        }
        return size;
    }

    // crop bitmap to chosen aspect ratio
    private static Bitmap bitmapCrop(Bitmap rawBitmap, int width, int height, int resWidth, int resHeight) {
        int cropX, cropY, cropWidth, cropHeight;

        float xScale = (float) width / (float) resWidth;
        float yScale = (float) height / (float) resHeight;
        float scale = Math.max(xScale, yScale);

        if (xScale >= yScale) {
            cropWidth = Math.round(resWidth);
            cropX = 0;
            cropHeight = Math.round(height / scale);
            cropY = (resHeight - cropHeight) / 2;
        } else {
            cropWidth = Math.round(width / scale);
            cropX = (resWidth - cropWidth) / 2;
            cropHeight = Math.round(resHeight);
            cropY = 0;
        }
        return Bitmap.createBitmap(rawBitmap, cropX, cropY, cropWidth, cropHeight);
    }
}