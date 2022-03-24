package de.seinab.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadUtils {
    private static final Logger log = LoggerFactory.getLogger(DownloadUtils.class);


    public static ResponseEntity<InputStreamResource> getResponseEntity(InputStream inputStream, HttpHeaders headers) {
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
    }

    public static ResponseEntity<InputStreamResource> getResponseEntity(File file, HttpHeaders headers) {
        try {
            return getResponseEntity(new FileInputStream(file), headers);
        } catch (FileNotFoundException e) {
            log.info("Error getting ResponseEntity for file: {}", file.getAbsolutePath());
        }
        return null;
    }

    public static HttpHeaders getHttpHeaders(String filename, String fileType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/" + fileType));
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return headers;
    }

    public static HttpHeaders getHttpHeaders(String filename) {
        String fileType = getFileType(filename);
        return getHttpHeaders(filename, fileType);
    }

    public static String getFileType(String filename) {
        Matcher matcher = Pattern.compile(".*\\.(.*)").matcher(filename);
        String fileType = "";
        if (matcher.matches()) {
            fileType = matcher.group(1);
        }
        return fileType;
    }
}
