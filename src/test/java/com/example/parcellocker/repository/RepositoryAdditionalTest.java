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
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class RepositoryAdditionalTest {

    private EntityManagerFactory entityManagerFactory;
    private TransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        String databaseName = "parcel_locker_repository_additional_test_"
                + UUID.randomUUID().toString().replace("-", "");

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
    void customerRepositoryShouldMergeExistingCustomerAndDeleteManagedAndDetachedCustomers() {
        AtomicReference<Long> managedCustomerId = new AtomicReference<>();
        AtomicReference<Long> detachedCustomerId = new AtomicReference<>();
        AtomicReference<Customer> detachedCustomer = new AtomicReference<>();

        transactionManager.doInTransaction(entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);

            Customer managedCandidate = new Customer("Managed Customer", "+390000001001");
            Customer detachedCandidate = new Customer("Detached Customer", "+390000001002");

            customerRepository.save(managedCandidate);
            customerRepository.save(detachedCandidate);

            managedCustomerId.set(managedCandidate.getId());
            detachedCustomerId.set(detachedCandidate.getId());
        });

        transactionManager.doInTransaction(entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);

            Customer existingCustomer = customerRepository
                    .findById(managedCustomerId.get())
                    .orElseThrow();

            Customer mergedCustomer = customerRepository.save(existingCustomer);

            assertThat(mergedCustomer.getId()).isEqualTo(managedCustomerId.get());

            customerRepository.delete(existingCustomer);

            assertThat(customerRepository.findById(managedCustomerId.get())).isEmpty();
        });

        transactionManager.doInTransaction(entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);

            Customer existingCustomer = customerRepository
                    .findById(detachedCustomerId.get())
                    .orElseThrow();

            detachedCustomer.set(existingCustomer);
        });

        transactionManager.doInTransaction(entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);

            customerRepository.delete(detachedCustomer.get());

            assertThat(customerRepository.findById(detachedCustomerId.get())).isEmpty();
        });
    }

    @Test
    void parcelRepositoryShouldMergeExistingParcelAndFindParcelsByStatus() {
        AtomicReference<Long> createdParcelId = new AtomicReference<>();

        transactionManager.doInTransaction(entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);
            LockerCellRepository lockerCellRepository = new LockerCellRepository(entityManager);

            Customer customer = new Customer("Parcel Owner", "+390000001003");

            Parcel createdParcel = new Parcel("TRK-CREATED", "Created parcel", Size.SMALL, customer);

            Parcel assignedParcel = new Parcel("TRK-ASSIGNED", "Assigned parcel", Size.MEDIUM, customer);
            LockerCell assignedCell = new LockerCell("PA1", Size.MEDIUM);
            assignedParcel.assignTo(assignedCell, "111111");
            assignedCell.markOccupied();

            Parcel collectedParcel = new Parcel("TRK-COLLECTED", "Collected parcel", Size.LARGE, customer);
            LockerCell collectedCell = new LockerCell("PA2", Size.LARGE);
            collectedParcel.assignTo(collectedCell, "222222");
            collectedCell.markOccupied();
            collectedParcel.collect();

            lockerCellRepository.save(assignedCell);
            lockerCellRepository.save(collectedCell);
            customerRepository.save(customer);

            createdParcelId.set(createdParcel.getId());
        });

        transactionManager.doInTransaction(entityManager -> {
            ParcelRepository parcelRepository = new ParcelRepository(entityManager);

            Parcel existingParcel = parcelRepository
                    .findById(createdParcelId.get())
                    .orElseThrow();

            Parcel mergedParcel = parcelRepository.save(existingParcel);

            assertThat(mergedParcel.getId()).isEqualTo(createdParcelId.get());

            assertThat(parcelRepository.findByStatus(ParcelStatus.CREATED))
                    .extracting(Parcel::getTrackingNumber)
                    .containsExactly("TRK-CREATED");

            assertThat(parcelRepository.findByStatus(ParcelStatus.ASSIGNED))
                    .extracting(Parcel::getTrackingNumber)
                    .containsExactly("TRK-ASSIGNED");

            assertThat(parcelRepository.findByStatus(ParcelStatus.COLLECTED))
                    .extracting(Parcel::getTrackingNumber)
                    .containsExactly("TRK-COLLECTED");
        });
    }

    @Test
    void lockerCellRepositoryShouldMergeExistingLockerCellAndFindById() {
        AtomicReference<Long> lockerCellId = new AtomicReference<>();

        transactionManager.doInTransaction(entityManager -> {
            LockerCellRepository lockerCellRepository = new LockerCellRepository(entityManager);

            LockerCell lockerCell = new LockerCell("RA1", Size.SMALL);

            lockerCellRepository.save(lockerCell);

            lockerCellId.set(lockerCell.getId());
        });

        transactionManager.doInTransaction(entityManager -> {
            LockerCellRepository lockerCellRepository = new LockerCellRepository(entityManager);

            LockerCell existingCell = lockerCellRepository
                    .findById(lockerCellId.get())
                    .orElseThrow();

            LockerCell mergedCell = lockerCellRepository.save(existingCell);

            assertThat(mergedCell.getId()).isEqualTo(lockerCellId.get());

            assertThat(lockerCellRepository.findById(lockerCellId.get()))
                    .isPresent()
                    .get()
                    .extracting(LockerCell::getCellNumber)
                    .isEqualTo("RA1");

            assertThat(lockerCellRepository.findById(-1L)).isEmpty();
        });
    }

    @Test
    void customerRepositorySaveShouldReturnPersistedNewCustomer() {
        transactionManager.doInTransaction(entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);

            Customer customer = new Customer("Returned Customer", "+390000001010");

            Customer savedCustomer = customerRepository.save(customer);

            assertThat(savedCustomer).isSameAs(customer);
            assertThat(savedCustomer.getId()).isNotNull();
        });
    }

    @Test
    void customerRepositoryFindAllShouldReturnCustomersOrderedByFullName() {
        transactionManager.doInTransaction(entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);

            customerRepository.save(new Customer("Zoe Customer", "+390000001011"));
            customerRepository.save(new Customer("Alice Customer", "+390000001012"));

            assertThat(customerRepository.findAll())
                    .extracting(Customer::getFullName)
                    .containsExactly("Alice Customer", "Zoe Customer");
        });
    }

    @Test
    void parcelRepositorySaveShouldReturnPersistedNewParcel() {
        transactionManager.doInTransaction(entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);
            ParcelRepository parcelRepository = new ParcelRepository(entityManager);

            Customer customer = new Customer("Parcel Return Owner", "+390000001013");
            customerRepository.save(customer);

            Parcel parcel = new Parcel(
                    "TRK-RETURNED-SAVE",
                    "Returned parcel",
                    Size.SMALL,
                    customer
            );

            Parcel savedParcel = parcelRepository.save(parcel);

            assertThat(savedParcel).isSameAs(parcel);
            assertThat(savedParcel.getId()).isNotNull();
        });
    }

    @Test
    void lockerCellRepositorySaveShouldReturnPersistedNewLockerCell() {
        transactionManager.doInTransaction(entityManager -> {
            LockerCellRepository lockerCellRepository = new LockerCellRepository(entityManager);

            LockerCell lockerCell = new LockerCell("RETURN-CELL-1", Size.LARGE);

            LockerCell savedLockerCell = lockerCellRepository.save(lockerCell);

            assertThat(savedLockerCell).isSameAs(lockerCell);
            assertThat(savedLockerCell.getId()).isNotNull();
        });
    }
}