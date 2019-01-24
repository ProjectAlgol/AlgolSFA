package com.algol.project.algolsfa.handlers;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.algol.project.algolsfa.helper.FileDownloader;
import com.algol.project.algolsfa.interfaces.DownloadListener;
import com.algol.project.algolsfa.pojos.DownloadStatus;

/**
 * Created by swarnavo.dutta on 1/24/2019.
 */

public class DownloadHandler extends Handler {
    private Context context;
    private DownloadListener downloadListener;

    public DownloadHandler(Context context, DownloadListener downloadListener) {
        this.context= context;
        this.downloadListener= downloadListener;
    }

    @Override
    public void handleMessage(Message message) {
        DownloadStatus downloadStatus = (DownloadStatus) message.obj;
        if (downloadStatus.getStatus() == FileDownloader.DOWNLOAD_SUCCESS)
            downloadListener.onDownloadComplete(downloadStatus.getFileType());
        else
            downloadListener.onDownloadFailed(downloadStatus.getFileType(), downloadStatus.getStatus());
    }
}
