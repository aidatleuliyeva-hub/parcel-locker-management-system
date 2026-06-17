package com.example.parcellocker.repository;

import com.example.parcellocker.domain.Customer;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class CustomerRepository {

    private final EntityManager entityManager;

    public CustomerRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            entityManager.persist(customer);
            return customer;
        }

        return entityManager.merge(customer);
    }

    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Customer.class, id));
    }

    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        return entityManager
                .createQuery("""
                        select c
                        from Customer c
                        where c.phoneNumber = :phoneNumber
                        """, Customer.class)
                .setParameter("phoneNumber", phoneNumber)
                .getResultStream()
                .findFirst();
    }

    public List<Customer> findAll() {
        return entityManager
                .createQuery("""
                        select c
                        from Customer c
                        order by c.fullName
                        """, Customer.class)
                .getResultList();
    }

    public void delete(Customer customer) {
        Customer managedCustomer = entityManager.contains(customer)
                ? customer
                : entityManager.merge(customer);

        entityManager.remove(managedCustomer);
    }
}