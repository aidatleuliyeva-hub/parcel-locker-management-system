package com.example.parcellocker.service;

import com.example.parcellocker.domain.Customer;
import com.example.parcellocker.domain.LockerCell;
import com.example.parcellocker.domain.Parcel;
import com.example.parcellocker.domain.ParcelStatus;
import com.example.parcellocker.domain.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParcelLockerServiceTest {

    private ParcelLockerService service;

    @BeforeEach
    void setUp() {
        service = new ParcelLockerService(() -> "123456");
    }

    @Test
    void assignParcelToAvailableMatchingCell_success() {
        Customer customer = new Customer("John Smith", "+390000000001");
        Parcel parcel = new Parcel("TRK-001", "Phone case", Size.SMALL, customer);
        LockerCell cell = new LockerCell("A1", Size.SMALL);

        service.assignParcelToCell(parcel, cell);

        assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.ASSIGNED);
        assertThat(parcel.getLockerCell()).isEqualTo(cell);
        assertThat(parcel.getPickupCode()).isEqualTo("123456");
        assertThat(cell.isOccupied()).isTrue();
    }

    @Test
    void cannotAssignParcelToOccupiedCell() {
        Customer customer = new Customer("John Smith", "+390000000001");

        Parcel firstParcel = new Parcel("TRK-001", "Phone case", Size.SMALL, customer);
        Parcel secondParcel = new Parcel("TRK-002", "USB cable", Size.SMALL, customer);

        LockerCell cell = new LockerCell("A1", Size.SMALL);

        service.assignParcelToCell(firstParcel, cell);

        assertThatThrownBy(() -> service.assignParcelToCell(secondParcel, cell))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Locker cell is already occupied");
    }

    @Test
    void cannotAssignParcelWhenSizeDoesNotMatchCellSize() {
        Customer customer = new Customer("John Smith", "+390000000001");
        Parcel parcel = new Parcel("TRK-001", "Large jacket", Size.LARGE, customer);
        LockerCell cell = new LockerCell("A1", Size.SMALL);

        assertThatThrownBy(() -> service.assignParcelToCell(parcel, cell))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Parcel size must match locker cell size");

        assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.CREATED);
        assertThat(cell.isOccupied()).isFalse();
    }

    @Test
    void cannotAssignAlreadyAssignedParcel() {
        Customer customer = new Customer("John Smith", "+390000000001");
        Parcel parcel = new Parcel("TRK-001", "Phone case", Size.SMALL, customer);

        LockerCell firstCell = new LockerCell("A1", Size.SMALL);
        LockerCell secondCell = new LockerCell("A2", Size.SMALL);

        service.assignParcelToCell(parcel, firstCell);

        assertThatThrownBy(() -> service.assignParcelToCell(parcel, secondCell))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Parcel is already assigned to a locker cell");
    }

    @Test
    void collectParcelWithValidPickupCode_success() {
        Customer customer = new Customer("John Smith", "+390000000001");
        Parcel parcel = new Parcel("TRK-001", "Phone case", Size.SMALL, customer);
        LockerCell cell = new LockerCell("A1", Size.SMALL);

        service.assignParcelToCell(parcel, cell);

        service.collectParcel(parcel, "123456");

        assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.COLLECTED);
        assertThat(parcel.getLockerCell()).isNull();
        assertThat(parcel.getPickupCode()).isNull();
        assertThat(cell.isOccupied()).isFalse();
    }

    @Test
    void cannotCollectParcelWithWrongPickupCode() {
        Customer customer = new Customer("John Smith", "+390000000001");
        Parcel parcel = new Parcel("TRK-001", "Phone case", Size.SMALL, customer);
        LockerCell cell = new LockerCell("A1", Size.SMALL);

        service.assignParcelToCell(parcel, cell);

        assertThatThrownBy(() -> service.collectParcel(parcel, "000000"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid pickup code");

        assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.ASSIGNED);
        assertThat(cell.isOccupied()).isTrue();
    }

    @Test
    void cannotCollectParcelTwice() {
        Customer customer = new Customer("John Smith", "+390000000001");
        Parcel parcel = new Parcel("TRK-001", "Phone case", Size.SMALL, customer);
        LockerCell cell = new LockerCell("A1", Size.SMALL);

        service.assignParcelToCell(parcel, cell);
        service.collectParcel(parcel, "123456");

        assertThatThrownBy(() -> service.collectParcel(parcel, "123456"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Parcel has already been collected");
    }

    @Test
    void customerCanHaveMultipleParcels() {
        Customer customer = new Customer("John Smith", "+390000000001");

        Parcel firstParcel = new Parcel("TRK-001", "Phone case", Size.SMALL, customer);
        Parcel secondParcel = new Parcel("TRK-002", "Book", Size.MEDIUM, customer);

        assertThat(customer.getParcels())
                .containsExactly(firstParcel, secondParcel);
    }

    @Test
    void constructorShouldRejectNullPickupCodeGenerator() {
        assertThatThrownBy(() -> new ParcelLockerService(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Pickup code generator cannot be null");
    }

    @Test
    void cannotAssignNullParcel() {
        LockerCell cell = new LockerCell("A1", Size.SMALL);

        assertThatThrownBy(() -> service.assignParcelToCell(null, cell))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Parcel cannot be null");
    }

    @Test
    void cannotAssignParcelToNullLockerCell() {
        Customer customer = new Customer("John Smith", "+390000000001");
        Parcel parcel = new Parcel("TRK-001", "Phone case", Size.SMALL, customer);

        assertThatThrownBy(() -> service.assignParcelToCell(parcel, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Locker cell cannot be null");
    }

    @Test
    void cannotAssignCollectedParcelAgain() {
        Customer customer = new Customer("John Smith", "+390000000001");
        Parcel parcel = new Parcel("TRK-001", "Phone case", Size.SMALL, customer);
        LockerCell firstCell = new LockerCell("A1", Size.SMALL);
        LockerCell secondCell = new LockerCell("A2", Size.SMALL);

        service.assignParcelToCell(parcel, firstCell);
        service.collectParcel(parcel, "123456");

        assertThatThrownBy(() -> service.assignParcelToCell(parcel, secondCell))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Collected parcel cannot be assigned again");
    }

    @Test
    void cannotAssignParcelWhenGeneratedPickupCodeIsNull() {
        ParcelLockerService serviceWithNullCode = new ParcelLockerService(() -> null);

        Customer customer = new Customer("John Smith", "+390000000001");
        Parcel parcel = new Parcel("TRK-001", "Phone case", Size.SMALL, customer);
        LockerCell cell = new LockerCell("A1", Size.SMALL);

        assertThatThrownBy(() -> serviceWithNullCode.assignParcelToCell(parcel, cell))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Generated pickup code cannot be blank");

        assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.CREATED);
        assertThat(cell.isOccupied()).isFalse();
    }

    @Test
    void cannotAssignParcelWhenGeneratedPickupCodeIsBlank() {
        ParcelLockerService serviceWithBlankCode = new ParcelLockerService(() -> "   ");

        Customer customer = new Customer("John Smith", "+390000000001");
        Parcel parcel = new Parcel("TRK-001", "Phone case", Size.SMALL, customer);
        LockerCell cell = new LockerCell("A1", Size.SMALL);

        assertThatThrownBy(() -> serviceWithBlankCode.assignParcelToCell(parcel, cell))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Generated pickup code cannot be blank");

        assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.CREATED);
        assertThat(cell.isOccupied()).isFalse();
    }

    @Test
    void cannotCollectNullParcel() {
        assertThatThrownBy(() -> service.collectParcel(null, "123456"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Parcel cannot be null");
    }

    @Test
    void cannotCollectParcelThatHasNotBeenAssigned() {
        Customer customer = new Customer("John Smith", "+390000000001");
        Parcel parcel = new Parcel("TRK-001", "Phone case", Size.SMALL, customer);

        assertThatThrownBy(() -> service.collectParcel(parcel, "123456"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Parcel has not been assigned to a locker cell yet");
    }

    @Test
    void cannotCollectParcelWithNullPickupCode() {
        Customer customer = new Customer("John Smith", "+390000000001");
        Parcel parcel = new Parcel("TRK-001", "Phone case", Size.SMALL, customer);
        LockerCell cell = new LockerCell("A1", Size.SMALL);

        service.assignParcelToCell(parcel, cell);

        assertThatThrownBy(() -> service.collectParcel(parcel, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Pickup code cannot be blank");

        assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.ASSIGNED);
        assertThat(cell.isOccupied()).isTrue();
    }

    @Test
    void cannotCollectParcelWithBlankPickupCode() {
        Customer customer = new Customer("John Smith", "+390000000001");
        Parcel parcel = new Parcel("TRK-001", "Phone case", Size.SMALL, customer);
        LockerCell cell = new LockerCell("A1", Size.SMALL);

        service.assignParcelToCell(parcel, cell);

        assertThatThrownBy(() -> service.collectParcel(parcel, "   "))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Pickup code cannot be blank");

        assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.ASSIGNED);
        assertThat(cell.isOccupied()).isTrue();
    }

    @Test
    void cannotCollectAssignedParcelWithoutLockerCell() {
        Customer customer = new Customer("John Smith", "+390000000001");
        Parcel parcel = new Parcel("TRK-001", "Phone case", Size.SMALL, customer);

        setPrivateField(parcel, "status", ParcelStatus.ASSIGNED);
        setPrivateField(parcel, "pickupCode", "123456");
        setPrivateField(parcel, "lockerCell", null);

        assertThatThrownBy(() -> service.collectParcel(parcel, "123456"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Assigned parcel must have a locker cell");
    }

    private static void setPrivateField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Failed to set private field: " + fieldName, exception);
        }
    }
}