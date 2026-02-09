package org.alsception.pegasus.features.order.dto;

import java.util.ArrayList;
import java.util.List;
import org.alsception.pegasus.features.order.PGSOrder;
import org.alsception.pegasus.features.order.PGSOrderItem;
import org.alsception.pegasus.features.order.dto.OrderDTO;
import org.alsception.pegasus.features.order.dto.OrderItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OrderMapper {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderMapper.class);

    private OrderMapper() {
        // utility class
    }

    /* =========================
       ENTITY → DTO
       ========================= */

    public static OrderDTO toDto(PGSOrder order) 
    {
        logger.trace("Mapping to dto");

        if (order == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCode(order.getCode());
        dto.setStol(order.getStol());
        dto.setName(order.getName());
        dto.setAddress(order.getAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCurrency(order.getCurrency());
        dto.setStatus(order.getStatus());
        dto.setComment(order.getComment());
        dto.setPrice(order.getPrice());
        dto.setCreated(order.getCreated());
        dto.setModified(order.getModified());
        dto.setUPripremiAt(order.getUPripremiAt());
        dto.setSpremnoAt(order.getSpremnoAt());

        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setUser(order.getUser());
        }

        if (order.getTable() != null) {
            dto.setTableId(order.getTable().getId());
        }

        dto.setItems(toItemDtoList(order.getItems()));

        return dto;
    }

    private static List<OrderItemDTO> toItemDtoList(List<PGSOrderItem> items) 
    {
        logger.trace("Mapping items list");

        if (items == null) {
            return List.of();
        }

        List<OrderItemDTO> result = new ArrayList<>();
        for (PGSOrderItem item : items) {
            result.add(toItemDto(item));
        }
        return result;
    }

    private static OrderItemDTO toItemDto(PGSOrderItem item) 
    {
        logger.trace("Mapping to item dto");
        
        OrderItemDTO dto = new OrderItemDTO();
        
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());

        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getId());
            dto.setProduct(item.getProduct());
        }

        dto.setOrder(item.getOrder());
        dto.setCreated(item.getCreated());
        dto.setModified(item.getModified());

        return dto;
    }

    /* =========================
       DTO → ENTITY
       ========================= */

    public static PGSOrder toEntity(
            OrderDTO dto
    ) {
        logger.debug("Mapping to entity");
        if (dto == null) {
            return null;
        }

        PGSOrder order = new PGSOrder();
        order.setId(dto.getId());
        order.setCode(dto.getCode());
        order.setStol(dto.getStol());
        order.setName(dto.getName());
        order.setAddress(dto.getAddress());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setCurrency(dto.getCurrency());
        order.setStatus(dto.getStatus());
        order.setComment(dto.getComment());
        order.setPrice(dto.getPrice());
        order.setUser(dto.getUser());
        order.setUPripremiAt(dto.getUPripremiAt());
        order.setSpremnoAt(dto.getSpremnoAt());

        List<PGSOrderItem> items = new ArrayList<>();
        if (dto.getItems() != null) {
            for (OrderItemDTO itemDto : dto.getItems()) {
                PGSOrderItem item = toItemEntity(itemDto, order);
                items.add(item);
            }
        }

        order.setItems(items);
        return order;
    }

    private static PGSOrderItem toItemEntity(
            OrderItemDTO dto,
            PGSOrder order) 
    {
        logger.debug("Mapping to item entity");
        PGSOrderItem item = new PGSOrderItem();
        item.setId(dto.getId());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        item.setOrder(order);
        item.setProduct(dto.getProduct());

        return item;
    }
}