package test;

import dao.InvoiceDao;
import dao.PaymentDao;
import dao.StudentDao;
import dao.UserDao;
import exception.DuplicateReferenceException;
import exception.InvalidAmountException;
import exception.OverpaymentException;
import model.invoice;
import model.student;
import model.user;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import service.PaymentService;

public class PaymentServiceTest {

    private PaymentService paymentService;
    private InvoiceDao invoiceDao;
    private PaymentDao paymentDao;
    private StudentDao studentDao;
    private UserDao userDao;
    
    private Long testInvoiceId;
    private Long testStaffId;

    @Before
    public void setUp() {
        paymentService = new PaymentService();
        invoiceDao = new InvoiceDao();
        paymentDao = new PaymentDao();
        studentDao = new StudentDao();
        userDao = new UserDao();

        // 1. Create a dummy Student
        student newStudent = new student("Test Student", "Year 1", "0700000000");
        studentDao.save(newStudent);

        // 2. Create a dummy Invoice for that student (Amount Due: 100,000)
        invoice inv = new invoice(newStudent.getId(), "Test Term", 100000.0, new java.util.Date());
        invoiceDao.save(inv);
        testInvoiceId = inv.getId();

        // 3. Get the Admin user ID (With a safety check)
        user admin = userDao.findByUsername("admin");
        if (admin == null) {
            user newAdmin = new user("Test Admin", "testadmin", "test123", "ADMIN");
            userDao.save(newAdmin);
            testStaffId = newAdmin.getId();
        } else {
            testStaffId = admin.getId();
        }
    }

    @After
    public void tearDown() {
        // Clean up the test data after each test so the database stays clean
        try {
            invoice inv = invoiceDao.findById(testInvoiceId);
            if (inv != null) {
                invoiceDao.delete(inv);
            }
        } catch (Exception e) {
            // Ignore if already deleted
        }
    }

    // ==========================================
    // TEST 1: Successful Payment (Positive Path)
    // ==========================================
    @Test
    public void testValidPayment_Success() throws Exception {
        // Pay 40,000 out of 100,000
        paymentService.processPayment(testInvoiceId, testStaffId, 40000.0, "REF-" + System.currentTimeMillis(), "CASH");

        // Verify the invoice was updated
        invoice updatedInv = invoiceDao.findById(testInvoiceId);
        assertEquals(40000.0, updatedInv.getAmountPaid(), 0.01);
        assertEquals(60000.0, updatedInv.calculateBalance(), 0.01);
        assertEquals("PARTIAL", updatedInv.getStatus());
    }

    // ==========================================
    // TEST 2: Invalid Amount (Negative Scenario)
    // ==========================================
    @Test(expected = InvalidAmountException.class)
    public void testInvalidAmount_ThrowsException() throws Exception {
        // Try to pay -500
        paymentService.processPayment(testInvoiceId, testStaffId, -500.0, "REF-" + System.currentTimeMillis(), "CASH");
    }

    // ==========================================
    // TEST 3: Overpayment (Negative Scenario & Rollback Verification)
    // ==========================================
    @Test
    public void testOverpayment_ThrowsExceptionAndRollsBack() {
        // Try to pay 150,000 when the bill is 100,000
        try {
            paymentService.processPayment(testInvoiceId, testStaffId, 150000.0, "REF-" + System.currentTimeMillis(), "CASH");
            fail("Expected OverpaymentException was not thrown");
        } catch (Exception e) {
            assertTrue(e instanceof OverpaymentException);
            
            // Verify Integrity / Rollback: Invoice should remain UNPAID with 0 paid
            invoice invAfterFail = invoiceDao.findById(testInvoiceId);
            assertEquals(0.0, invAfterFail.getAmountPaid(), 0.01);
            assertEquals("UNPAID", invAfterFail.getStatus());
        }
    }

    // ==========================================
    // TEST 4: Duplicate Reference (Negative Scenario)
    // ==========================================
    @Test
    public void testDuplicateReference_ThrowsException() {
        try {
            // First payment succeeds (using a fixed string)
            paymentService.processPayment(testInvoiceId, testStaffId, 10000.0, "UNIQUE-REF-123", "CASH");
            
            // Second payment with the exact same reference should fail
            paymentService.processPayment(testInvoiceId, testStaffId, 10000.0, "UNIQUE-REF-123", "CASH");
            fail("Expected DuplicateReferenceException was not thrown");
            
        } catch (Exception e) {
            assertTrue(e instanceof DuplicateReferenceException);
            
            // Verify Integrity: only the first payment was saved (Amount Paid should be 10,000)
            invoice invAfterFail = invoiceDao.findById(testInvoiceId);
            assertEquals(10000.0, invAfterFail.getAmountPaid(), 0.01);
        }
    }

    // ==========================================
    // TEST 5: Fully Paid Status Update
    // ==========================================
    @Test
    public void testFullPayment_UpdatesStatusToPaid() throws Exception {
        // Pay the exact balance
        paymentService.processPayment(testInvoiceId, testStaffId, 100000.0, "REF-" + System.currentTimeMillis(), "BANK");

        // Verify the invoice is now PAID with a 0 balance
        invoice updatedInv = invoiceDao.findById(testInvoiceId);
        assertEquals(100000.0, updatedInv.getAmountPaid(), 0.01);
        assertEquals(0.0, updatedInv.calculateBalance(), 0.01);
        assertEquals("PAID", updatedInv.getStatus());
    }
}