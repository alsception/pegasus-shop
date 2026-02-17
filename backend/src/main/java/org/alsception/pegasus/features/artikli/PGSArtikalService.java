package org.alsception.pegasus.features.artikli;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PGSArtikalService {

    private final PGSArtikalRepository repository;

    public List<PGSArtikal> getAll() {
        return repository.findAll();
    }
    
    public List<PGSArtikal> findByName(String search) {
        return repository.findByNameContainingIgnoreCase(search);
    }

    public Optional<PGSArtikal> getById(Long id) {
        return repository.findById(id);
    }

    public List<PGSArtikal> getByKategorija(Integer kategorijaId) {
        return repository.findByKategorijaId(kategorijaId);
    }

    @Transactional
    public PGSArtikal save(PGSArtikal artikal) {
        return repository.save(artikal);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}