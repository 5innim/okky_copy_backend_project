package com.innim.okkycopy.global.common.storage.image_usage;

import com.innim.okkycopy.global.common.storage.ImageExtractor;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUsageService {

    private final ImageUsageRepository imageUsageRepository;
    private final ImageExtractor imageExtractor;

    @Transactional
    public Long addImageUsage() {
        ImageUsage imageUsage = imageUsageRepository.save(ImageUsage.create());
        return imageUsage.getImageUsageId();
    }

    @Transactional
    public void modifyImageUsages(String content, boolean isInUsage) {
        ArrayList<String> srcList = imageExtractor.extractImageSrc(content);
        for (String src : srcList) {
            long imageName = imageExtractor.extractImageName(src);
            if (imageName != -1) {
                modifyImageUsage(imageName, isInUsage);
            }
        }
    }

    @Transactional
    public void modifyImageUsages(String oldContent, String newContent) {
        HashSet<Long> oldImageSet = new HashSet<>();
        HashSet<Long> notInUse = new HashSet<>();
        for (String src : imageExtractor.extractImageSrc(oldContent)) {
            long imageName = imageExtractor.extractImageName(src);
            if (imageName != -1) {
                oldImageSet.add(imageName);
                notInUse.add(imageName);
            }
        }

        HashSet<Long> newImageSet = new HashSet<>();
        HashSet<Long> inUse = new HashSet<>();
        for (String src : imageExtractor.extractImageSrc(newContent)) {
            long imageName = imageExtractor.extractImageName(src);
            if (imageName != -1) {
                newImageSet.add(imageName);
                inUse.add(imageName);
            }
        }

        notInUse.removeAll(newImageSet);
        inUse.removeAll(oldImageSet);

        for (long imageName : notInUse) {
            modifyImageUsage(imageName, false);
        }

        for (long imageName : inUse) {
            modifyImageUsage(imageName, true);
        }
    }

    @Transactional
    public void changeImage(String oldSrc, String newSrc) {
        if (oldSrc != null) {
            long oldImageName = imageExtractor.extractImageName(oldSrc);
            if (oldImageName != -1) {
                modifyImageUsage(oldImageName, false);
            }
        }

        if (newSrc != null) {
            long newImageName = imageExtractor.extractImageName(newSrc);
            if (newImageName != -1) {
                modifyImageUsage(newImageName, true);
            }
        }
    }

    @Transactional
    public void removeImageUsagesByIsInUseAndCreatedDate(boolean isInUse, LocalDateTime date) {
        imageUsageRepository.deleteByIsInUseAndCreatedDate(isInUse, date);
    }
    
    @Transactional(readOnly = true)
    public List<ImageUsage> findImageUsagesByIsInUseAndCreatedDate(boolean isInUse, LocalDateTime date) {
        return imageUsageRepository.findByIsInUseAndCreatedDate(isInUse, date);
    }

    @Transactional
    public void modifyImageUsage(long imageUsageId, boolean isInUse) {
        ImageUsage imageUsage = imageUsageRepository.findByImageUsageId(imageUsageId).orElseThrow(
            () -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_IMAGE)
        );

        imageUsage.setIsInUse(isInUse);
    }

    @Transactional
    public void modifyImageUsage(String src, boolean isInUse) {
        if (src != null) {
            long imageName = imageExtractor.extractImageName(src);
            if (imageName != -1) {
                ImageUsage imageUsage = imageUsageRepository.findByImageUsageId(imageName).orElseThrow(
                    () -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_IMAGE)
                );
                imageUsage.setIsInUse(isInUse);
            }
        }
    }
}
