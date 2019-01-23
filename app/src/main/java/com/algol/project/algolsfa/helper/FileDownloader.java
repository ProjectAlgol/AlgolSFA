package com.algol.project.algolsfa.helper;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.algol.project.algolsfa.interfaces.DownloadListener;

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
    private DownloadListener downloadListener;
    private File destinationFile;

    /* Custom Download-Errors */
    public static final int ERROR_INVALID_URL = 0, ERROR_BAD_SERVER = 1, ERROR_BAD_NETWORK = 2, ERROR_UNEXPECTED = 3, ERROR_CONNECTION_TIME_OUT = 4, ERROR_INVALID_FILE_DESTINATION = 5, ERROR_SERVER_RESPONSE = 6;
    /* ---------------------- */

    public FileDownloader(Context context, String fileType, DownloadListener downloadListener) {
        this.context = context;
        this.fileType = fileType;
        this.downloadListener = downloadListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void download(String downloadURL, String destination) {
        try {
            destinationFile = new File(destination);
            if (!destinationFile.exists())
                destinationFile.createNewFile();
            new Thread(() -> {
                try {
                    URL fileURL = new URL(downloadURL);
                    try {
                        URLConnection urlConnection = fileURL.openConnection();
                        if (urlConnection instanceof HttpURLConnection) {
                            connect((HttpURLConnection) urlConnection);
                        } else {
                            throw new IOException();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        downloadListener.onDownloadFailed(fileType, ERROR_INVALID_URL);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    downloadListener.onDownloadFailed(fileType, ERROR_INVALID_URL);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            downloadListener.onDownloadFailed(fileType, ERROR_INVALID_FILE_DESTINATION);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void connect(HttpURLConnection connection) {
        try {
            connection.setAllowUserInteraction(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("GET");
            connection.connect(); // blocking call

            // on receiving response
            switch (connection.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    processResponse(connection.getInputStream(), connection.getContentLengthLong());
                    break;
                case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                    downloadListener.onDownloadFailed(fileType, ERROR_CONNECTION_TIME_OUT);
                    break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                case HttpURLConnection.HTTP_NOT_FOUND:
                    downloadListener.onDownloadFailed(fileType, ERROR_BAD_SERVER);
                    break;
                default:
                    downloadListener.onDownloadFailed(fileType, ERROR_UNEXPECTED);
                    break;
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
            downloadListener.onDownloadFailed(fileType, ERROR_UNEXPECTED);
        } catch (IOException e) {
            e.printStackTrace();
            downloadListener.onDownloadFailed(fileType, ERROR_UNEXPECTED);
        }
    }

    private void processResponse(InputStream inputStream, long contentLength) {
        if (inputStream != null) {
            ReadableByteChannel channel = Channels.newChannel(inputStream);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);
                fileOutputStream.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
                fileOutputStream.close();
                inputStream.close();
                if (destinationFile.length() < contentLength) {
                    downloadListener.onDownloadFailed(fileType, ERROR_UNEXPECTED);
                    destinationFile.delete();
                } else {
                    downloadListener.onDownloadComplete(fileType);
                }
            } catch (FileNotFoundException e) {
                downloadListener.onDownloadFailed(fileType, ERROR_INVALID_FILE_DESTINATION);
                e.printStackTrace();
            } catch (IOException e) {
                downloadListener.onDownloadFailed(fileType, ERROR_SERVER_RESPONSE);
                e.printStackTrace();
            }
        }
    }
}
