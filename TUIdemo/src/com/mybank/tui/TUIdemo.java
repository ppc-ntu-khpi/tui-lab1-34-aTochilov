package com.mybank.tui;

import jexer.TAction;
import jexer.TApplication;
import jexer.TField;
import jexer.TText;
import jexer.TWindow;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;

import com.mybank.domain.Bank;
import com.mybank.domain.Account;
import com.mybank.domain.Customer;
import com.mybank.domain.SavingsAccount;
import com.mybank.data.DataSource;
import com.mybank.reporting.CustomerReport;


/**
 *
 * @author Alexander 'Taurus' Babich
 */
public class TUIdemo extends TApplication {

    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;
    private static final int REPORT_VIEW = 2020;

    public static void main(String[] args) throws Exception {
        TUIdemo tdemo = new TUIdemo();
        (new Thread(tdemo)).start();
    }

    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        addToolMenu();
        //custom 'File' menu
        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addItem(REPORT_VIEW, "&View report");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);
        //end of 'File' menu  

        addWindowMenu();

        //custom 'Help' menu
        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");
        //end of 'Help' menu 

        setFocusFollowsMouse(true);
        
        //open test.dat
        DataSource ds = new DataSource("./src/com/mybank/data/test.dat");
        ds.loadData();
        
        //Customer window
        ShowCustomerDetails();
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About", "\t\t\t\t\t   Just a simple Jexer demo.\n\nCopyright \u00A9 2019 Alexander \'Taurus\' Babich").show();
            return true;
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        if (menu.getId() == REPORT_VIEW){
            viewReport();
            return true;
        }
        return super.onMenu(menu);
    }

    private void ShowCustomerDetails() {
        StringBuilder custInfo = new StringBuilder();
        
        TWindow custWin = addWindow("Customer Window", 2, 1, 40, 12, TWindow.NOZOOMBOX);
        custWin.newStatusBar("Enter valid customer number and press Show...");

        custWin.addLabel("Enter customer number: ", 2, 2);
        TField custNo = custWin.addField(24, 2, 3, false);
        TText details = custWin.addText("Owner Name: \nAccount Type: \nAccount Balance: ", 2, 4, 38, 8);
        custWin.addButton("&Show", 28, 2, new TAction() {
            @Override
            public void DO() {
                try {
                    int custNum = Integer.parseInt(custNo.getText());
                    Customer customer = Bank.getCustomer(custNum);
                    custInfo.append("Owner Name: ")
                            .append(Bank.getCustomer(custNum).getFirstName()).append(" ")
                            .append(Bank.getCustomer(custNum).getLastName())
                            .append(" (id = ").append(custNum).append(")");

                    // For each account for this customer...
                    for ( int acct_idx = 0;
                          acct_idx < customer.getNumberOfAccounts();
                          acct_idx++ ) {
                        Account account = customer.getAccount(acct_idx);
                        // Determine the account type
                        if ( account instanceof SavingsAccount ) {
                            custInfo.append("\nAccount Type: Savings account");
                            custInfo.append("\nAccount Balance: ");
                        }
                        else{
                            custInfo.append("\nAccount Type: Checking account");
                            custInfo.append("\nAccount Balance: ");
                        }
                        custInfo.append(customer.getAccount(acct_idx).getBalance());
                      }
                    //details about customer with index==custNum
                    details.setText(custInfo.toString());
                } catch (Exception e) {
                    messageBox("Error", "You must provide a valid customer number!").show();
                }
            }
        });
    }
    
    private void viewReport(){
        TWindow reportWind = addWindow("Report Window", 1, 1, 48, 22, TWindow.NOZOOMBOX);
        TText details = reportWind.addText("No reports", 2, 2, 46, 20);
                try {
                    CustomerReport cr = new CustomerReport();
                    details.setText(cr.printReport());
                }catch (Exception e) {
                    messageBox("Error", "You must provide a valid customer number!").show();
                }
    }
}
