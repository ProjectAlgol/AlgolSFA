package com.algol.project.algolsfa.pojos;

/**
 * Created by swarnavo.dutta on 1/24/2019.
 */

public class DownloadStatus {
    String fileType;
    int status;

    public DownloadStatus(String fileType, int status) {
        this.fileType = fileType;
        this.status = status;
    }

    public String getFileType() {
        return fileType;
    }

    public int getStatus() {
        return status;
    }
}
