package com.safeandfast.repository;

import com.safeandfast.domain.Car;
import com.safeandfast.domain.Reservation;
import com.safeandfast.domain.User;
import com.safeandfast.domain.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>  {


    boolean existsByCar(Car car);

    boolean existsByUser(User user);

    @EntityGraph(attributePaths = {"car", "car.image", "user"})
    Optional<Reservation> findById(Long id);

    @EntityGraph(attributePaths = {"car", "car.image"})
    List<Reservation> findAll();

    @EntityGraph(attributePaths = {"car", "car.image"})
    Page<Reservation> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"car", "car.image", "user"})
    Page<Reservation> findAllByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"car", "car.image", "user"})
    Optional<Reservation> findByIdAndUser(Long id, User user);

    @EntityGraph(attributePaths = {"car", "user"})
    List<Reservation> findAllBy();

    @Query("SELECT r FROM Reservation r "
            + "JOIN FETCH Car c on r.car=c.id WHERE "
            + "c.id=:carId and (r.status not in :status) and :pickUpTime BETWEEN r.pickUpTime and r.dropOffTime "
            + "or "
            + "c.id=:carId and (r.status not in :status) and :dropOffTime BETWEEN r.pickUpTime and r.dropOffTime "
            + "or "
            + "c.id=:carId and (r.status not in :status) and (r.pickUpTime BETWEEN :pickUpTime and :dropOffTime)")
    List<Reservation> checkCarStatus(@Param("carId") Long carId, @Param("pickUpTime") LocalDateTime pickUpTime,
                                     @Param("dropOffTime") LocalDateTime dropOffTime, @Param("status") ReservationStatus[] status);

}
