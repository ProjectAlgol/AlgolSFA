package com.algol.project.algolsfa.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.algol.project.algolsfa.helper.FileDownloader;
import com.algol.project.algolsfa.interfaces.DownloadListener;

import com.algol.project.algolsfa.pojos.DownloadStatus;


/**
 * Created by swarnavo.dutta on 1/25/2019.
 */

public class AsyncDownloader extends AsyncTask<String, String, DownloadStatus> {
    private Context context;
    private DownloadListener downloadListener;
    private String fileType;

    public AsyncDownloader(Context context, String fileType, DownloadListener downloadListener) {
        this.context = context;
        this.downloadListener = downloadListener;
        this.fileType = fileType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected DownloadStatus doInBackground(String[] params) {
        FileDownloader fileDownloader = new FileDownloader(context, fileType);
        return fileDownloader.download(params[0], params[1]); // Download URL, File Destination
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(DownloadStatus downloadStatus) {
        if (downloadStatus.getStatus() == FileDownloader.DOWNLOAD_SUCCESS)
            downloadListener.onDownloadComplete(downloadStatus.getFileType());
        else
            downloadListener.onDownloadFailed(downloadStatus.getFileType(), downloadStatus.getStatus());
    }
}
