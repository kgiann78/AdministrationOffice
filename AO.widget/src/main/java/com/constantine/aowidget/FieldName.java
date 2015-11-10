package com.constantine.aowidget;

public enum FieldName {
    ID("ID"),
    NAME("Όνομα"),
    SURNAME("Επίθετο"),
    BIRTHDAY("Ημερομηνία Γέννησης"),
    AGM("ΑΓΜ"),
    MOBILE("Κινητό Τηλέφωνο"),
    PHONE("Κινητό Οικίας"),
    EMAIL("Email"),
    ADDRESS("Διεύθυνση"),
    DAYOFF_TYPE("Τύπος Αδείας"),
    TRANSFER_TYPE("Τύπος Μετακίνησης"),
    DEPARTURE("Αναχώρηση"),
    DAYOFF_ARRIVAL("Επιστροφή"),
    ARRIVAL("Άφιξη"),
    SERVICE("Υπηρεσία"),
    SCHOOL_NAME("Σχολείο"),
    START("Έναρξη"),
    END("Πέρας");

    private String name;

    private FieldName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
