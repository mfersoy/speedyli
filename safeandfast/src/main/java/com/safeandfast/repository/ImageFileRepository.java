package com.safeandfast.repository;

import com.safeandfast.domain.ImageFile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import java.util.List;

@Repository
public interface ImageFileRepository extends JpaRepository<ImageFile, String> {

    @EntityGraph(attributePaths = "id")
    List<ImageFile> findAll();

}
