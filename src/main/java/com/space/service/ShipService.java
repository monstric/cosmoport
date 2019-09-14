package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

public interface ShipService {

    List<Ship> getAllShips(Specification<Ship> specification);

    Page<Ship> getAllShips(Specification<Ship> specification, Pageable sortedByName);

    Ship createShip(Ship ship);

    Ship updateShip(String id, Ship ship);

    void deleteShip(String id);

    Ship getShipById(String id);

    Specification<Ship> filterByName(String name);

    Specification<Ship> filterByPlanet(String planet);

    Specification<Ship> filterByProdDate(Long dateBefore, Long dateAfter);

    Specification<Ship> filterByCrewSize(Integer crewSizeFrom, Integer creSizeTo);

    Specification<Ship> filterBySpeed(Double speedFrom, Double speedTo);

    Specification<Ship> filterByShipType(ShipType shipType);

    Specification<Ship> filterByUsed(Boolean isUsed) ;

    Specification<Ship> filterByRating (Double ratingFrom, Double ratingTo) ;

}