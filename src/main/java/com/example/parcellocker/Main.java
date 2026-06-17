package com.example.parcellocker;

import com.example.parcellocker.application.ParcelLockerApplicationService;
import com.example.parcellocker.persistence.JpaUtil;
import com.example.parcellocker.persistence.TransactionManager;
import com.example.parcellocker.service.PickupCodeGenerator;
import com.example.parcellocker.ui.MainFrame;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TransactionManager transactionManager =
                    new TransactionManager(JpaUtil.getEntityManagerFactory());

            ParcelLockerApplicationService applicationService =
                    new ParcelLockerApplicationService(
                            transactionManager,
                            PickupCodeGenerator.randomSixDigitGenerator()
                    );

            MainFrame frame = new MainFrame(applicationService);
            frame.setVisible(true);
        });
    }
}