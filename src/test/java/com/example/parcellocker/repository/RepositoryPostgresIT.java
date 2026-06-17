package com.example.parcellocker.repository;

import com.example.parcellocker.domain.Customer;
import com.example.parcellocker.domain.LockerCell;
import com.example.parcellocker.domain.Parcel;
import com.example.parcellocker.domain.ParcelStatus;
import com.example.parcellocker.domain.Size;
import com.example.parcellocker.persistence.TestJpaFactory;
import com.example.parcellocker.persistence.TransactionManager;
import com.example.parcellocker.service.BusinessException;
import com.example.parcellocker.service.ParcelLockerService;

import jakarta.persistence.EntityManager;
import java.util.function.Function;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;



@Testcontainers
class RepositoryPostgresIT {

    @SuppressWarnings("resource")
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("parcel_locker_test")
            .withUsername("test")
            .withPassword("test");

    private static EntityManagerFactory entityManagerFactory;
    private static TransactionManager transactionManager;

    @BeforeAll
    static void setUp() {
        entityManagerFactory = TestJpaFactory.createPostgresEntityManagerFactory(postgres);
        transactionManager = new TransactionManager(entityManagerFactory);
    }

    @AfterAll
    static void tearDown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    @Test
    void canPersistAssignedParcelStateInRealPostgresDatabase() {
        transactionManager.doInTransaction(entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);
            LockerCellRepository lockerCellRepository = new LockerCellRepository(entityManager);

            Customer customer = new Customer("Maria Rossi", "+390000000020");
            Parcel parcel = new Parcel("PG-TRK-001", "Wireless mouse", Size.SMALL, customer);
            LockerCell cell = new LockerCell("PG-A1", Size.SMALL);

            ParcelLockerService service = new ParcelLockerService(() -> "654321");
            service.assignParcelToCell(parcel, cell);

            lockerCellRepository.save(cell);
            customerRepository.save(customer);
        });

        transactionManager.doInTransaction(entityManager -> {
            ParcelRepository parcelRepository = new ParcelRepository(entityManager);

            Parcel foundParcel = parcelRepository
                    .findByTrackingNumber("PG-TRK-001")
                    .orElseThrow();

            assertThat(foundParcel.getStatus()).isEqualTo(ParcelStatus.ASSIGNED);
            assertThat(foundParcel.getPickupCode()).isEqualTo("654321");
            assertThat(foundParcel.getLockerCell().getCellNumber()).isEqualTo("PG-A1");
            assertThat(foundParcel.getLockerCell().isOccupied()).isTrue();
        });
    }

    @Test
    void transactionRollbackDoesNotPersistDataInPostgres() {
        assertThatThrownBy(() -> transactionManager.doInTransaction((Function<EntityManager, Void>) entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);

            Customer customer = new Customer("Rollback User", "+390000000099");
            customerRepository.save(customer);

            throw new BusinessException("Forced rollback");
        }))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Forced rollback");

        transactionManager.doInTransaction(entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);

            assertThat(customerRepository.findByPhoneNumber("+390000000099"))
                    .isEmpty();

            return null;
        });
    }
}