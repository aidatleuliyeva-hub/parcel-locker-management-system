package com.example.parcellocker.repository;

import com.example.parcellocker.domain.Customer;
import com.example.parcellocker.domain.LockerCell;
import com.example.parcellocker.domain.Parcel;
import com.example.parcellocker.domain.ParcelStatus;
import com.example.parcellocker.domain.Size;
import com.example.parcellocker.persistence.TestJpaFactory;
import com.example.parcellocker.persistence.TransactionManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RepositoryIntegrationTest {

    private EntityManagerFactory entityManagerFactory;
    private TransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        String databaseName = "parcel_locker_test_" + UUID.randomUUID().toString().replace("-", "");

        entityManagerFactory = TestJpaFactory.createH2InMemoryEntityManagerFactory(databaseName);
        transactionManager = new TransactionManager(entityManagerFactory);
    }

    @AfterEach
    void tearDown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    @Test
    void canPersistCustomerWithParcel() {
        transactionManager.doInTransaction(entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);

            Customer customer = new Customer("Alice Brown", "+390000000010");
            new Parcel("TRK-100", "Laptop charger", Size.SMALL, customer);

            customerRepository.save(customer);
        });

        transactionManager.doInTransaction(entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);

            Customer foundCustomer = customerRepository
                    .findByPhoneNumber("+390000000010")
                    .orElseThrow();

            assertThat(foundCustomer.getFullName()).isEqualTo("Alice Brown");
            assertThat(foundCustomer.getParcels()).hasSize(1);
            assertThat(foundCustomer.getParcels().get(0).getTrackingNumber()).isEqualTo("TRK-100");
        });
    }

    @Test
    void canFindAvailableLockerCellBySize() {
        transactionManager.doInTransaction(entityManager -> {
            LockerCellRepository lockerCellRepository = new LockerCellRepository(entityManager);

            lockerCellRepository.save(new LockerCell("A1", Size.SMALL));
            lockerCellRepository.save(new LockerCell("B1", Size.MEDIUM));
            lockerCellRepository.save(new LockerCell("C1", Size.LARGE));
        });

        transactionManager.doInTransaction(entityManager -> {
            LockerCellRepository lockerCellRepository = new LockerCellRepository(entityManager);

            LockerCell foundCell = lockerCellRepository
                    .findAvailableBySize(Size.MEDIUM)
                    .orElseThrow();

            assertThat(foundCell.getCellNumber()).isEqualTo("B1");
            assertThat(foundCell.getSize()).isEqualTo(Size.MEDIUM);
            assertThat(foundCell.isAvailable()).isTrue();
        });
    }

    @Test
    void canPersistAssignedParcelState() {
        transactionManager.doInTransaction(entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);
            LockerCellRepository lockerCellRepository = new LockerCellRepository(entityManager);

            Customer customer = new Customer("Bob Green", "+390000000011");
            Parcel parcel = new Parcel("TRK-200", "Book", Size.MEDIUM, customer);
            LockerCell cell = new LockerCell("D1", Size.MEDIUM);

            parcel.assignTo(cell, "777777");
            cell.markOccupied();

            lockerCellRepository.save(cell);
            customerRepository.save(customer);
        });

        transactionManager.doInTransaction(entityManager -> {
            ParcelRepository parcelRepository = new ParcelRepository(entityManager);

            Parcel foundParcel = parcelRepository
                    .findByTrackingNumber("TRK-200")
                    .orElseThrow();

            assertThat(foundParcel.getStatus()).isEqualTo(ParcelStatus.ASSIGNED);
            assertThat(foundParcel.getPickupCode()).isEqualTo("777777");
            assertThat(foundParcel.getLockerCell().getCellNumber()).isEqualTo("D1");
            assertThat(foundParcel.getLockerCell().isOccupied()).isTrue();
        });
    }
}