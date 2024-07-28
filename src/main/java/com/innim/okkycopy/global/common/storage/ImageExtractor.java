package com.innim.okkycopy.global.common.storage;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageExtractor {
    @Value("#{environment['cloud.aws.s3.bucket']}")
    private String bucketName;
    @Value("#{environment['cloud.aws.s3.folder-path']}")
    private String folderPath;
    @Value("#{environment['cloud.aws.region.static']}")
    private String region;


    public ArrayList<String> extractImageSrc(String html) {
        ArrayList<String> result = new ArrayList<>();

        String imgTagPattern = "<img\\s+([^>]*?)src\\s*=\\s*(['\"])(.*?)\\2";

        Pattern pattern = Pattern.compile(imgTagPattern, Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String src = matcher.group(3);
            result.add(src);
        }

        return result;
    }

    public Long extractImageName(String src) {
        String imgNamePattern = "https://" + bucketName + "\\.s3\\." + region + "\\.amazonaws.com/" + folderPath + "/(\\d+)";
        Pattern pattern = Pattern.compile(imgNamePattern);

        Matcher matcher = pattern.matcher(src);

        if (matcher.matches()) {
            String imageName = matcher.group(1);
            return Long.valueOf(imageName);
        } else {
            return -1L;
        }
    }
}
