package org.alsception.pegasus.core.utils2;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class PaginatedResponse<T> 
{
    private List<T> content;
    private long totalItems;
    private int totalPages;
    private int currentPage;
}
