package org.alsception.pegasus.features.reservation;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final PGSReservationRepository reservationRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);


    /***
    
        TODO:

        SEARCH BY DATE, 
        AND NAME.
    
    **/

    // Get all reservations
    public List<ReservationDTO> findAll() {
        return reservationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get reservation by ID
    public ReservationDTO findById(Long id) {
        return reservationRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id " + id));
    }

    public List<ReservationDTO> searchReservations_v1(LocalDate date, String name) 
    {
        logger.info("Searching reservations: date="+date+", name=");
        List<PGSReservation> reservations;
        
        if (date != null && name != null && !name.trim().isEmpty()) {
            // Search by both date and name
            reservations = reservationRepository.findByDanAndImeContainingIgnoreCase(date, name.trim());
        } else if (date != null) {
            // Search by date only
            reservations = reservationRepository.findByDan(date);
        } else if (name != null && !name.trim().isEmpty()) {
            // Search by name only
            reservations = reservationRepository.findByImeContainingIgnoreCase(name.trim());
        } else {
            // No search criteria - return all
            reservations = reservationRepository.findAll();
        }
        
        return reservations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Map<LocalDate, List<ReservationDTO>> searchReservationsRaw(LocalDate dateFrom, LocalDate dateTo, String name) 
    {
        logger.info("Searching reservations: dateFrom=" + dateFrom + ", dateTo=" + dateTo + ", name=" + name);
        
        List<PGSReservation> reservations;
        
        if (dateFrom != null && dateTo != null && name != null && !name.trim().isEmpty()) {
            // Search by date range and name
            reservations = reservationRepository.findByDanBetweenAndImeContainingIgnoreCase(dateFrom, dateTo, name.trim());
        } else if (dateFrom != null && dateTo != null) {
            // Search by date range only
            reservations = reservationRepository.findByDanBetween(dateFrom, dateTo);
        } else if (dateFrom != null) {
            // Search from date onwards (if only dateFrom provided)
            reservations = reservationRepository.findByDanGreaterThanEqualAndImeContainingIgnoreCase(dateFrom, name != null ? name.trim() : "");
        } else if (name != null && !name.trim().isEmpty()) {
            // Search by name only
            reservations = reservationRepository.findByImeContainingIgnoreCase(name.trim());
        } else {
            // No search criteria - return all
            reservations = reservationRepository.findAll();
        }
        
        // Group reservations by day
        // Attention: reservations with null day will be filtered out from the resultset
        return reservations.stream()
            .map(this::mapToDTO)
            .filter(dto -> dto.getDan() != null)
            .collect(Collectors.groupingBy(ReservationDTO::getDan));
    }   

    /**
     * This method returns reservations grouped by day.
     * Reservations without day will be discarded
     */
    public List<DailyReservationSummary> searchReservations(LocalDate dateFrom, LocalDate dateTo, String name) 
    {
        logger.info("Searching reservations: dateFrom=" + dateFrom + ", dateTo=" + dateTo + ", name=" + name);
        
        List<PGSReservation> reservations;
        
        if (dateFrom != null && dateTo != null && name != null && !name.trim().isEmpty()) 
        {
            reservations = reservationRepository.findByDanBetweenAndImeContainingIgnoreCase(dateFrom, dateTo, name.trim());
        } 
        else if (dateFrom != null && dateTo != null) 
        {
            reservations = reservationRepository.findByDanBetween(dateFrom, dateTo);
        }
        else if (dateFrom != null) 
        {
            reservations = reservationRepository.findByDanGreaterThanEqualAndImeContainingIgnoreCase(dateFrom, name != null ? name.trim() : "");
        } 
        else if (name != null && !name.trim().isEmpty()) 
        {
            reservations = reservationRepository.findByImeContainingIgnoreCase(name.trim());
        } 
        else 
        {
            reservations = reservationRepository.findAll();
        }
        
        // Map to DTO and group by day
        // Attention: reservations with null day will be filtered out from the resultset
        Map<LocalDate, List<ReservationDTO>> grouped = reservations.stream()
                .map(this::mapToDTO)
                .filter(dto -> dto.getDan() != null)
                .collect(Collectors.groupingBy(ReservationDTO::getDan));

        // Build summary objects
        return grouped.entrySet().stream()
            .map(entry -> {
                LocalDate date = entry.getKey();
                List<ReservationDTO> dayReservations = entry.getValue();

                int totalGuests = dayReservations.stream()
                        .mapToInt(ReservationDTO::getBrojGostiju)
                        .sum();

                int totalMenuS = dayReservations.stream()
                        .mapToInt(ReservationDTO::getMenuStandard)
                        .sum();

                int totalMenuG = dayReservations.stream()
                        .mapToInt(ReservationDTO::getMenuGold)
                        .sum();

                int totalMenuP = dayReservations.stream()
                        .mapToInt(ReservationDTO::getMenuPremium)
                        .sum();

                int totalMenuV = dayReservations.stream()
                        .mapToInt(ReservationDTO::getMenuVege)
                        .sum();

                return new DailyReservationSummary(date, totalGuests, totalMenuS, totalMenuG, totalMenuP, totalMenuV, dayReservations);
            })
            .sorted(Comparator.comparing(DailyReservationSummary::getDate))
            .collect(Collectors.toList());
    }

    // Create a new reservation
    @Transactional
    public ReservationDTO create(ReservationDTO dto, String user) {
        dto.setCreatedBy(user);
        PGSReservation reservation = mapToEntity(dto);
        PGSReservation saved = reservationRepository.save(reservation);
        logger.info("Created reservation "+saved.getId());
        return mapToDTO(saved);
    }

    // Update an existing reservation
    @Transactional
    public ReservationDTO update(Long id, ReservationDTO dto, String user) {
        PGSReservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id " + id));
        existing.setUpdatedBy(user);
        updateEntity(existing, dto);
        PGSReservation updated = reservationRepository.save(existing);
        logger.info("Updated reservation "+id);
        return mapToDTO(updated);
    }

    // Delete reservation
    @Transactional
    public void delete(Long id) {
        PGSReservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id " + id));
        logger.info("Deleted reservation "+id);                
        reservationRepository.delete(existing);
    }

    // Mapping helpers
    private ReservationDTO mapToDTO(PGSReservation entity) {
        return ReservationDTO.builder()
                .id(entity.getId())
                .dan(entity.getDan())
                .vreme(entity.getVreme())
                .ime(entity.getIme())
                .email(entity.getEmail())
                .telefon(entity.getTelefon())
                .brojGostiju(entity.getBrojGostiju())
                .menuStandard(entity.getMenuStandard())
                .menuGold(entity.getMenuGold())
                .menuPremium(entity.getMenuPremium())
                .menuVege(entity.getMenuVege())
                .menuX(entity.getMenuX())
                .potvrdjeno(entity.getPotvrdjeno())
                .dogovorio(entity.getDogovorio())
                .napomena(entity.getNapomena())
                .rucak(entity.getRucak())
                .vecera(entity.getVecera())
                .vazno(entity.getVazno())
                .otkazano(entity.getOtkazano())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private PGSReservation mapToEntity(ReservationDTO dto) {
        return PGSReservation.builder()
                .dan(dto.getDan())
                .vreme(dto.getVreme())
                .ime(dto.getIme())
                .email(dto.getEmail())
                .telefon(dto.getTelefon())
                .brojGostiju(dto.getBrojGostiju())
                .menuStandard(dto.getMenuStandard())
                .menuGold(dto.getMenuGold())
                .menuPremium(dto.getMenuPremium())
                .menuVege(dto.getMenuVege())
                .menuX(dto.getMenuX())
                .potvrdjeno(dto.getPotvrdjeno())
                .dogovorio(dto.getDogovorio())
                .napomena(dto.getNapomena())
                .rucak(dto.getRucak())
                .vecera(dto.getVecera())
                .vazno(dto.getVazno())
                .otkazano(dto.getOtkazano())
                .createdBy(dto.getCreatedBy())
                .updatedBy(dto.getUpdatedBy())
                .build();
    }

    private void updateEntity(PGSReservation entity, ReservationDTO dto) {
        entity.setDan(dto.getDan());
        entity.setVreme(dto.getVreme());
        entity.setIme(dto.getIme());
        entity.setEmail(dto.getEmail());
        entity.setTelefon(dto.getTelefon());
        entity.setBrojGostiju(dto.getBrojGostiju());
        entity.setMenuStandard(dto.getMenuStandard());
        entity.setMenuGold(dto.getMenuGold());
        entity.setMenuPremium(dto.getMenuPremium());
        entity.setMenuVege(dto.getMenuVege());
        entity.setMenuX(dto.getMenuX());
        entity.setPotvrdjeno(dto.getPotvrdjeno());
        entity.setDogovorio(dto.getDogovorio());
        entity.setNapomena(dto.getNapomena());
        entity.setRucak(dto.getRucak());
        entity.setVecera(dto.getVecera());
        entity.setVazno(dto.getVazno());
        entity.setOtkazano(dto.getOtkazano());
        entity.setUpdatedBy(dto.getUpdatedBy());
    }
}
