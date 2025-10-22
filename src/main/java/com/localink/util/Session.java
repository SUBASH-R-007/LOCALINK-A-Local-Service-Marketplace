package com.localink.util;

import com.localink.model.Service;
import java.time.ZonedDateTime;

public class Session {
    private static String selectedCategory;
    private static Service selectedService;
    private static Long currentUserId = 1L; // TODO: replace with real auth
    private static Long lastBookingId;
    private static ZonedDateTime lastScheduledAt;

    public static String getSelectedCategory() { return selectedCategory; }
    public static void setSelectedCategory(String selectedCategory) { Session.selectedCategory = selectedCategory; }

    public static Service getSelectedService() { return selectedService; }
    public static void setSelectedService(Service selectedService) { Session.selectedService = selectedService; }

    public static Long getCurrentUserId() { return currentUserId; }
    public static void setCurrentUserId(Long id) { currentUserId = id; }

    public static Long getLastBookingId() { return lastBookingId; }
    public static void setLastBookingId(Long id) { lastBookingId = id; }

    public static ZonedDateTime getLastScheduledAt() { return lastScheduledAt; }
    public static void setLastScheduledAt(ZonedDateTime when) { lastScheduledAt = when; }
}
