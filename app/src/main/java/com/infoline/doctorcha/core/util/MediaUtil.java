package com.infoline.doctorcha.core.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.infoline.doctorcha.core.CoreCons;
import com.infoline.doctorcha.presentation.MainCons;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.commons.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;

import static com.infoline.doctorcha.core.util.CommonUtil.getExtName;

/**
 * Created by Administrator on 2016-05-31.
 */
public class MediaUtil {
    //final String[] proj = { MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA };
    public String realPath;
    public int selectedPos;
    public List<Uri> selectedUriList;

    public static boolean isImage(String url) {
        return !url.isEmpty() && MediaType.parse(MediaUtil.getMimeType(url)).type().equals("image");
    }

    public static boolean isVideo(String url) {
        return !url.isEmpty() && MediaType.parse(MediaUtil.getMimeType(url)).type().equals("video");
    }

    public static String getMimeType(String realPath) {
        //1. finename이 한글일 경우 mime = null이므로 encoding해야한다
        //2. filenmane에 '~'이 들어가는 경우 MimeTypeMap.getFileExtensionFromUrl(realPath)는 null을 return 한다
        //String yy = MimeTypeMap.getFileExtensionFromUrl(realPath);
        //return (MimeTypeMap.getSingleton()).getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(Uri.encode(realPath)));

        //image/jpeg, video/mp4
        return (MimeTypeMap.getSingleton()).getMimeTypeFromExtension(getExtName(Uri.encode(realPath)));
    }

    public static MediaType getMediaType(String realPath) {
        return MediaType.parse(MediaUtil.getMimeType(realPath));
    }

