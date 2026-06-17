package com.example.parcellocker.ui;

import com.example.parcellocker.application.ParcelLockerApplicationService;
import com.example.parcellocker.application.dto.CustomerView;
import com.example.parcellocker.application.dto.LockerCellView;
import com.example.parcellocker.application.dto.ParcelView;
import com.example.parcellocker.domain.Size;
import com.example.parcellocker.service.BusinessException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.Dimension;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Objects;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ParcelLockerApplicationService applicationService;
    private final boolean showDialogs;

    private final DefaultTableModel customersTableModel = createReadOnlyTableModel(
            new Object[]{"ID", "Full name", "Phone", "Parcels"}
    );

    private final DefaultTableModel lockerCellsTableModel = createReadOnlyTableModel(
            new Object[]{"ID", "Cell number", "Size", "Occupied"}
    );

    private final DefaultTableModel parcelsTableModel = createReadOnlyTableModel(
            new Object[]{
                    "ID",
                    "Tracking number",
                    "Description",
                    "Size",
                    "Status",
                    "Customer",
                    "Cell",
                    "Pickup code"
            }
    );

    private final JTable customersTable = new JTable(customersTableModel);
    private final JTable lockerCellsTable = new JTable(lockerCellsTableModel);
    private final JTable parcelsTable = new JTable(parcelsTableModel);

    private final JTextField customerNameField = new JTextField();
    private final JTextField customerPhoneField = new JTextField();

    private final JTextField lockerCellNumberField = new JTextField();
    private final JComboBox<Size> lockerCellSizeComboBox = new JComboBox<>(Size.values());

    private final JTextField parcelTrackingNumberField = new JTextField();
    private final JTextField parcelDescriptionField = new JTextField();
    private final JComboBox<Size> parcelSizeComboBox = new JComboBox<>(Size.values());
    private final JTextField parcelCustomerIdField = new JTextField();

    private final JTextField assignParcelIdField = new JTextField();
    private final JTextField collectParcelIdField = new JTextField();
    private final JTextField pickupCodeField = new JTextField();

    public MainFrame(ParcelLockerApplicationService applicationService) {
        this(applicationService, true);
    }

    public MainFrame(ParcelLockerApplicationService applicationService, boolean showDialogs) {
        this.applicationService = Objects.requireNonNull(
                applicationService,
                "Application service cannot be null"
        );
        this.showDialogs = showDialogs;

        setTitle("Parcel Locker Management System");
        setName("mainFrame");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        configureComponentNames();
        configureTables();

        JTabbedPane tabs = new JTabbedPane();
        tabs.setName("mainTabbedPane");

        tabs.addTab("Customers", createCustomersPanel());
        tabs.addTab("Locker Cells", createLockerCellsPanel());
        tabs.addTab("Parcels", createParcelsPanel());
        tabs.addTab("Actions", createActionsPanel());

        add(tabs, BorderLayout.CENTER);

        refreshAllTables();
    }

    private DefaultTableModel createReadOnlyTableModel(Object[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void configureTables() {
        customersTable.setAutoCreateRowSorter(true);
        lockerCellsTable.setAutoCreateRowSorter(true);
        parcelsTable.setAutoCreateRowSorter(true);

        customersTable.setFillsViewportHeight(true);
        lockerCellsTable.setFillsViewportHeight(true);
        parcelsTable.setFillsViewportHeight(true);
    }

    private void configureComponentNames() {
        customersTable.setName("customersTable");
        lockerCellsTable.setName("lockerCellsTable");
        parcelsTable.setName("parcelsTable");

        customerNameField.setName("customerNameField");
        customerPhoneField.setName("customerPhoneField");

        lockerCellNumberField.setName("lockerCellNumberField");
        lockerCellSizeComboBox.setName("lockerCellSizeComboBox");

        parcelTrackingNumberField.setName("parcelTrackingNumberField");
        parcelDescriptionField.setName("parcelDescriptionField");
        parcelSizeComboBox.setName("parcelSizeComboBox");
        parcelCustomerIdField.setName("parcelCustomerIdField");

        assignParcelIdField.setName("assignParcelIdField");
        collectParcelIdField.setName("collectParcelIdField");
        pickupCodeField.setName("pickupCodeField");
    }

    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Register customer"));

        JButton registerButton = new JButton("Register customer");
        registerButton.setName("registerCustomerButton");
        registerButton.addActionListener(event -> registerCustomer());

        form.add(new JLabel("Full name:"));
        form.add(customerNameField);
        form.add(new JLabel("Phone number:"));
        form.add(customerPhoneField);
        form.add(new JLabel(""));
        form.add(registerButton);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(customersTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLockerCellsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Create locker cell"));

        JButton createButton = new JButton("Create locker cell");
        createButton.setName("createLockerCellButton");
        createButton.addActionListener(event -> createLockerCell());

        form.add(new JLabel("Cell number:"));
        form.add(lockerCellNumberField);
        form.add(new JLabel("Size:"));
        form.add(lockerCellSizeComboBox);
        form.add(new JLabel(""));
        form.add(createButton);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(lockerCellsTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createParcelsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Create parcel"));

        JButton createButton = new JButton("Create parcel");
        createButton.setName("createParcelButton");
        createButton.addActionListener(event -> createParcel());

        form.add(new JLabel("Tracking number:"));
        form.add(parcelTrackingNumberField);
        form.add(new JLabel("Description:"));
        form.add(parcelDescriptionField);
        form.add(new JLabel("Size:"));
        form.add(parcelSizeComboBox);
        form.add(new JLabel("Customer ID:"));
        form.add(parcelCustomerIdField);
        form.add(new JLabel(""));
        form.add(createButton);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(parcelsTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel assignPanel = new JPanel(new BorderLayout(8, 8));
        assignPanel.setBorder(BorderFactory.createTitledBorder("Assign parcel to available locker cell"));
        assignPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JPanel assignFieldsPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        assignFieldsPanel.add(new JLabel("Parcel ID:"));
        assignFieldsPanel.add(assignParcelIdField);

        JButton assignButton = new JButton("Assign parcel");
        assignButton.setName("assignParcelButton");
        assignButton.addActionListener(event -> assignParcel());

        assignPanel.add(assignFieldsPanel, BorderLayout.CENTER);
        assignPanel.add(assignButton, BorderLayout.SOUTH);

        JPanel collectPanel = new JPanel(new BorderLayout(8, 8));
        collectPanel.setBorder(BorderFactory.createTitledBorder("Collect parcel"));
        collectPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel collectFieldsPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        collectFieldsPanel.add(new JLabel("Parcel ID:"));
        collectFieldsPanel.add(collectParcelIdField);
        collectFieldsPanel.add(new JLabel("Pickup code:"));
        collectFieldsPanel.add(pickupCodeField);

        JButton collectButton = new JButton("Collect parcel");
        collectButton.setName("collectParcelButton");
        collectButton.addActionListener(event -> collectParcel());

        collectPanel.add(collectFieldsPanel, BorderLayout.CENTER);
        collectPanel.add(collectButton, BorderLayout.SOUTH);

        JButton refreshButton = new JButton("Refresh all tables");
        refreshButton.setName("refreshButton");
        refreshButton.addActionListener(event -> refreshAllTables());
        refreshButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        panel.add(assignPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(collectPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(refreshButton);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private void registerCustomer() {
        executeUiAction(() -> {
            applicationService.registerCustomer(
                    customerNameField.getText(),
                    customerPhoneField.getText()
            );

            customerNameField.setText("");
            customerPhoneField.setText("");

            refreshAllTables();
            showSuccess("Customer registered successfully");
        });
    }

    private void createLockerCell() {
        executeUiAction(() -> {
            Size selectedSize = (Size) lockerCellSizeComboBox.getSelectedItem();

            applicationService.createLockerCell(
                    lockerCellNumberField.getText(),
                    selectedSize
            );

            lockerCellNumberField.setText("");

            refreshAllTables();
            showSuccess("Locker cell created successfully");
        });
    }

    private void createParcel() {
        executeUiAction(() -> {
            Size selectedSize = (Size) parcelSizeComboBox.getSelectedItem();
            Long customerId = parseLong(parcelCustomerIdField.getText(), "Customer ID");

            applicationService.createParcel(
                    parcelTrackingNumberField.getText(),
                    parcelDescriptionField.getText(),
                    selectedSize,
                    customerId
            );

            parcelTrackingNumberField.setText("");
            parcelDescriptionField.setText("");
            parcelCustomerIdField.setText("");

            refreshAllTables();
            showSuccess("Parcel created successfully");
        });
    }

    private void assignParcel() {
        executeUiAction(() -> {
            Long parcelId = parseLong(assignParcelIdField.getText(), "Parcel ID");

            applicationService.assignParcelToAvailableCell(parcelId);

            assignParcelIdField.setText("");

            refreshAllTables();
            showSuccess("Parcel assigned successfully");
        });
    }

    private void collectParcel() {
        executeUiAction(() -> {
            Long parcelId = parseLong(collectParcelIdField.getText(), "Parcel ID");

            applicationService.collectParcel(parcelId, pickupCodeField.getText());

            collectParcelIdField.setText("");
            pickupCodeField.setText("");

            refreshAllTables();
            showSuccess("Parcel collected successfully");
        });
    }

    private Long parseLong(String value, String fieldName) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException exception) {
            throw new BusinessException(fieldName + " must be a valid number");
        }
    }

    private void executeUiAction(Runnable action) {
        try {
            action.run();
        } catch (BusinessException | IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (RuntimeException exception) {
            showError("Unexpected error: " + exception.getMessage());
        }
    }

    private void refreshAllTables() {
        refreshCustomersTable();
        refreshLockerCellsTable();
        refreshParcelsTable();
    }

    private void refreshCustomersTable() {
        customersTableModel.setRowCount(0);

        List<CustomerView> customers = applicationService.findAllCustomers();

        for (CustomerView customer : customers) {
            customersTableModel.addRow(new Object[]{
                    customer.id(),
                    customer.fullName(),
                    customer.phoneNumber(),
                    customer.parcelCount()
            });
        }
    }

    private void refreshLockerCellsTable() {
        lockerCellsTableModel.setRowCount(0);

        List<LockerCellView> lockerCells = applicationService.findAllLockerCells();

        for (LockerCellView lockerCell : lockerCells) {
            lockerCellsTableModel.addRow(new Object[]{
                    lockerCell.id(),
                    lockerCell.cellNumber(),
                    lockerCell.size(),
                    lockerCell.occupied()
            });
        }
    }

    private void refreshParcelsTable() {
        parcelsTableModel.setRowCount(0);

        List<ParcelView> parcels = applicationService.findAllParcels();

        for (ParcelView parcel : parcels) {
            parcelsTableModel.addRow(new Object[]{
                    parcel.id(),
                    parcel.trackingNumber(),
                    parcel.description(),
                    parcel.size(),
                    parcel.status(),
                    parcel.customerName(),
                    parcel.lockerCellNumber(),
                    parcel.pickupCode()
            });
        }
    }

    private void showSuccess(String message) {
        if (!showDialogs) {
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showError(String message) {
        if (!showDialogs) {
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}