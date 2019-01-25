/*
* AsyncDownloader helps to download files asynchronously (in background thread) and return the results back to the UI thread.
* @Constructor Params: Context, type of file to be downloaded (Database, app etc), callback listener (DownloadListener)
* */
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
    protected DownloadStatus doInBackground(String[] params)
    /*
    * downloads the find in background thread
    * */
    {
        FileDownloader fileDownloader = new FileDownloader(context, fileType);
        return fileDownloader.download(params[0], params[1]); // Download URL, File Destination
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(DownloadStatus downloadStatus)
    /*
    * Based on the download completion status (Failed or succeeded), it invokes the callback methods in the UI thread.
    * On Download failed, the error is also passed to the callback.
    * */
    {
        if (downloadStatus.getStatus() == FileDownloader.DOWNLOAD_SUCCESS)
            downloadListener.onDownloadComplete(downloadStatus.getFileType());
        else
            downloadListener.onDownloadFailed(downloadStatus.getFileType(), downloadStatus.getStatus());
    }
}
