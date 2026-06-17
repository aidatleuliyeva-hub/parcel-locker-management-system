package com.example.parcellocker.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DomainModelTest {

    @Test
    void parcelStatusHelpersShouldReturnCorrectValues() {
        Customer customer = new Customer("Alice Brown", "+390000000100");
        Parcel parcel = new Parcel("TRK-001", "Phone charger", Size.SMALL, customer);

        assertThat(parcel.isCreated()).isTrue();
        assertThat(parcel.isAssigned()).isFalse();
        assertThat(parcel.isCollected()).isFalse();

        LockerCell lockerCell = new LockerCell("A1", Size.SMALL);
        parcel.assignTo(lockerCell, "123456");

        assertThat(parcel.isCreated()).isFalse();
        assertThat(parcel.isAssigned()).isTrue();
        assertThat(parcel.isCollected()).isFalse();

        parcel.collect();

        assertThat(parcel.isCreated()).isFalse();
        assertThat(parcel.isAssigned()).isFalse();
        assertThat(parcel.isCollected()).isTrue();
    }

    @Test
    void parcelShouldRejectInvalidTrackingNumber() {
        Customer customer = new Customer("Alice Brown", "+390000000100");

        assertThatThrownBy(() -> new Parcel(null, "Phone charger", Size.SMALL, customer))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Parcel("   ", "Phone charger", Size.SMALL, customer))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parcelShouldRejectInvalidDescription() {
        Customer customer = new Customer("Alice Brown", "+390000000100");

        assertThatThrownBy(() -> new Parcel("TRK-001", null, Size.SMALL, customer))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Parcel("TRK-001", "   ", Size.SMALL, customer))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parcelShouldRejectInvalidSize() {
        Customer customer = new Customer("Alice Brown", "+390000000100");

        assertThatThrownBy(() -> new Parcel("TRK-001", "Phone charger", null, customer))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parcelShouldRejectInvalidCustomer() {
        assertThatThrownBy(() -> new Parcel("TRK-001", "Phone charger", Size.SMALL, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parcelShouldStoreConstructorValues() {
        Customer customer = new Customer("Alice Brown", "+390000000100");
        Parcel parcel = new Parcel("TRK-001", "Phone charger", Size.SMALL, customer);

        assertThat(parcel.getTrackingNumber()).isEqualTo("TRK-001");
        assertThat(parcel.getDescription()).isEqualTo("Phone charger");
        assertThat(parcel.getSize()).isEqualTo(Size.SMALL);
        assertThat(parcel.getCustomer()).isSameAs(customer);
        assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.CREATED);
        assertThat(parcel.getPickupCode()).isNull();
        assertThat(parcel.getLockerCell()).isNull();
    }

    @Test
    void parcelShouldAssignAndCollectCorrectly() {
        Customer customer = new Customer("Alice Brown", "+390000000100");
        Parcel parcel = new Parcel("TRK-001", "Phone charger", Size.SMALL, customer);
        LockerCell lockerCell = new LockerCell("A1", Size.SMALL);

        parcel.assignTo(lockerCell, "123456");

        assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.ASSIGNED);
        assertThat(parcel.getLockerCell()).isSameAs(lockerCell);
        assertThat(parcel.getPickupCode()).isEqualTo("123456");

        parcel.collect();

        assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.COLLECTED);
        assertThat(parcel.getLockerCell()).isNull();
        assertThat(parcel.getPickupCode()).isNull();
    }

    @Test
    void customerShouldRejectInvalidFullName() {
        assertThatThrownBy(() -> new Customer(null, "+390000000100"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Customer("   ", "+390000000100"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void customerShouldRejectInvalidPhoneNumber() {
        assertThatThrownBy(() -> new Customer("Alice Brown", null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Customer("Alice Brown", "   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void customerShouldStoreConstructorValues() {
        Customer customer = new Customer("Alice Brown", "+390000000100");

        assertThat(customer.getFullName()).isEqualTo("Alice Brown");
        assertThat(customer.getPhoneNumber()).isEqualTo("+390000000100");
        assertThat(customer.getParcels()).isEmpty();
    }

    @Test
    void customerShouldRemoveParcel() {
        Customer customer = new Customer("Alice Brown", "+390000000100");
        Parcel parcel = new Parcel("TRK-001", "Phone charger", Size.SMALL, customer);

        assertThat(customer.getParcels()).contains(parcel);
        assertThat(parcel.getCustomer()).isSameAs(customer);

        customer.removeParcel(parcel);

        assertThat(customer.getParcels()).doesNotContain(parcel);
        assertThat(parcel.getCustomer()).isNull();
    }

    @Test
    void lockerCellShouldRejectInvalidCellNumber() {
        assertThatThrownBy(() -> new LockerCell(null, Size.SMALL))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new LockerCell("   ", Size.SMALL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void lockerCellShouldRejectInvalidSize() {
        assertThatThrownBy(() -> new LockerCell("A1", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void lockerCellShouldStoreConstructorValues() {
        LockerCell lockerCell = new LockerCell("A1", Size.SMALL);

        assertThat(lockerCell.getCellNumber()).isEqualTo("A1");
        assertThat(lockerCell.getSize()).isEqualTo(Size.SMALL);
        assertThat(lockerCell.isOccupied()).isFalse();
        assertThat(lockerCell.isAvailable()).isTrue();
    }

    @Test
    void lockerCellAvailabilityShouldDependOnOccupiedFlag() {
        LockerCell lockerCell = new LockerCell("A1", Size.SMALL);

        assertThat(lockerCell.isAvailable()).isTrue();

        lockerCell.markOccupied();

        assertThat(lockerCell.isOccupied()).isTrue();
        assertThat(lockerCell.isAvailable()).isFalse();

        lockerCell.release();

        assertThat(lockerCell.isOccupied()).isFalse();
        assertThat(lockerCell.isAvailable()).isTrue();
    }

    @Test
    void parcelShouldRejectNullLockerCellWhenAssigning() {
        Customer customer = new Customer("Alice Brown", "+390000000100");
        Parcel parcel = new Parcel("TRK-NULL-CELL", "Phone charger", Size.SMALL, customer);

        assertThatThrownBy(() -> parcel.assignTo(null, "123456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Locker cell cannot be null");
    }

    @Test
    void parcelShouldRejectNullPickupCodeWhenAssigning() {
        Customer customer = new Customer("Alice Brown", "+390000000100");
        Parcel parcel = new Parcel("TRK-NULL-CODE", "Phone charger", Size.SMALL, customer);
        LockerCell lockerCell = new LockerCell("Z1", Size.SMALL);

        assertThatThrownBy(() -> parcel.assignTo(lockerCell, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Pickup code cannot be blank");
    }

    @Test
    void parcelShouldRejectBlankPickupCodeWhenAssigning() {
        Customer customer = new Customer("Alice Brown", "+390000000100");
        Parcel parcel = new Parcel("TRK-BLANK-CODE", "Phone charger", Size.SMALL, customer);
        LockerCell lockerCell = new LockerCell("Z2", Size.SMALL);

        assertThatThrownBy(() -> parcel.assignTo(lockerCell, "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Pickup code cannot be blank");
    }

    @Test
    void jpaConstructorsShouldCreateEmptyEntities() {
        assertThat(createWithNoArgs(Customer.class)).isNotNull();
        assertThat(createWithNoArgs(Parcel.class)).isNotNull();
        assertThat(createWithNoArgs(LockerCell.class)).isNotNull();
    }

    private static <T> T createWithNoArgs(Class<T> type) {
        try {
            java.lang.reflect.Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Failed to create entity with no-args constructor: " + type.getSimpleName(), exception);
        }
    }

    @Test
    void lockerCellShouldRejectEmptyCellNumber() {
        assertThatThrownBy(() -> new LockerCell("", Size.SMALL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Locker cell number cannot be blank");
    }

    @Test
    void lockerCellShouldRejectMarkOccupiedWhenAlreadyOccupied() {
        LockerCell lockerCell = new LockerCell("A2", Size.SMALL);

        lockerCell.markOccupied();

        assertThatThrownBy(lockerCell::markOccupied)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Locker cell is already occupied");
    }
}