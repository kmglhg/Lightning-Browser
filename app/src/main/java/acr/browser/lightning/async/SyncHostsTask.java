package acr.browser.lightning.async;

import android.app.Application;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import acr.browser.lightning.constant.Constants;
import ch.boye.httpclientandroidlib.util.ByteArrayBuffer;

public class SyncHostsTask extends AsyncTask<String, Void, Void> {

    private static final String TAG = SyncHostsTask.class.getSimpleName();
    @NonNull private final Application mContext;

    public SyncHostsTask(@NonNull Application context) {
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ByteArrayBuffer baf = null;

        try {
            URL url = new URL(Constants.HOSTS_URL);

//            String fileName = Uri.parse(Constants.HOSTS_URL).getLastPathSegment();
            String fileName = "hosts.txt";

            File file = new File(mContext.getFilesDir(), fileName);

            if (file.exists()) {
                file.delete();
            }

            URLConnection uConn = url.openConnection();
            uConn.connect();

            bis = new BufferedInputStream(url.openStream());

            baf = new ByteArrayBuffer(5000);

            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.flush();

            fos.close();
            baf.clear();
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
                if (baf != null) baf.clear();
                if (bis != null) bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
