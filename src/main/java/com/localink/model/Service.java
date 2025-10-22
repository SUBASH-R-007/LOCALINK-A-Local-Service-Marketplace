package com.localink.model;

public class Service {
    private long id;
    private long providerId;
    private String providerName;
    private String title;
    private String category;
    private String description;
    private double hourlyRate;
    private boolean active;

    public Service() {}

    public Service(long id, long providerId, String providerName, String title, String category, String description, double hourlyRate, boolean active) {
        this.id = id;
        this.providerId = providerId;
        this.providerName = providerName;
        this.title = title;
        this.category = category;
        this.description = description;
        this.hourlyRate = hourlyRate;
        this.active = active;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getProviderId() { return providerId; }
    public void setProviderId(long providerId) { this.providerId = providerId; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return title + " (" + providerName + ") — 	" + category + " — $" + hourlyRate + "/h";
    }
}
