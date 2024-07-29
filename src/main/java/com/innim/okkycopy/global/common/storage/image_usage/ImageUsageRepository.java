package com.innim.okkycopy.global.common.storage.image_usage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImageUsageRepository extends JpaRepository<ImageUsage, Long> {

    Optional<ImageUsage> findByImageUsageId(long imageUsageId);

    @Query("SELECT i from ImageUsage i WHERE i.isInUse = :isInUse AND i.createdDate < :date")
    List<ImageUsage> findByIsInUseAndCreatedDate(boolean isInUse, LocalDateTime date);

    @Query("DELETE from ImageUsage i WHERE i.isInUse = :isInUse AND i.createdDate < :date")
    void deleteByIsInUseAndCreatedDate(boolean isInUse, LocalDateTime date);

}
