package com.innim.okkycopy.global.common.storage.image_usage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "image_usage")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ImageUsage {

    @Id
    @Column(name = "image_usage_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageUsageId;

    @Column(name = "is_in_use", nullable = false)
    private Boolean isInUse;

    @Column(name = "created_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    public static ImageUsage create() {
        return ImageUsage.builder()
            .isInUse(false)
            .build();
    }

}
