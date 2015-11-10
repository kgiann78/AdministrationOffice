package com.constantine.aowidget;

public enum TransferType {
    DIATHESI("Διάθεση"),
    APOSPASI("Απόσπαση"),
    METATHESI("Μετάθεση");

    private String name;
    private TransferType(String name) { this.name = name; }

    public String getName() { return this.name; }

    @Override
    public String toString() { return this.name; }
}
