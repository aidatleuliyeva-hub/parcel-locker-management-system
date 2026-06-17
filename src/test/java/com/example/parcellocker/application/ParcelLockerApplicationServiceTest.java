package com.example.parcellocker.application;

import com.example.parcellocker.application.dto.LockerCellView;
import com.example.parcellocker.application.dto.ParcelView;
import com.example.parcellocker.domain.ParcelStatus;
import com.example.parcellocker.domain.Size;
import com.example.parcellocker.persistence.TestJpaFactory;
import com.example.parcellocker.persistence.TransactionManager;
import com.example.parcellocker.service.BusinessException;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParcelLockerApplicationServiceTest {

    private EntityManagerFactory entityManagerFactory;
    private ParcelLockerApplicationService applicationService;

    @BeforeEach
    void setUp() {
        String databaseName = "parcel_locker_app_service_test_"
                + UUID.randomUUID().toString().replace("-", "");

        entityManagerFactory = TestJpaFactory.createH2InMemoryEntityManagerFactory(databaseName);

        TransactionManager transactionManager = new TransactionManager(entityManagerFactory);

        applicationService = new ParcelLockerApplicationService(
                transactionManager,
                () -> "111222"
        );
    }

    @AfterEach
    void tearDown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    @Test
    void canRegisterCustomerCreateParcelAssignAndCollect() {
        Long customerId = applicationService.registerCustomer("Alice Brown", "+390000000100");
        applicationService.createLockerCell("A1", Size.SMALL);

        Long parcelId = applicationService.createParcel(
                "APP-TRK-001",
                "Phone charger",
                Size.SMALL,
                customerId
        );

        applicationService.assignParcelToAvailableCell(parcelId);

        ParcelView assignedParcel = applicationService.findAllParcels().get(0);

        assertThat(assignedParcel.status()).isEqualTo(ParcelStatus.ASSIGNED);
        assertThat(assignedParcel.pickupCode()).isEqualTo("111222");
        assertThat(assignedParcel.lockerCellNumber()).isEqualTo("A1");

        LockerCellView occupiedCell = applicationService.findAllLockerCells().get(0);
        assertThat(occupiedCell.occupied()).isTrue();

        applicationService.collectParcel(parcelId, "111222");

        ParcelView collectedParcel = applicationService.findAllParcels().get(0);

        assertThat(collectedParcel.status()).isEqualTo(ParcelStatus.COLLECTED);
        assertThat(collectedParcel.pickupCode()).isEmpty();
        assertThat(collectedParcel.lockerCellNumber()).isEmpty();

        LockerCellView releasedCell = applicationService.findAllLockerCells().get(0);
        assertThat(releasedCell.occupied()).isFalse();
    }

    @Test
    void cannotRegisterCustomerWithDuplicatePhoneNumber() {
        applicationService.registerCustomer("Alice Brown", "+390000000100");

        assertThatThrownBy(() ->
                applicationService.registerCustomer("Another Alice", "+390000000100")
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage("Customer with this phone number already exists");
    }

    @Test
    void cannotCreateLockerCellWithDuplicateNumber() {
        applicationService.createLockerCell("A1", Size.SMALL);

        assertThatThrownBy(() ->
                applicationService.createLockerCell("A1", Size.MEDIUM)
        )
                .isInstanceOf(BusinessException.class)
                .hasMessage("Locker cell with this number already exists");
    }

    @Test
    void cannotAssignParcelWhenNoAvailableCellExists() {
        Long customerId = applicationService.registerCustomer("Alice Brown", "+390000000100");

        Long parcelId = applicationService.createParcel(
                "APP-TRK-001",
                "Big box",
                Size.LARGE,
                customerId
        );

        assertThatThrownBy(() -> applicationService.assignParcelToAvailableCell(parcelId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("No available locker cell for parcel size");
    }

    @Test
    void findAllParcelsShouldShowLockerCellNumberAndPickupCodeForAssignedParcel() {
        Long customerId = applicationService.registerCustomer("Coverage User", "+390000009001");
        applicationService.createLockerCell("CV1", Size.SMALL);

        Long parcelId = applicationService.createParcel(
                "TRK-COVERAGE-001",
                "Coverage parcel",
                Size.SMALL,
                customerId
        );

        applicationService.assignParcelToAvailableCell(parcelId);

        assertThat(applicationService.findAllParcels())
                .singleElement()
                .satisfies(parcelView -> {
                    assertThat(parcelView.trackingNumber()).isEqualTo("TRK-COVERAGE-001");
                    assertThat(parcelView.lockerCellNumber()).isEqualTo("CV1");
                    assertThat(parcelView.pickupCode()).isEqualTo("111222");
                });
    }

    @Test
    void constructorShouldRejectNullTransactionManager() {
        assertThatThrownBy(() -> new ParcelLockerApplicationService(null, () -> "111222"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Transaction manager cannot be null");
    }

    @Test
    void constructorShouldRejectNullPickupCodeGenerator() {
        TransactionManager transactionManager = new TransactionManager(entityManagerFactory);

        assertThatThrownBy(() -> new ParcelLockerApplicationService(transactionManager, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Pickup code generator cannot be null");
    }

    @Test
    void cannotCreateParcelWithDuplicateTrackingNumber() {
        Long customerId = applicationService.registerCustomer("Alice Brown", "+390000009100");

        applicationService.createParcel(
                "APP-DUP-001",
                "First parcel",
                Size.SMALL,
                customerId
        );

        assertThatThrownBy(() -> applicationService.createParcel(
                "APP-DUP-001",
                "Second parcel",
                Size.SMALL,
                customerId
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Parcel with this tracking number already exists");
    }

    @Test
    void cannotCreateParcelForMissingCustomer() {
        assertThatThrownBy(() -> applicationService.createParcel(
                "APP-MISSING-CUSTOMER-001",
                "Parcel without customer",
                Size.SMALL,
                -1L
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Customer not found");
    }

    @Test
    void cannotAssignMissingParcel() {
        assertThatThrownBy(() -> applicationService.assignParcelToAvailableCell(-1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Parcel not found");
    }

    @Test
    void cannotCollectMissingParcel() {
        assertThatThrownBy(() -> applicationService.collectParcel(-1L, "111222"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Parcel not found");
    }

    @Test
    void createLockerCellShouldReturnGeneratedIdAndExposeCreatedCell() {
        Long lockerCellId = applicationService.createLockerCell("MUT-CELL-001", Size.MEDIUM);

        assertThat(lockerCellId).isNotNull().isPositive();

        assertThat(applicationService.findAllLockerCells())
                .singleElement()
                .satisfies(lockerCellView -> {
                    assertThat(lockerCellView.id()).isEqualTo(lockerCellId);
                    assertThat(lockerCellView.cellNumber()).isEqualTo("MUT-CELL-001");
                    assertThat(lockerCellView.size()).isEqualTo(Size.MEDIUM);
                    assertThat(lockerCellView.occupied()).isFalse();
                });
    }

    @Test
    void findAllCustomersShouldReturnViewsOrderedByNameWithParcelCount() {
        Long charlieId = applicationService.registerCustomer("Charlie White", "+390000009201");
        Long aliceId = applicationService.registerCustomer("Alice Brown", "+390000009202");

        applicationService.createParcel(
                "APP-CUSTOMER-VIEW-001",
                "First parcel",
                Size.SMALL,
                aliceId
        );

        applicationService.createParcel(
                "APP-CUSTOMER-VIEW-002",
                "Second parcel",
                Size.MEDIUM,
                aliceId
        );

        assertThat(applicationService.findAllCustomers())
                .satisfiesExactly(
                        aliceView -> {
                            assertThat(aliceView.id()).isEqualTo(aliceId);
                            assertThat(aliceView.fullName()).isEqualTo("Alice Brown");
                            assertThat(aliceView.phoneNumber()).isEqualTo("+390000009202");
                            assertThat(aliceView.parcelCount()).isEqualTo(2);
                        },
                        charlieView -> {
                            assertThat(charlieView.id()).isEqualTo(charlieId);
                            assertThat(charlieView.fullName()).isEqualTo("Charlie White");
                            assertThat(charlieView.phoneNumber()).isEqualTo("+390000009201");
                            assertThat(charlieView.parcelCount()).isZero();
                        }
                );
    }
}