package com.localink.model;

import java.time.ZonedDateTime;

public class Booking {
    private long id;
    private long customerId;
    private long serviceId;
    private String status;
    private ZonedDateTime scheduledAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }
    public long getServiceId() { return serviceId; }
    public void setServiceId(long serviceId) { this.serviceId = serviceId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public ZonedDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(ZonedDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
}
