package com.example.parcellocker.repository;

import com.example.parcellocker.domain.LockerCell;
import com.example.parcellocker.domain.Size;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class LockerCellRepository {

    private final EntityManager entityManager;

    public LockerCellRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public LockerCell save(LockerCell lockerCell) {
        if (lockerCell.getId() == null) {
            entityManager.persist(lockerCell);
            return lockerCell;
        }

        return entityManager.merge(lockerCell);
    }

    public Optional<LockerCell> findById(Long id) {
        return Optional.ofNullable(entityManager.find(LockerCell.class, id));
    }

    public Optional<LockerCell> findAvailableBySize(Size size) {
        return entityManager
                .createQuery("""
                        select c
                        from LockerCell c
                        where c.size = :size
                          and c.occupied = false
                        order by c.cellNumber
                        """, LockerCell.class)
                .setParameter("size", size)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public List<LockerCell> findAll() {
        return entityManager
                .createQuery("""
                        select c
                        from LockerCell c
                        order by c.cellNumber
                        """, LockerCell.class)
                .getResultList();
    }

    public Optional<LockerCell> findByCellNumber(String cellNumber) {
        return entityManager
                .createQuery("""
                        select c
                        from LockerCell c
                        where c.cellNumber = :cellNumber
                        """, LockerCell.class)
                .setParameter("cellNumber", cellNumber)
                .getResultStream()
                .findFirst();
    }
}