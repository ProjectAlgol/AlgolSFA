package com.algol.project.algolsfa.helper;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.algol.project.algolsfa.pojos.DownloadStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by swarnavo.dutta on 1/23/2019.
 */

public class FileDownloader {
    private Context context;
    private String fileType;
    private File destinationFile;

    /* Custom Download-Errors */
    public static final int ERROR_INVALID_URL = 0, ERROR_BAD_SERVER = 1, ERROR_BAD_NETWORK = 2, ERROR_UNEXPECTED = 3, ERROR_CONNECTION_TIME_OUT = 4, ERROR_INVALID_FILE_DESTINATION = 5, ERROR_SERVER_RESPONSE = 6;
    public static final int DOWNLOAD_SUCCESS= 7;
    /* ---------------------- */

    public FileDownloader(Context context, String fileType) {
        this.context = context;
        this.fileType = fileType;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public DownloadStatus download(String downloadURL, String destination) {
        try {
            destinationFile = new File(destination);
            if (!destinationFile.exists())
                destinationFile.createNewFile();
            try {
                URL fileURL = new URL(downloadURL);
                try {
                    URLConnection urlConnection = fileURL.openConnection();
                    if (urlConnection instanceof HttpURLConnection) {
                        return connect((HttpURLConnection) urlConnection);
                    } else {
                        throw new IOException();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return new DownloadStatus(fileType,ERROR_INVALID_URL);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return new DownloadStatus(fileType,ERROR_INVALID_URL);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new DownloadStatus(fileType,ERROR_INVALID_FILE_DESTINATION);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private DownloadStatus connect(HttpURLConnection connection) {
        try {
            connection.setAllowUserInteraction(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("GET");
            connection.connect(); // blocking call

            // on receiving response
            switch (connection.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    return processResponse(connection.getInputStream(), connection.getContentLengthLong());
                case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                    return new DownloadStatus(fileType,ERROR_CONNECTION_TIME_OUT);
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return new DownloadStatus(fileType,ERROR_BAD_SERVER);
                default:
                    return new DownloadStatus(fileType,ERROR_UNEXPECTED);
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
            return new DownloadStatus(fileType,ERROR_UNEXPECTED);
        } catch (IOException e) {
            e.printStackTrace();
            return new DownloadStatus(fileType,ERROR_UNEXPECTED);
        }
    }

    private DownloadStatus processResponse(InputStream inputStream, long contentLength) {
        if (inputStream != null) {
            ReadableByteChannel channel = Channels.newChannel(inputStream);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);
                fileOutputStream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
                fileOutputStream.close();
                inputStream.close();
                if (destinationFile.length() < contentLength) {
                    destinationFile.delete();
                    return new DownloadStatus(fileType,ERROR_UNEXPECTED);
                } else {
                    return new DownloadStatus(fileType,DOWNLOAD_SUCCESS);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return new DownloadStatus(fileType,ERROR_INVALID_FILE_DESTINATION);
            } catch (IOException e) {
                e.printStackTrace();
                return new DownloadStatus(fileType,ERROR_SERVER_RESPONSE);
            }
        }
        else {
            return new DownloadStatus(fileType,ERROR_SERVER_RESPONSE);
        }
    }
}
