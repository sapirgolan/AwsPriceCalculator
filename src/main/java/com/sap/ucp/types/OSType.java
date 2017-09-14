package com.sap.ucp.types;

public enum OSType {
    SUSE,
    Linux,
    Windows,
    RHEL;

    @Override
    public String toString() {
        return this.name();
    }
}
