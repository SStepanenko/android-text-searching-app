package com.sstepanenko.textsearchingapp.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.sstepanenko.textsearchingapp.data.DownloadedFile;
import com.sstepanenko.textsearchingapp.errors.FileDownloadException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public final class IoUtils {

    private static final String INTERNAL_DOWNLOADS_FOLDER_NAME = "downloads";
    private static final String DOWNLOADED_FILE_NAME = "downloaded_file";

    private static final int BUFFER_SIZE = 4096;

    private IoUtils() {
    }

    @NonNull
    public static DownloadedFile downloadFile(@NonNull Context context, @NonNull String fileUrl) throws FileDownloadException {
        createInternalDownloadsFolderIfNotExist(context);
        File downloadedFile = deleteDownloadedFileIfExists(context);
        URL url = createUrl(fileUrl);
        try(InputStream inputStream = openUrlStream(url);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            FileOutputStream fileOutputStream = createFileOutputStream(downloadedFile)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = readFromBufferedInputStream(bufferedInputStream, buffer)) != -1) {
                writeToFileOutputStream(fileOutputStream, buffer, bytesRead);
            }
            return new DownloadedFile(fileUrl, downloadedFile.getAbsolutePath());
        } catch (IOException exception) {
            throw new FileDownloadException("Some error occurred on file downloading", exception,
                    FileDownloadException.ErrorType.UNKNOWN_ERROR);
        }
    }

    @NonNull
    public static File getDownloadedFile(@NonNull Context context) {
        File downloadsFolder = new File(context.getFilesDir(), INTERNAL_DOWNLOADS_FOLDER_NAME);
        return new File(downloadsFolder, DOWNLOADED_FILE_NAME);
    }

    private static void createInternalDownloadsFolderIfNotExist(@NonNull Context context) {
        File downloadsFolder = new File(context.getFilesDir(), INTERNAL_DOWNLOADS_FOLDER_NAME);
        if(!downloadsFolder.exists()){
            if (!downloadsFolder.mkdir()) {
                throw new FileDownloadException("Failed to create internal folder: " + downloadsFolder.getAbsolutePath(),
                        FileDownloadException.ErrorType.CREATE_DOWNLOADS_FOLDER_ERROR);
            }
        }
    }

    @NonNull
    private static File deleteDownloadedFileIfExists(@NonNull Context context) {
        File file = getDownloadedFile(context);
        if (file.exists()){
            if(!file.delete()) {
                throw new FileDownloadException("Failed to delete file: " + file.getAbsolutePath(),
                        FileDownloadException.ErrorType.DELETE_DOWNLOADED_FILE_ERROR);

            }
        }
        return file;
    }

    @NonNull
    private static URL createUrl(@NonNull String urlStr) {
        try {
            return new URL(urlStr);
        } catch (MalformedURLException exception) {
            throw new FileDownloadException("Invalid URL format: " + urlStr, exception,
                    FileDownloadException.ErrorType.INVALID_URL_FORMAT);
        }
    }

    @NonNull
    private static InputStream openUrlStream(@NonNull URL url) {
        try {
            return url.openStream();
        } catch (IOException exception) {
            throw new FileDownloadException("Failed to open input stream for URL", exception,
                    FileDownloadException.ErrorType.OPEN_URL_STREAM_ERROR);
        }
    }

    @NonNull
    private static FileOutputStream createFileOutputStream(@NonNull File file) {
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException exception) {
            throw new FileDownloadException("Failed to open output file", exception,
                    FileDownloadException.ErrorType.OPEN_OUTPUT_FILE_ERROR);
        } catch (SecurityException exception) {
            throw new FileDownloadException("Failed to open output file: security error", exception,
                    FileDownloadException.ErrorType.OPEN_OUTPUT_FILE_SECURITY_ERROR);
        }
    }

    private static int readFromBufferedInputStream(@NonNull BufferedInputStream inputStream, @NonNull byte[] buffer) {
        try {
            return inputStream.read(buffer);
        } catch (IOException exception) {
            throw new FileDownloadException("Failed read URL data", exception,
                    FileDownloadException.ErrorType.READ_URL_DATA_ERROR);
        }
    }

    private static void writeToFileOutputStream(@NonNull FileOutputStream outputStream, @NonNull byte[] bytes, int bytesCount) {
        try {
            outputStream.write(bytes, 0, bytesCount);
        } catch (IOException exception) {
            throw new FileDownloadException("Failed write data to output file stream", exception,
                    FileDownloadException.ErrorType.WRITE_URL_DATA_ERROR);
        }
    }

    private static boolean deleteFileIfExists(@NonNull File file) {
        boolean result = true;
        if (file.exists()){
            result = file.delete();
        }
        return result;
    }

    public interface DownloadProgressHandler {

        void onBytesDownloaded(int bytesCount, int totalBytesCount);

    }
}
