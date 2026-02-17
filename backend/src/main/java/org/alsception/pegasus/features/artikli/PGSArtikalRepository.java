package org.alsception.pegasus.features.artikli;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PGSArtikalRepository extends JpaRepository<PGSArtikal, Long> {
    
    // Pronađi artikal po barkodu
    Optional<PGSArtikal> findByBarcode(String barcode);
    
    // Provjeri da li artikal sa barkodom postoji
    boolean existsByBarcode(String barcode);
    
    // Pronađi sve artikle po kategoriji
    List<PGSArtikal> findByKategorijaId(Integer kategorijaId);
    
    // Pronađi artikle po imenu (case-insensitive search)
    List<PGSArtikal> findByNameContainingIgnoreCase(String name);
    
    // Pronađi artikle čija cijena (price1) je u određenom rasponu
    List<PGSArtikal> findByPrice1Between(BigDecimal minPrice, BigDecimal maxPrice);
}