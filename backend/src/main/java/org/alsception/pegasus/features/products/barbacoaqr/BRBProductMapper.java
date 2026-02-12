// Mapper
package org.alsception.pegasus.features.products.barbacoaqr;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BRBProductMapper {
    
    public BRBProductDTO toDTO(BRBProduct entity) {
        if (entity == null) {
            return null;
        }
        
        BRBProductDTO dto = new BRBProductDTO();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProductId());
        dto.setTitle(entity.getTitle());
        dto.setAmount(entity.getAmount());
        dto.setHasAmount(entity.getHasAmount());
        dto.setParentTitle(entity.getParentTitle());
        dto.setScheduleActive(entity.getScheduleActive());
        dto.setImageUrl(entity.getImageUrl());
        dto.setVideoUrl(entity.getVideoUrl());
        dto.setIcon(entity.getIcon());
        dto.setOrdersActive(entity.getOrdersActive());
        dto.setPriceBottle(entity.getPriceBottle());
        dto.setProductTypeId(entity.getProductTypeId());
        dto.setAlcoholPercent(entity.getAlcoholPercent());
        dto.setPromotion(entity.getPromotion());
        dto.setDescription(entity.getDescription());
        dto.setAdditionCategories(entity.getAdditionCategories());
        dto.setChildren(entity.getChildren());
        dto.setFilters(entity.getFilters());
        dto.setAllergens(entity.getAllergens());
        
        return dto;
    }
    
    public List<BRBProductDTO> toDTOList(List<BRBProduct> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}