package com.safeandfast.repository;

import com.safeandfast.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("Select count(*) from Car c join c.image im where im.id=:id")
    Integer findCarCountByImageId(@Param("id") String id);



}
