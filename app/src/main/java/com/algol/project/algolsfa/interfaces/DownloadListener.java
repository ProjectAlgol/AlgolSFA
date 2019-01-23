package com.algol.project.algolsfa.interfaces;

/**
 * Created by swarnavo.dutta on 1/23/2019.
 */

public interface DownloadListener {
    void onDownloadComplete(String fileType);
    void onDownloadFailed(String fileType, int error);
}
