package org.alsception.pegasus.features.artikli;

import lombok.RequiredArgsConstructor;
import org.alsception.pegasus.features.notifications.NotificationController;
import org.alsception.pegasus.features.products.PGSProduct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/artikli")
@RequiredArgsConstructor
public class PGSArtikalController 
{
    private final PGSArtikalService service;
    private static final Logger logger = LoggerFactory.getLogger(PGSArtikalController.class);

    @GetMapping
    public List<PGSArtikal> getAll(@RequestParam(required = false) Integer kategorijaId, @RequestParam(required = false) String search) 
    {
        if (kategorijaId != null) {
            return service.getByKategorija(kategorijaId);
        }
        else if ( search != null && !"".equals(search))
        {
            return service.findByName(search);
        }
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PGSArtikal> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public PGSArtikal create(@RequestBody PGSArtikal artikal) {
        return service.save(artikal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PGSArtikal> update(@PathVariable Long id, @RequestBody PGSArtikal artikal) 
    {
        if(id==0l)
        {
            try 
            {   //If 0 we will create new
                logger.debug("received new artikal: ");
                logger.debug(artikal.toString());
                return ResponseEntity.ok(service.save(artikal));
            } 
            catch (Exception e) 
            {
                logger.error("Greska prilikom kreiranja novog artikla", e);
                return ResponseEntity.internalServerError().build();
            }
        }
        else
        {
            return service.getById(id)
                .map(existing -> {
                    artikal.setId(id);
                    return ResponseEntity.ok(service.save(artikal));
                })
                .orElse(ResponseEntity.notFound().build());
        }
        
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}