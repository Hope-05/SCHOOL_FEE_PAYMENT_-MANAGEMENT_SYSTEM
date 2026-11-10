package exception;

public class InvoiceNotFoundException extends FeesSystemException {
    
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}