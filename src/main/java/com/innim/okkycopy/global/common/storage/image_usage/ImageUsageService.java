package com.innim.okkycopy.global.common.storage.image_usage;

import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public Long addImageUsage() {
        ImageUsage imageUsage = imageUsageRepository.save(ImageUsage.create());
        return imageUsage.getImageUsageId();
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
