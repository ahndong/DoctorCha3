package com.infoline.doctorcha.core.listener;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

/**
 * Created by Administrator on 2016-06-02.
 */
public class MediaScanFileHelper {
    public interface OnMediaScanListener{
        void onError();
        void onSuccess(Uri uri);
    }

    private MediaScannerConnection conn;
    private Context context;

    public MediaScanFileHelper(Context context){
        this.context = context;
    }

    public void scanFile(final File file, final OnMediaScanListener lis){
        if(conn!=null)
            conn.disconnect();

        if(!file.isFile()){
            lis.onError();
            return;
        }

        conn = new MediaScannerConnection(context,new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
                conn.scanFile(file.getAbsolutePath(), null);
            }

            public void onScanCompleted(String arg0, Uri arg1) {
                conn.disconnect();
                if(arg1==null){
                    lis.onError();
                }

                lis.onSuccess(arg1);
            }});
        conn.connect();
    }
}
