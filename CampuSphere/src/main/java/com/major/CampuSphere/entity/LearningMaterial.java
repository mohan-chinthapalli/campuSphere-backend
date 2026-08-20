package com.major.CampuSphere.entity;

import com.major.CampuSphere.enums.MaterialType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "learning_materials")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LearningMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "material_key", nullable = false, unique = true, length = 100)
    private String materialKey;

    @Column(nullable = false, length = 500)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "material_type", nullable = false, length = 20)
    private MaterialType materialType;

    @Column(name = "subject_code", nullable = false, length = 20)
    private String subjectCode;

    @Column(nullable = false)
    private int semester;

    @Column(length = 100)
    private String author;

    @Column(name = "file_size", length = 20)
    private String fileSize;

    @Column(name = "download_count", nullable = false)
    @Builder.Default
    private int downloadCount = 0;

    @Column(name = "file_path", length = 1000)
    private String filePath;

    @Column(length = 10)
    private String emoji;

    @Column(length = 255)
    private String gradient;

    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MaterialProgress> progressRecords = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
