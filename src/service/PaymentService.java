/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.InvoiceDao;
import dao.PaymentDao;
import exception.DuplicateReferenceException;
import exception.InvalidAmountException;
import exception.InvoiceNotFoundException;
import exception.OverpaymentException;
import model.invoice;
import model.payment;

/**
 *
 * @author shema
 */
public class PaymentService {

    private PaymentDao paymentDao;
    private InvoiceDao invoiceDao;

    public PaymentService() {
        this.paymentDao = new PaymentDao();
        this.invoiceDao = new InvoiceDao();
    }

    public void processPayment(Long invoiceId, Long recordedBy, double amount, String reference, String method) 
            throws InvalidAmountException, DuplicateReferenceException, InvoiceNotFoundException, OverpaymentException {
        
        // --- RULE 3: Invalid Amount ---
        if (amount <= 0) {
            throw new InvalidAmountException("Payment failed: Amount must be greater than zero.");
        }

        // --- RULE 2: Duplicate Reference ---
        if (paymentDao.referenceExists(reference)) {
            throw new DuplicateReferenceException("Payment failed: The reference '" + reference + "' has already been used.");
        }

        // Fetch the Invoice
        invoice inv = invoiceDao.findById(invoiceId);
        if (inv == null) {
            throw new InvoiceNotFoundException("Payment failed: Invoice with ID " + invoiceId + " does not exist.");
        }

        // --- RULE 1: Overpayment ---
        double currentBalance = inv.calculateBalance(); // Using your helper method!
        
        if (amount > currentBalance) {
            throw new OverpaymentException("Payment failed: Amount (" + amount + ") exceeds the outstanding balance (" + currentBalance + ").");
        }
        
        // Create the payment object
        payment newPayment = new payment();
        newPayment.setInvoiceId(invoiceId);
        newPayment.setRecordedBy(recordedBy);
        newPayment.setAmount(amount);
        newPayment.setReference(reference);
        newPayment.setMethod(method);
        newPayment.setStatus("SUCCESS");
        
        // Generate a simple receipt number
        newPayment.setReceiptNumber("RCP-" + System.currentTimeMillis());

        // 1. Save the Payment
        paymentDao.save(newPayment);

        // 2. Update the Invoice Balance
        inv.setAmountPaid(inv.getAmountPaid() + amount);
        
        // Update status (UNPAID, PARTIAL, PAID)
        if (inv.calculateBalance() == 0) {
            inv.setStatus("PAID");
        } else {
            inv.setStatus("PARTIAL");
        }
        
        // 3. Save the updated Invoice
        invoiceDao.update(inv);
    }
}
