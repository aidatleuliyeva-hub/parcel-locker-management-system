package com.example.parcellocker.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class TransactionManager {

    private final EntityManagerFactory entityManagerFactory;

    public TransactionManager(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = Objects.requireNonNull(
                entityManagerFactory,
                "EntityManagerFactory cannot be null"
        );
    }

    public <T> T doInTransaction(Function<EntityManager, T> action) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            T result = action.apply(entityManager);
            transaction.commit();
            return result;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }

    public void doInTransaction(Consumer<EntityManager> action) {
        doInTransaction(entityManager -> {
            action.accept(entityManager);
            return null;
        });
    }
}