/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bank.swingCustomerService;


import javax.swing.*;

import com.bank.swingCustomerTransaction.CustomerTransactionSwing;
import com.bank.swing.CustomerStatement.CustomerStatementSwing;
import com.bank.swingUpdateCustomer.CustomerUpdateGUI;
import Bank.swing.admin.ViewParticularCustGUI;

public class CustomerServiceSwing {

    public void viewDetails(JFrame parent) {
        new ViewParticularCustGUI().showUserDetails(parent);
    }

    public void updateDetails(JFrame parent) {
        new CustomerUpdateGUI().updateCustomer(parent);
    }

    public void transferMoney(JFrame parent, int userId) {
        new CustomerTransactionSwing().openTransactionForm(parent, userId);
    }

    public void viewTransactionHistory(JFrame parent, int userId) {
        new CustomerStatementSwing().showStatement(parent, userId);
    }
}


