package com.example.parcellocker.application;

import com.example.parcellocker.application.dto.CustomerView;
import com.example.parcellocker.application.dto.LockerCellView;
import com.example.parcellocker.application.dto.ParcelView;
import com.example.parcellocker.domain.Customer;
import com.example.parcellocker.domain.LockerCell;
import com.example.parcellocker.domain.Parcel;
import com.example.parcellocker.domain.Size;
import com.example.parcellocker.persistence.TransactionManager;
import com.example.parcellocker.repository.CustomerRepository;
import com.example.parcellocker.repository.LockerCellRepository;
import com.example.parcellocker.repository.ParcelRepository;
import com.example.parcellocker.service.BusinessException;
import com.example.parcellocker.service.ParcelLockerService;
import com.example.parcellocker.service.PickupCodeGenerator;

import java.util.List;
import java.util.Objects;

public class ParcelLockerApplicationService {

    private final TransactionManager transactionManager;
    private final PickupCodeGenerator pickupCodeGenerator;

    public ParcelLockerApplicationService(
            TransactionManager transactionManager,
            PickupCodeGenerator pickupCodeGenerator
    ) {
        this.transactionManager = Objects.requireNonNull(
                transactionManager,
                "Transaction manager cannot be null"
        );
        this.pickupCodeGenerator = Objects.requireNonNull(
                pickupCodeGenerator,
                "Pickup code generator cannot be null"
        );
    }

    public Long registerCustomer(String fullName, String phoneNumber) {
        return transactionManager.doInTransaction(entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);

            customerRepository.findByPhoneNumber(phoneNumber)
                    .ifPresent(existingCustomer -> {
                        throw new BusinessException("Customer with this phone number already exists");
                    });

            Customer customer = new Customer(fullName, phoneNumber);
            customerRepository.save(customer);

            return customer.getId();
        });
    }

    public Long createLockerCell(String cellNumber, Size size) {
        return transactionManager.doInTransaction(entityManager -> {
            LockerCellRepository lockerCellRepository = new LockerCellRepository(entityManager);

            lockerCellRepository.findByCellNumber(cellNumber)
                    .ifPresent(existingCell -> {
                        throw new BusinessException("Locker cell with this number already exists");
                    });

            LockerCell lockerCell = new LockerCell(cellNumber, size);
            lockerCellRepository.save(lockerCell);

            return lockerCell.getId();
        });
    }

    public Long createParcel(
            String trackingNumber,
            String description,
            Size size,
            Long customerId
    ) {
        return transactionManager.doInTransaction(entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);
            ParcelRepository parcelRepository = new ParcelRepository(entityManager);

            parcelRepository.findByTrackingNumber(trackingNumber)
                    .ifPresent(existingParcel -> {
                        throw new BusinessException("Parcel with this tracking number already exists");
                    });

            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new BusinessException("Customer not found"));

            Parcel parcel = new Parcel(trackingNumber, description, size, customer);
            parcelRepository.save(parcel);

            return parcel.getId();
        });
    }

    public void assignParcelToAvailableCell(Long parcelId) {
        transactionManager.doInTransaction(entityManager -> {
            ParcelRepository parcelRepository = new ParcelRepository(entityManager);
            LockerCellRepository lockerCellRepository = new LockerCellRepository(entityManager);

            Parcel parcel = parcelRepository.findById(parcelId)
                    .orElseThrow(() -> new BusinessException("Parcel not found"));

            LockerCell lockerCell = lockerCellRepository.findAvailableBySize(parcel.getSize())
                    .orElseThrow(() -> new BusinessException("No available locker cell for parcel size"));

            ParcelLockerService parcelLockerService = new ParcelLockerService(pickupCodeGenerator);
            parcelLockerService.assignParcelToCell(parcel, lockerCell);
        });
    }

    public void collectParcel(Long parcelId, String pickupCode) {
        transactionManager.doInTransaction(entityManager -> {
            ParcelRepository parcelRepository = new ParcelRepository(entityManager);

            Parcel parcel = parcelRepository.findById(parcelId)
                    .orElseThrow(() -> new BusinessException("Parcel not found"));

            ParcelLockerService parcelLockerService = new ParcelLockerService(pickupCodeGenerator);
            parcelLockerService.collectParcel(parcel, pickupCode);
        });
    }

    public List<CustomerView> findAllCustomers() {
        return transactionManager.doInTransaction(entityManager -> {
            CustomerRepository customerRepository = new CustomerRepository(entityManager);

            return customerRepository.findAll()
                    .stream()
                    .map(customer -> new CustomerView(
                            customer.getId(),
                            customer.getFullName(),
                            customer.getPhoneNumber(),
                            customer.getParcels().size()
                    ))
                    .toList();
        });
    }

    public List<LockerCellView> findAllLockerCells() {
        return transactionManager.doInTransaction(entityManager -> {
            LockerCellRepository lockerCellRepository = new LockerCellRepository(entityManager);

            return lockerCellRepository.findAll()
                    .stream()
                    .map(lockerCell -> new LockerCellView(
                            lockerCell.getId(),
                            lockerCell.getCellNumber(),
                            lockerCell.getSize(),
                            lockerCell.isOccupied()
                    ))
                    .toList();
        });
    }

    public List<ParcelView> findAllParcels() {
        return transactionManager.doInTransaction(entityManager -> {
            ParcelRepository parcelRepository = new ParcelRepository(entityManager);

            return parcelRepository.findAll()
                    .stream()
                    .map(parcel -> new ParcelView(
                            parcel.getId(),
                            parcel.getTrackingNumber(),
                            parcel.getDescription(),
                            parcel.getSize(),
                            parcel.getStatus(),
                            parcel.getCustomer().getFullName(),
                            parcel.getLockerCell() == null
                                    ? ""
                                    : parcel.getLockerCell().getCellNumber(),
                            parcel.getPickupCode() == null
                                    ? ""
                                    : parcel.getPickupCode()
                    ))
                    .toList();
        });
    }
}