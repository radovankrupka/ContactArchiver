package com.vma.contactarchive.model;

import java.util.Objects;

public class PhoneContactProjection {

    private String displayName;
    private String phoneNumber;
    private boolean synced = false;

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public PhoneContactProjection() {
    }

    public PhoneContactProjection(String displayName, String phoneNumber) {
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
    }

    public PhoneContactProjection(String displayName, String phoneNumber, boolean synced) {
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.synced = synced;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PhoneContactProjection other = (PhoneContactProjection) obj;
        return Objects.equals(displayName, other.displayName) && Objects.equals(phoneNumber, other.phoneNumber);
    }
}
