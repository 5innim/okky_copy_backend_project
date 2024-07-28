package com.innim.okkycopy.global.common.storage.image_usage;

import com.innim.okkycopy.global.common.storage.ImageExtractor;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
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

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public Long addImageUsage() {
        ImageUsage imageUsage = imageUsageRepository.save(ImageUsage.create());
        return imageUsage.getImageUsageId();
    }

    @Transactional
    public void modifyImageUsages(String content) {
        ArrayList<String> srcList = imageExtractor.extractImageSrc(content);
        for (String src : srcList) {
            long imageName = imageExtractor.extractImageName(src);
            if (imageName != -1) {
                modifyImageUsage(imageName, true);
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
    public void removeImageUsage(long imageUsageId) {
        Optional<ImageUsage> optional = imageUsageRepository.findByImageUsageId(imageUsageId);
        if (optional.isPresent()) {
            ImageUsage imageUsage = optional.get();
            imageUsage.remove(entityManager);
        } else {
            log.error("not exist image_usage_id: " + imageUsageId);
        }
    }

    @Transactional
    public void modifyImageUsage(long imageUsageId, boolean isInUse) {
        ImageUsage imageUsage = imageUsageRepository.findByImageUsageId(imageUsageId).orElseThrow(
            () -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_IMAGE)
        );

        imageUsage.setIsInUse(isInUse);
    }
}
