package InstanceLib;

/**
 * Created by Administrator on 2016-12-29.
 * 1. Universal Image Loader -> ImageLoadingListener 확장
 * 2. SedBird에 의해 임의로 Ladscape로 저장된 사진을 원래대로 복원하요 Display
 *
 * http://stackoverflow.com/questions/25250274/why-are-portrait-images-displayed-rotated-using-imageloader
 */

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

/**
 * Created by kiwitech on 30/8/16.
 */
public class ImageRotationListener implements ImageLoadingListener {

    @Override
    public void onLoadingStarted(String imageUri, View view) {
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        try {
            if(loadedImage != null){
                final File file = ImageLoader.getInstance().getDiskCache().get(imageUri);

                if(file == null){
                    return;
                }

                final int rotation = getCameraPhotoOrientation(file);
                Bitmap bitmap = null;

                if(rotation != 0) {
                    bitmap = resizeBitmap(loadedImage, 1000, 1000);

                    if(bitmap != null){
                        if(view instanceof ImageView){
                            ((ImageView) view).setImageBitmap(bitmap);
                        }
                    }
                }
                view.setBackgroundDrawable(null);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
    }

    public Bitmap resizeBitmap( Bitmap input, int destWidth, int destHeight )
    {
        int srcWidth = input.getWidth();
        int srcHeight = input.getHeight();
        boolean needsResize = false;
        float p;
        if (srcWidth > destWidth || srcHeight > destHeight) {
            needsResize = true;
            if ( srcWidth > srcHeight && srcWidth > destWidth) {
                p = (float)destWidth / (float)srcWidth;
                destHeight = (int)( srcHeight * p );
            }
            else {
                p = (float)destHeight / (float)srcHeight;
                destWidth = (int)( srcWidth * p );
            }
        }
        else {
            destWidth = srcWidth;
            destHeight = srcHeight;
        }
        if (needsResize) {
            Bitmap output = Bitmap.createScaledBitmap( input, destWidth, destHeight, true );
            return output;
        }
        else {
            return input;
        }
    }

    public int getCameraPhotoOrientation(File imageFile) throws Exception {
        int rotate = 0;
        try {
            if(imageFile.exists()){
                ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
                int orientation = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                switch (orientation) {
                    case ExifInterface.ORIENTATION_NORMAL:
                        rotate = 0;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }
}