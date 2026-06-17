package com.example.parcellocker.repository;

import com.example.parcellocker.domain.Parcel;
import com.example.parcellocker.domain.ParcelStatus;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class ParcelRepository {

    private final EntityManager entityManager;

    public ParcelRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Parcel save(Parcel parcel) {
        if (parcel.getId() == null) {
            entityManager.persist(parcel);
            return parcel;
        }

        return entityManager.merge(parcel);
    }

    public Optional<Parcel> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Parcel.class, id));
    }

    public Optional<Parcel> findByTrackingNumber(String trackingNumber) {
        return entityManager
                .createQuery("""
                        select p
                        from Parcel p
                        where p.trackingNumber = :trackingNumber
                        """, Parcel.class)
                .setParameter("trackingNumber", trackingNumber)
                .getResultStream()
                .findFirst();
    }

    public List<Parcel> findByStatus(ParcelStatus status) {
        return entityManager
                .createQuery("""
                        select p
                        from Parcel p
                        where p.status = :status
                        order by p.trackingNumber
                        """, Parcel.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Parcel> findAll() {
        return entityManager
                .createQuery("""
                        select p
                        from Parcel p
                        order by p.trackingNumber
                        """, Parcel.class)
                .getResultList();
    }
}