    public Intent getGallaryIntent_(final Activity activity, final int intentType) {
        //1. intentType : 1.사진겔러리 2.사진촬영 3.동영상 겔러리 4.동영상 촬영
        if(!PermissionUtil.requestCameraAndWriteStoragePermissions(activity)) {
            return null;
        }

        if(selectedUriList == null) {
            selectedUriList = new ArrayList<>();
        } else {
            selectedUriList.clear();
        }

        final Intent intent;
        final String fileName = CommonUtil.getFormattedDate(CoreCons.EnumDateFormat.UNIQUE_FILENAME);

        switch (intentType) {
            case 0:
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                /////////////////////////////////////////////////intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                break;
            case 1:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                realPath = createRealPathFromFileName(activity, 1, fileName);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputMediaFileUri(activity));

                break;
            case 2:
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                //realPath = createRealPathFromFileName(1, fileName);
                /////////intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputMediaFileUri());
                break;
            case 3:
            default:
                intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                realPath = createRealPathFromFileName(activity, 2, fileName);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputMediaFileUri(activity));

                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 111);
                intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, (long) (1024*1024*200)); //200MB

                /*

                 */
        }

        return intent;
    }

    public Intent getGallaryIntentForPhotoOnly(final Activity activity, boolean allowMultipleChoice) {
        if(!PermissionUtil.requestCameraAndWriteStoragePermissions(activity)) {
            return null;
        }

        final Intent intent;
        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultipleChoice);

        return intent;
    }

    public Intent getPhotoGallaryChooser(boolean multipleChoice) {
        //   data = {Intent@21000} "Intent { dat=content://media/external/images/media/20159 (has extras) }"
        //   data.getData() = {Uri$HierarchicalUri@21117} "content://media/external/images/media/20159"

        if(selectedUriList == null) {
            selectedUriList = new ArrayList<>();
        } else {
            selectedUriList.clear();
        }

        /*
        ES파일탐색가, DropBox, SamsungLink 등등도 다 나온다
        return Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), "Choose an image");
        */

        /*
        //uri parsing시 오류났다 - 시간나면 확인해 볼 것
        final Intent intent = new Intent(MediaStore.ACTION_);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
        startActivityForResult(intent, CN_REQUEST_GALLARY);
        */

        //uri parsing시 오류났다 - 시간나면 확인해 볼 것
        /*
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, CN_REQUEST_GALLARY);
        */

        //1. 유일하게 정상작동 한다
        //2. 한번만, 항상이 안나오고 항상 앱 선택 BottomSheet나온다
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI); //EXTERNAL_CONTENT_URI  --> 둘 다 똑같다. 작동도 정상이다. 무슨 차이점이 있지?
        if(multipleChoice) galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        return Intent.createChooser(galleryIntent, "사진 겔러리 선택");
    }

    public Intent getPhotoGallaryIntent(boolean multipleChoice) {
        if(selectedUriList == null) {
            selectedUriList = new ArrayList<>();
        } else {
            selectedUriList.clear();
        }

        final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //INXTERNAL_CONTENT_URI  --> 둘 다 똑같다. 작동도 정상이다. 무슨 차이점이 있지?
        if(multipleChoice) intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        return Intent.createChooser(intent, "사진 겔러리 선택");
    }

    public Intent getGallaryIntent(final Activity activity, final int intentType) {
        //1. intentType : 1.사진겔러리 2.사진촬영 3.동영상 겔러리 4.동영상 촬영
        if(!PermissionUtil.requestCameraAndWriteStoragePermissions(activity)) {
            return null;
        }

        if(selectedUriList == null) {
            selectedUriList = new ArrayList<>();
        } else {
            selectedUriList.clear();
        }

        final Intent intent;
        final String fileName = CommonUtil.getFormattedDate(CoreCons.EnumDateFormat.UNIQUE_FILENAME);

        switch (intentType) {
            case 0:
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                break;
            case 1:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                realPath = createRealPathFromFileName(activity, 1, fileName);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputMediaFileUri(activity));

                break;
            case 2:
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                //realPath = createRealPathFromFileName(activity, 1, fileName);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputMediaFileUri(activity));
                break;
            case 3:
            default:
                intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                realPath = createRealPathFromFileName(activity, 2, fileName);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, getOutputMediaFileUri(activity));

                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 111);
                intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, (long) (1024*1024*200)); //200MB

                /*

                 */
        }

        return intent;
    }

    public Intent getPhotoGallaryChooser() {
        if(selectedUriList == null) {
            selectedUriList = new ArrayList<>();
        } else {
            selectedUriList.clear();
        }

        /*
        ES파일탐색가, DropBox, SamsungLink 등등도 다 나온다
        return Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), "Choose an image");
        */

        final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
        return Intent.createChooser(intent, "사진 겔러리 선택");
    }

    public Intent getPhotoGallaryOrPhotoCamera(Activity activity, boolean multipleChoice) {
        //1.

        //1. Camera를 선택할 경우
        //   data = Intent { act=inline-data (has extras) }
        //   data.getData() = null
        //   (Bitmap)data.getExtras().get("data")는 촬영한 Bitmap이 반환된다. - 단, 저장은 되지 않은 상태이다.
        //   cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, saveUri)를 특정짓지 않으면 촐영한 사진은 저장되지 않는다.
        //   한글명이 포함된 uri는 3bit code로 변환되며 인식되지 않는다.
        //2. Gallary를 선택할 경우
        //   data = {Intent@21000} "Intent { dat=content://media/external/images/media/20159 (has extras) }"
        //   data.getData() = {Uri$HierarchicalUri@21117} "content://media/external/images/media/20159"

        if(!PermissionUtil.requestCameraAndWriteStoragePermissions(activity)) {
            return null;
        }

        if(selectedUriList == null) {
            selectedUriList = new ArrayList<>();
        } else {
            selectedUriList.clear();
        }

        realPath = createRealPathFromFileName(activity, 1, CommonUtil.getFormattedDate(CoreCons.EnumDateFormat.UNIQUE_FILENAME));

        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(multipleChoice) galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        final Intent chooserIntent = Intent.createChooser(galleryIntent, "겔러리 선택");

        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final Uri saveUri = getOutputMediaFileUri(activity);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);

        //////////////////////cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, saveUri); //
        //EXTRA_OUTPUT을 설정하면 저장은 되지만 return되는 data = null. 즉, uri를 구하지 못하겠다. 공부할 것
        //지정하지 않으면 저장은 되지 않으나 bitmap을 구할 수 있다.

        Intent[] extraIntents = {cameraIntent};
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);

        return chooserIntent;
    }

    public Intent getMediaChooser(final Activity activity) {
        //1. Camera를 선택할 경우
        //   data = Intent { act=inline-data (has extras) }
        //   data.getData() = null
        //   (Bitmap)data.getExtras().get("data")는 촬영한 Bitmap이 반환된다. - 단, 저장은 되지 않은 상태이다.
        //   cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, saveUri)를 특정짓지 않으면 촐영한 사진은 저장되지 않는다.
        //   한글명이 포함된 uri는 3bit code로 변환되며 인식되지 않는다.
        //2. Gallary를 선택할 경우
        //   data = {Intent@21000} "Intent { dat=content://media/external/images/media/20159 (has extras) }"
        //   data.getData() = {Uri$HierarchicalUri@21117} "content://media/external/images/media/20159"

        if(!PermissionUtil.requestCameraAndWriteStoragePermissions(activity)) {
            return null;
        }

        final String fileName = CommonUtil.getFormattedDate(CoreCons.EnumDateFormat.UNIQUE_FILENAME);
        realPath = createRealPathFromFileName(activity, 1, fileName);

        final Intent videoGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);


        final Intent chooserIntent = Intent.createChooser(videoGalleryIntent, "미디어 선택");

        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final Uri saveUri = getOutputMediaFileUri(activity);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);

        //////////////////////cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, saveUri); //
        //EXTRA_OUTPUT을 설정하면 저장은 되지만 return되는 data = null. 즉, uri를 구하지 못하겠다. 공부할 것
        //지정하지 않으면 저장은 되지 않으나 bitmap을 구할 수 있다.


        final Intent photoGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoGalleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        Intent[] extraIntents = {cameraIntent, photoGalleryIntent};
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);

        return chooserIntent;
    }

    /*
    public Intent getMediaChooser() {
        //1. 이 경우 복수개 이미지를 선택할 수 있는 google photo는 나오지 않는다.
        Intent videoIntent = new Intent();
        videoIntent.setAction(Intent.ACTION_GET_CONTENT);
        videoIntent.setType("video/*;image/*");

        return videoIntent;
    }
    */

    /*
    private Uri getOutputMediaFileUri() {
        final File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DoctorCha");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        return Uri.fromFile(new File(realPath));
    }
    */

    private Uri getOutputMediaFileUri(Activity activity) {
        Uri uri = null;

        try {
            final File file = new File(realPath);

            uri = FileProvider.getUriForFile(activity, "com.infoline.doctorcha.provider", file);
        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return uri;
    }

    //Nougat
    private String createRealPathFromFileName(Activity activity, int mediaType, String fileName) {
        //final String xxx = Environment.getExternalStorageDirectory().getAbsolutePath().concat("/test/") + fileName + (mediaType == 1 ? ".jpg" : ".mp4");

        final File storageDir = new File(Environment.getExternalStorageDirectory() + "/DoctorCha/CameraTemp/");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        final String xxx = Environment.getExternalStorageDirectory() + "/DoctorCha/CameraTemp/" + fileName + (mediaType == 1 ? ".jpg" : ".mp4");
       return xxx;
        //return FileProvider.getUriForFile(activity, "com.infoline.doctorcha", file);
    }

    public String getThumbnailPathFromUri(Context ctx, Uri uri) {
        String path = null;
        final String [] proj = {MediaStore.Images.Media._ID};
        final Cursor cursor = ctx.getContentResolver().query(uri, proj, null, null, null);

        if(cursor != null && cursor.moveToFirst()) {
            final int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            final long videoId = cursor.getLong(column_index);


            final String[] proj2 = {MediaStore.Video.Thumbnails.DATA,};
            final Cursor thmbnail_cursor = ctx.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, proj2, MediaStore.Video.Thumbnails.VIDEO_ID + "=?", new String[] { String.valueOf(videoId) }, null);

            if (thmbnail_cursor != null && thmbnail_cursor.moveToFirst()) {
                path = thmbnail_cursor.getString(0);
                thmbnail_cursor.close();
            }

            cursor.close();
        }

        return path;
    }

    /*
    public String getThumbnailPathFromUri(Context ctx, Uri uri) {
        String path = null;
        final String [] proj = {MediaStore.Images.Media._ID};
        final Cursor cursor = ctx.getContentResolver().query(uri, proj, null, null, null);

        if(cursor != null && cursor.moveToFirst()) {
            final int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            final long videoId = cursor.getLong(column_index);
            final Cursor thmbnail_cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(ctx.getContentResolver(), videoId, MediaStore.Images.Thumbnails.MINI_KIND, null);

            if (thmbnail_cursor != null && thmbnail_cursor.moveToFirst()) {
                thmbnail_cursor.moveToFirst();
                path = thmbnail_cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
                thmbnail_cursor.close();
            }

            cursor.close();
        }

        return path;
    }
     */

    /*
    public Bitmap getThumbnailBitmap(Uri uri){
        String[] proj = { MediaStore.Images.Media.DATA };

        // This method was deprecated in API level 11
        // Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        CursorLoader cursorLoader = new CursorLoader(activity, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

        cursor.moveToFirst();
        long imageId = cursor.getLong(column_index);
        //cursor.close();

        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                getContentResolver(), imageId,
                MediaStore.Images.Thumbnails.MINI_KIND,
                (BitmapFactory.Options) null );

        return bitmap;
    }
    */

    /*
    public Uri getLastCaptureImageUri(Context ctx){
        Uri uri = null;

        Cursor cursor = ctx.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, null, null, MediaStore.MediaColumns._ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            uri = Uri.parse(cursor.getString(1)); ///storage/emulated/0/doctorcha/20160601_1329_901.jpg
            //uri = Uri.parse("content://media/external/images/media/" + id);
            cursor.close();
        }

        return Uri.parse("content://media/external/images/media/" + getMediaItemIdFromProvider(ctx, realPath));
    }

    public long getMediaItemIdFromProvider(Context ctx, String _realPath) {
        long id = -1;

        Cursor cursor = ctx.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, MediaStore.MediaColumns.DATA + "=?", new String[] { _realPath }, null);

        if (cursor == null) {
            Log.d("====", "Null cursor for file " + _realPath);
        }
        else {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID));
            }

            cursor.close();
        }

        return id;
    }
    */

    /*
    public void refreshMediaProvider(Context ctx, String fileName) {
        MediaScannerConnection scanner = null;
        try {
            scanner = new MediaScannerConnection(ctx, null);
            scanner.connect();
            try {
                Thread.sleep(200);
            } catch (Exception e) {
            }
            if (scanner.isConnected()) {
                Log.d("refreshMediaProvider", "Requesting scan for file " + fileName);
                scanner.scanFile(fileName, null);
            }
        }
        catch (Exception e) {
            Log.e("refreshMediaProvider", "Cannot to scan file", e);
        }
        finally {
            if (scanner != null) {
                scanner.disconnect();
            }
        }
    }
    */

    /*
    public Uri getLastCaptureImageUri(Context ctx){
        Uri uri = null;

        Cursor cursor = ctx.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, null, null, MediaStore.MediaColumns._ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            uri = Uri.parse(cursor.getString(1)); ///storage/emulated/0/doctorcha/20160601_1329_901.jpg
            //uri = Uri.parse("content://media/external/images/media/" + id);
            cursor.close();
        }

        return uri;
    }
    */



    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getRealPathFromUri(final Context context, final Uri uri) {
//mediakey:/local%3A494b4633-3bb9-4d94-a04e-f86fe6ea087c - 피카사 포토
//content://com.google.android.gallery3d.provider/picasa/item/6203977573269440306 - 피카사 겔러리
//content://com.google.android.apps.photos.contentprovider/0/2/content%3A%2F%2Fmedia%2Fexternal%2Fvideo%2Fmedia%2F21787/ORIGINAL/NONE/1752250702
//content://com.google.android.apps.photos.contentprovider/-1/2/content%3A%2F%2Fmedia%2Fexternal%2Ffile%2F21787/ORIGINAL/NONE/415960013

//com.google.android.apps.photos.contentprovider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //1. picasa sync 관계없이 구글포토로 pickup할 경우
            //  1) 메디아 폴더 : content://com.google.android.apps.photos.contentprovider/0/1/mediakey%3A%2FAF1QipNKr3l4xnXErohtLtWlqfYx2OPQ9O5izwbgH7jO/ORIGINAL/NONE/1170840528
            //  2) 기기폴더 : content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Ffile%2F47/ORIGINAL/NONE/66213262
            final String authority = uri.getAuthority();

            if (authority.equals("com.infoline.doctorcha.provider")) {
                final String fileName = CommonUtil.getFileNameWithExt(uri.toString());
                return Environment.getExternalStorageDirectory() + "/DoctorCha/CameraTemp/" + fileName;
            } else {
                if (authority.equals("com.google.android.apps.photos.contentprovider")) {
                    final String ss = uri.getPathSegments().get(2);

                    if (ss.contains("mediakey")) {
                        final String url = getDataColumnWithAuthority(context, uri, null, null);
                        return getDataColumn(context, Uri.parse(url), null, null);
                    }
                }
            }

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static Uri getContentUriFromImageFile(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static Uri getContentUriFromVideoFile(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    public static int getCameraPhotoOrientation(File imageFile) throws Exception {
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

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String getDataColumnWithAuthority(Context context, Uri uri, String selection, String[] selectionArgs) {
        try {
            final InputStream is = context.getContentResolver().openInputStream(uri);
            final Bitmap bm = BitmapFactory.decodeStream(is);
            final File file = File.createTempFile("googlephoto", ".jpg");
            bm.compress(Bitmap.CompressFormat.JPEG, 100, new BufferedOutputStream(new FileOutputStream(file)));

            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    /*쓸모없는 temp file이 생성된다
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    */

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static File saveBitmaptoJpeg(Bitmap bitmap){
        final File dir1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DoctorCha/VideoThumbnail");
        final String tmpFileName = CommonUtil.getFormattedDate(CoreCons.EnumDateFormat.UNIQUE_FILENAME);

        final String file_pathe = dir1.getAbsolutePath();
        final String file_name = tmpFileName + ".jpg";

        try{
            final FileOutputStream out = new FileOutputStream(file_pathe + "/" + file_name);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.close();

            return new File(dir1.getAbsolutePath(), file_name);

        }catch(FileNotFoundException exception){
            //
        }catch(IOException exception){
            //
        }

        return null;
    }

    public static boolean saveBitmapToLocalFile(final Bitmap bitmap, final String filePath, final String extName){
        final File dir1 = new File(filePath);
        final String fileName = CommonUtil.getFormattedDate(CoreCons.EnumDateFormat.UNIQUE_FILENAME);

        final String file_pathe = dir1.getAbsolutePath();
        final String file_name = fileName + "." + extName;

        try{
            final FileOutputStream out = new FileOutputStream(file_pathe + "/" + file_name);
            bitmap.compress(extName.equals("jpg") ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            return true;

            //return new File(dir1.getAbsolutePath(), file_name);
        } catch(IOException exception){
            return false;
        }
    }

    public static Bitmap createThumbnailFromVideFile(String videoFilePath) {
        return ThumbnailUtils.createVideoThumbnail(videoFilePath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
    }
}

/*
final List<Intent> cameraIntents = new ArrayList<Intent>();
final Intent captureIntent = new Intent(Intent.ACTION_PICK);
final PackageManager packageManager = getActivity().getPackageManager();
final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);

for(ResolveInfo res : listCam) {
    final String packageName = res.activityInfo.packageName;
    final Intent i = new Intent(captureIntent);
    i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
    i.setPackage(packageName);
    //////////////////i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempFile));
    cameraIntents.add(i);
}

final Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
i.addCategory(Intent.CATEGORY_OPENABLE);  //-->머지. 있으나 없으나 결과는 같다
Intent chooserIntent = Intent.createChooser(i,"File Chooser");
chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

startActivityForResult(chooserIntent, CN_REQUEST_PICK_FILE);
SendBird.setAutoBackgroundDetection(false);
 */