package com.snapreserve.snapreserve.service.reservationrequest.model;

public record ParsedSlot(Long id, String startTime, String endTime) {
    public String getDisplayRange() {
        return startTime + " to " + endTime;
    }

    public static ParsedSlot parse(String slot) {
        if (slot == null) {
            throw new IllegalArgumentException("Slot string cannot be null");
        }
        String[] parts = slot.split("\\|", 3);
        try {
            Long id = Long.valueOf(parts[0]);
            return new ParsedSlot(id, parts[1], parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid slot ID in: " + slot, e);
        }
    }

}

