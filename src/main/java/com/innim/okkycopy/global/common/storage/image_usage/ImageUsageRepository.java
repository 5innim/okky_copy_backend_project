package com.innim.okkycopy.global.common.storage.image_usage;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageUsageRepository extends JpaRepository<ImageUsage, Long> {

    Optional<ImageUsage> findByImageUsageId(long imageUsageId);

}
