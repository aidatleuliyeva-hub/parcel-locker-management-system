package com.example.parcellocker.ui;

import com.example.parcellocker.application.ParcelLockerApplicationService;
import com.example.parcellocker.application.dto.LockerCellView;
import com.example.parcellocker.application.dto.ParcelView;
import com.example.parcellocker.domain.ParcelStatus;
import com.example.parcellocker.domain.Size;
import com.example.parcellocker.persistence.TestJpaFactory;
import com.example.parcellocker.persistence.TransactionManager;
import jakarta.persistence.EntityManagerFactory;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MainFrameE2ETest {

    private EntityManagerFactory entityManagerFactory;
    private ParcelLockerApplicationService applicationService;
    private FrameFixture window;

    @BeforeAll
    static void installThreadViolationChecker() {
        System.setProperty("java.awt.headless", "false");
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    void setUp() {
        String databaseName = "parcel_locker_ui_test_"
                + UUID.randomUUID().toString().replace("-", "");

        entityManagerFactory = TestJpaFactory.createH2InMemoryEntityManagerFactory(databaseName);

        TransactionManager transactionManager = new TransactionManager(entityManagerFactory);

        applicationService = new ParcelLockerApplicationService(
                transactionManager,
                () -> "999888"
        );

        MainFrame frame = GuiActionRunner.execute(
                () -> new MainFrame(applicationService, false)
        );

        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    void tearDown() {
        if (window != null) {
            window.cleanUp();
        }

        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    @Test
    void userCanRegisterCustomerCreateParcelAssignAndCollectParcel() {
        registerCustomerThroughUi();
        createLockerCellThroughUi();

        Long customerId = applicationService.findAllCustomers().get(0).id();

        createParcelThroughUi(customerId);

        Long parcelId = applicationService.findAllParcels().get(0).id();

        assignParcelThroughUi(parcelId);

        ParcelView assignedParcel = applicationService.findAllParcels().get(0);

        assertThat(assignedParcel.status()).isEqualTo(ParcelStatus.ASSIGNED);
        assertThat(assignedParcel.lockerCellNumber()).isEqualTo("A1");
        assertThat(assignedParcel.pickupCode()).isEqualTo("999888");

        collectParcelThroughUi(parcelId, "999888");

        ParcelView collectedParcel = applicationService.findAllParcels().get(0);
        LockerCellView lockerCell = applicationService.findAllLockerCells().get(0);

        assertThat(collectedParcel.status()).isEqualTo(ParcelStatus.COLLECTED);
        assertThat(collectedParcel.lockerCellNumber()).isEmpty();
        assertThat(collectedParcel.pickupCode()).isEmpty();
        assertThat(lockerCell.occupied()).isFalse();

        window.tabbedPane("mainTabbedPane").selectTab("Parcels");
        window.table("parcelsTable").requireRowCount(1);
    }

    private void registerCustomerThroughUi() {
        window.tabbedPane("mainTabbedPane").selectTab("Customers");

        window.textBox("customerNameField").enterText("Alice Brown");
        window.textBox("customerPhoneField").enterText("+390000000100");
        window.button("registerCustomerButton").click();

        window.table("customersTable").requireRowCount(1);

        assertThat(applicationService.findAllCustomers())
                .hasSize(1);

        assertThat(applicationService.findAllCustomers().get(0).fullName())
                .isEqualTo("Alice Brown");
    }

    private void createLockerCellThroughUi() {
        window.tabbedPane("mainTabbedPane").selectTab("Locker Cells");

        window.textBox("lockerCellNumberField").enterText("A1");
        window.comboBox("lockerCellSizeComboBox").selectItem(Size.SMALL.name());
        window.button("createLockerCellButton").click();

        window.table("lockerCellsTable").requireRowCount(1);

        assertThat(applicationService.findAllLockerCells())
                .hasSize(1);

        assertThat(applicationService.findAllLockerCells().get(0).cellNumber())
                .isEqualTo("A1");
    }

    private void createParcelThroughUi(Long customerId) {
        window.tabbedPane("mainTabbedPane").selectTab("Parcels");

        window.textBox("parcelTrackingNumberField").enterText("TRK-001");
        window.textBox("parcelDescriptionField").enterText("Phone charger");
        window.comboBox("parcelSizeComboBox").selectItem(Size.SMALL.name());
        window.textBox("parcelCustomerIdField").enterText(String.valueOf(customerId));
        window.button("createParcelButton").click();

        window.table("parcelsTable").requireRowCount(1);

        ParcelView parcel = applicationService.findAllParcels().get(0);

        assertThat(parcel.trackingNumber()).isEqualTo("TRK-001");
        assertThat(parcel.status()).isEqualTo(ParcelStatus.CREATED);
    }

    private void assignParcelThroughUi(Long parcelId) {
        window.tabbedPane("mainTabbedPane").selectTab("Actions");

        window.textBox("assignParcelIdField").enterText(String.valueOf(parcelId));
        window.button("assignParcelButton").click();
    }

    private void collectParcelThroughUi(Long parcelId, String pickupCode) {
        window.tabbedPane("mainTabbedPane").selectTab("Actions");

        window.textBox("collectParcelIdField").enterText(String.valueOf(parcelId));
        window.textBox("pickupCodeField").enterText(pickupCode);
        window.button("collectParcelButton").click();
    }
}