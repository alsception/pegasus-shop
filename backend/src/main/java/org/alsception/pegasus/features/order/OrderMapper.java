package org.alsception.pegasus.features.order;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    PGSOrderDTO toDTO(PGSOrder order);

    PGSOrder toEntity(PGSOrderDTO dto);
}
