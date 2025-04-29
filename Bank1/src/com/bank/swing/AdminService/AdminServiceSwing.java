/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bank.swing.AdminService;



import javax.swing.*;

import com.bank.swing.admin.AdminGetAllListSwing;
import com.bank.swingBlockAndUnblock.AdminBlockUnblockSwing;
import Bank.com.bank.customer_swingCreateAccount.AccountCreateSwing;
import Bank.swing.admin.ViewParticularCustGUI;

public class AdminServiceSwing {

    public void createAccount(JFrame parent) {
        new AccountCreateSwing().openAccountForm(parent);
    }

    public void viewParticularCustomer(JFrame parent) {
        new ViewParticularCustGUI().showUserDetails(parent);
    }

    public void viewAllCustomers(JFrame parent) {
        new AdminGetAllListSwing().showAllUsers(parent);
    }

    public void blockOrUnblockUser(JFrame parent) {
        new AdminBlockUnblockSwing().showBlockUnblockUI(parent);
    }
}

