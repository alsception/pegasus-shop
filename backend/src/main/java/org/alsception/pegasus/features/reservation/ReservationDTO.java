package org.alsception.pegasus.features.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {
    private Long id;
    private LocalDate dan;
    private LocalTime vreme;
    private String ime;
    private String email;
    private String telefon;
    private Integer brojGostiju;
    private Integer menuStandard;
    private Integer menuGold;
    private Integer menuPremium;
    private Integer menuVege;
    private Integer menuX;
    private Boolean poslano;
    private Boolean potvrdjeno;
    private String dogovorio;
    private String napomena;
    private Boolean rucak;
    private Boolean vecera;
    private Boolean vazno;
    private Boolean otkazano;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String createdBy;
    private String updatedBy;
}

