package com.safeandfast.mapper;

import com.safeandfast.domain.ImageFile;
import com.safeandfast.domain.Reservation;
import com.safeandfast.domain.User;
import com.safeandfast.dto.ReservationDTO;
import com.safeandfast.dto.request.ReservationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    Reservation reservationRequestToReservation(ReservationRequest reservationRequest);

    @Mapping(source = "user", target = "userId", qualifiedByName = "getUserId")
    @Mapping(source = "car.image", target = "car.image", qualifiedByName = "getImageAsString")
    ReservationDTO reservationToReservationDTO(Reservation reservation);

    List<ReservationDTO> map(List<Reservation> reservationList);

    @Named("getImageAsString")
    public static Set<String> getImageIds(Set<ImageFile> imageFiles) {
        Set<String> imgs = new HashSet<>();
        imgs = imageFiles.stream().map(imFile -> imFile.getId().toString()).collect(Collectors.toSet());
        return imgs;
    }

    @Named("getUserId")
    public static Long getUserId(User user) {
        return user.getId();
    }

}
