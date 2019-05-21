package model.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@Builder
public class Ticket {
   private LocalDateTime startDateTime;
    private String filmTitle;
    private BigDecimal price;
    private String genre;
    private int duration;

    @Override
    public String toString() {
        return "BILET, DATA SEANSU: " + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(startDateTime) +
                ", FILM: " + filmTitle + '\'' +
                ", CENA: " + price +
                ", GATUNEK: '" + genre + '\'' +
                ", DLUGOSC TRWANIA FILMU: " + duration +
                " MIN.";
    }
}
