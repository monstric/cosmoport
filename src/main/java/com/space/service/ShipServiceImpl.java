package com.space.service;

import com.space.BadRequestException;
import com.space.ShipNotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ShipServiceImpl implements ShipService {
    @Autowired
    private ShipRepository shipRepository;

    @Override
    public Page<Ship> getAllShips(Specification<Ship> specification, Pageable sortedByField) {
        return shipRepository.findAll(specification, sortedByField);
    }

    @Override
    public List<Ship> getAllShips(Specification<Ship> specification) {
        return shipRepository.findAll(specification);
    }


    @Override
    public Ship createShip(Ship ship) {
        checkShipParameters(ship);

        if (ship.getUsed() == null || ship.getUsed().equals("")){
            ship.setUsed(false);
        }

        ship.setRating(calculateRating(ship));

        return shipRepository.saveAndFlush(ship);
    }

    @Override
    public Ship updateShip(String id, Ship ship) {
        checkShip(ship);
        long longId = idValidation(id);
        Ship updateShip;

        try {
             updateShip = shipRepository.findById(longId).get();
        } catch (Exception e){
            throw new ShipNotFoundException("Ship Not Found!");
        }

        if (ship.getName() != null && checkName(ship)) {
            updateShip.setName(ship.getName());
        }

        if (ship.getPlanet() != null && checkPlanet(ship)) {
            updateShip.setPlanet(ship.getPlanet());
        }

        if (ship.getProdDate() != null && checkProdDate(ship)) {
            updateShip.setProdDate(ship.getProdDate());
        }

        if (ship.getShipType() != null) {
            updateShip.setShipType(ship.getShipType());
        }

        if (ship.getSpeed() != null && checkSpeed(ship)) {
            updateShip.setSpeed(ship.getSpeed());
        }

        if (ship.getCrewSize() != null && checkCrewSize(ship)) {
            updateShip.setCrewSize(ship.getCrewSize());
        }

        updateShip.setRating(calculateRating(updateShip));

        return shipRepository.saveAndFlush(updateShip);

    }

    @Override
    public void deleteShip(String id) {
        long longId = idValidation(id);

        try {
            shipRepository.deleteById(longId);
        } catch (Exception e){
            throw new ShipNotFoundException("ID Not Found In DataBase!");
        }

    }

    @Override
    public Ship getShipById(String id) {
        long longId = idValidation(id);

        try {
            return shipRepository.findById(longId).get();
        } catch (Exception e){
            throw new ShipNotFoundException("ID Not Found In DataBase!");
        }

    }

    private Double calculateRating(Ship ship) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());

        double k = ship.getUsed() == true ? 0.5d : 1d;
        double v = ship.getSpeed();
        long y0 = 3019;
        long y1 = calendar.get(Calendar.YEAR);
        double rating = (80 * v * k) / (y0 - y1 + 1);
        return   Math.round(rating * 100d) / 100d;
    }


    public Specification<Ship> filterByName(String name) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
            }
        };
    }


    public Specification<Ship> filterByPlanet(String planet) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return planet == null ? null : criteriaBuilder.like(root.get("planet"), "%" + planet + "%");
            }
        };
    }

    public Specification<Ship> filterByProdDate( Long fromDate, Long toDate) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                if (fromDate == null && toDate == null) {
                    return null;
                } else if (fromDate == null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("prodDate"), new Date(toDate));
                } else if (toDate == null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("prodDate"), new Date(fromDate));
                }

                return criteriaBuilder.between(root.get("prodDate"), new Date(fromDate), new Date(toDate));
            }
        };
    }

    public Specification<Ship> filterByCrewSize(Integer crewSizeFrom, Integer crewSizeTo) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                if (crewSizeFrom == null && crewSizeTo == null) {
                    return null;
                } else if (crewSizeFrom == null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), crewSizeTo);
                } else if (crewSizeTo == null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), crewSizeFrom);
                }

                return criteriaBuilder.between(root.get("crewSize"), crewSizeFrom, crewSizeTo);
            }
        };
    }

    public Specification<Ship> filterBySpeed(Double speedFrom, Double speedTo) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                if (speedFrom == null && speedTo == null) {
                    return null;
                } else if (speedFrom == null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("speed"), speedTo);
                } else if (speedTo == null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), speedFrom);
                }

                return criteriaBuilder.between(root.get("speed"), speedFrom, speedTo);
            }
        };
    }

    public Specification<Ship> filterByRating(Double ratingFrom, Double ratingTo) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                if (ratingFrom == null && ratingTo == null) {
                    return null;
                } else if (ratingFrom == null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("rating"), ratingTo);
                } else if (ratingTo == null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), ratingFrom);
                }

                return criteriaBuilder.between(root.get("rating"), ratingFrom, ratingTo);
            }
        };
    }

    public Specification<Ship> filterByShipType(ShipType shipType) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return shipType == null ? null : criteriaBuilder.equal(root.get("shipType"), shipType);
            }
        };
    }

    public Specification<Ship> filterByUsed(Boolean isUsed) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                if  (isUsed == null){
                    return null;
                } else if (isUsed) {
                   return criteriaBuilder.isTrue(root.get("isUsed"));
                }

            return criteriaBuilder.isFalse(root.get("isUsed"));
            }
        };
    }

    private Long idValidation(String id){

        try {
                if ((Long.parseLong(id) - Double.parseDouble(id) == 0) && (Long.parseLong(id) <= 0)){
                    throw new BadRequestException("ID Is Not Valid!");
                }

                return Long.parseLong(id) ;
        } catch (Exception e){
            throw new BadRequestException("ID Is Not Valid!");
        }

    }


    private boolean checkShipParameters(Ship ship) {
        checkShip(ship);

        boolean isName = ship.getName() != null && checkName(ship);
        boolean isPlanet = ship.getPlanet() != null && checkPlanet(ship);
        boolean isSpeed = ship.getSpeed()!= null && checkSpeed(ship);
        boolean isCrew = ship.getCrewSize() != null && checkCrewSize(ship);
        boolean isDate =  ship.getProdDate() != null && checkProdDate(ship);

        if (!isName || !isPlanet || !isSpeed || !isCrew || !isDate){
            throw new BadRequestException("Not Enough Parameters!");
        }
        return true;
    }

    private boolean checkName(Ship ship){

        if (!ship.getName().equals("") && ship.getName().length() <= 50){
            return true;
        } else {
            throw new BadRequestException("Wrong Name!");
        }
    }

    private boolean checkCrewSize (Ship ship) {

        if (ship.getCrewSize() >= 1 && ship.getCrewSize() <= 9999){
            return true;
        } else {
            throw new BadRequestException("Wrong Crew Size!");
        }
    }

    private boolean checkProdDate (Ship ship){

        if (ship.getProdDate().getTime() >= 26192246400000L && ship.getProdDate().getTime() <= 33103209600000L){
            return true;
        } else {
            throw new BadRequestException("Wrong Product Date!");
        }
    }

    private boolean checkPlanet(Ship ship){

        if (!ship.getPlanet().equals("") && ship.getPlanet().length() <= 50){
            return true;
        } else {
            throw new BadRequestException("Wrong Planet!");
        }
    }

    private boolean checkSpeed(Ship ship){

        if ( ship.getSpeed() >= 0.01d && ship.getSpeed() <= 0.99d){
            return true;
        } else {
            throw new BadRequestException("Wrong Speed!");
        }
    }

    private boolean checkShip(Ship ship){

        if (ship == null) {
                throw new BadRequestException("Ship is NULL!");
        }

        return true;
    }
}


