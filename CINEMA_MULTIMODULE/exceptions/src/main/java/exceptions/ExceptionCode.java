package exceptions;


public enum ExceptionCode {
    UNIDENTIFIED ("NIEZIDENTYFIKOWANY WYJATEK"),
    DB ("WYJATEK Z BAZY DANYCH"),
    REPOSITORY ("WYJATEK Z WARSTWY REPOZYTORYJNEJ"),
    SERVICE ("WYJATEK Z WARSWTY SERWISOWEJ"),
    VALIDATION ("WYJATEK OD WALIDACJI"),
    JSON("WYJATEK PLIKOW JSON"),
    BUILDER ("WYJATEK OD BUILDERA"),
    EMAIL("WYJATEK DOTYCZACY WYSYLANIA E-MAILI");

    private String description;

    ExceptionCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
