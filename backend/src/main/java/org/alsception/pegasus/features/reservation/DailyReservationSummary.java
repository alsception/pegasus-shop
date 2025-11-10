package org.alsception.pegasus.features.reservation;

import java.time.LocalDate;
import java.util.List;

public class DailyReservationSummary {
    private LocalDate date;
    private int totalGuests;
    private int totalMenuStandard;
    private int totalMenuGold;
    private int totalMenuPremium;
    private int totalMenuVege;
    private int totalReservations = 0;
    private List<ReservationDTO> reservations;

    public DailyReservationSummary(
        LocalDate date, 
        int totalGuests, 
        int totalMenuStandard, 
        int totalMenuGold, 
        int totalMenuPremium,
        int totalMenuVege,    
        List<ReservationDTO> reservations) 
    {
        this.date = date;
        this.totalGuests = totalGuests;
        this.totalMenuStandard = totalMenuStandard;
        this.totalMenuGold = totalMenuGold;
        this.totalMenuPremium = totalMenuPremium;
        this.totalMenuVege = totalMenuVege;
        this.reservations = reservations;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<ReservationDTO> getReservations() {
        return reservations;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTotalGuests() {
        return totalGuests;
    }

    public void setTotalGuests(int totalGuests) {
        this.totalGuests = totalGuests;
    }

    public int getTotalMenuStandard() {
        return totalMenuStandard;
    }

    public void setTotalMenuStandard(int totalMenuStandard) {
        this.totalMenuStandard = totalMenuStandard;
    }

    public int getTotalMenuGold() {
        return totalMenuGold;
    }

    public void setTotalMenuGold(int totalMenuGold) {
        this.totalMenuGold = totalMenuGold;
    }

    public int getTotalMenuPremium() {
        return totalMenuPremium;
    }

    public void setTotalMenuPremium(int totalMenuPremium) {
        this.totalMenuPremium = totalMenuPremium;
    }

    public int getTotalMenuVege() {
        return totalMenuVege;
    }

    public void setTotalMenuVege(int totalMenuVege) {
        this.totalMenuVege = totalMenuVege;
    }

    public void setReservations(List<ReservationDTO> reservations) {
        this.reservations = reservations;
    }

    public int getTotalReservations() {
        if(!(this.reservations == null || this.reservations.isEmpty()))
            return this.reservations.size();
        return 0;
    }

    public void setTotalReservations(int totalReservations) {
        this.totalReservations = totalReservations;
    }    
}