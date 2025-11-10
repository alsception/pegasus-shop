package org.alsception.pegasus.features.reservation;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.alsception.pegasus.features.users.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController 
{

    private final ReservationService reservationService;
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    // Get all reservations
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAll() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DailyReservationSummary>> searchReservations(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String name) 
    {        
        List<DailyReservationSummary> reservations = reservationService.searchReservations(dateFrom, dateTo, name);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/search/v1")
    public ResponseEntity<List<ReservationDTO>> searchReservations_v1(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String name) 
    {
        logger.info("Searching reservations: date="+date+", name=");
        List<ReservationDTO> reservations = reservationService.searchReservations_v1(date, name);
        return ResponseEntity.ok(reservations);
    }

    // Get reservation by ID
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.findById(id));
    }

    // Create a new reservation
    @PostMapping
    public ResponseEntity<ReservationDTO> create(@RequestBody ReservationDTO reservationDTO, Principal principal) 
    {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(reservationService.create(reservationDTO,principal.getName()));
    }

    // Update reservation by ID
    @PutMapping("/{id}")
    public ResponseEntity<ReservationDTO> update(@PathVariable Long id, @RequestBody ReservationDTO reservationDTO, Principal principal) 
    {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(reservationService.update(id, reservationDTO, principal.getName()));
    }

    // Delete reservation by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
