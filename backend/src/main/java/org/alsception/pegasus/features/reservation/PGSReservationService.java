package org.alsception.pegasus.features.reservation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PGSReservationService {

    private final PGSReservationRepository reservationRepository;

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

    // Create a new reservation
    @Transactional
    public ReservationDTO create(ReservationDTO dto) {
        PGSReservation reservation = mapToEntity(dto);
        PGSReservation saved = reservationRepository.save(reservation);
        return mapToDTO(saved);
    }

    // Update an existing reservation
    @Transactional
    public ReservationDTO update(Long id, ReservationDTO dto) {
        PGSReservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id " + id));

        updateEntity(existing, dto);
        PGSReservation updated = reservationRepository.save(existing);
        return mapToDTO(updated);
    }

    // Delete reservation
    @Transactional
    public void delete(Long id) {
        PGSReservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id " + id));
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
