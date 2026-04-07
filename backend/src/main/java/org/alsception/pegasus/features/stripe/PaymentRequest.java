package org.alsception.pegasus.features.stripe;

public class PaymentRequest {
    
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
}