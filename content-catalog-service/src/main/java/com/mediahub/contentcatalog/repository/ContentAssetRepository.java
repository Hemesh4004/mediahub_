package com.mediahub.contentcatalog.repository;

import com.mediahub.contentcatalog.entity.ContentAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentAssetRepository extends JpaRepository<ContentAsset, Integer> {}