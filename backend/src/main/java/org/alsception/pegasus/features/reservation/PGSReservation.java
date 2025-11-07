package org.alsception.pegasus.features.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pgs_reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PGSReservation
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    
    private Boolean potvrdjeno;
    private Boolean poslano;
    private String dogovorio;
    private String napomena;
    
    private Boolean rucak;
    private Boolean vecera;
    
    private Boolean vazno;
    private Boolean otkazano;
    
    @Column(name = "created", updatable = false)
    private LocalDateTime created;
    
    @Column(nullable = true)
    private LocalDateTime updated;
    
    @Column(name = "created_by", updatable = false)
    private String createdBy;
    
    @Column(name = "updated_by", nullable = true)
    private String updatedBy;
    
    @PrePersist
    protected void onCreate() {
        this.created = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updated = LocalDateTime.now();
    }
}