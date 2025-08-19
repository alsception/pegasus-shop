package org.alsception.pegasus.features.products;

import java.util.List;

public class PaginatedProductsResponse {
    private List<PGSProductDTO> products;
    private long totalCount;
    private int totalPages;
    private int page;
    private int size;

    public PaginatedProductsResponse(List<PGSProductDTO> products, long totalCount, int totalPages, int page, int size) {
        this.products = products;
        this.totalCount = totalCount;
        this.totalPages = totalPages;
        this.page = page;
        this.size = size;
    }

    public List<PGSProductDTO> getProducts() { return products; }
    public long getTotalCount() { return totalCount; }
    public int getTotalPages() { return totalPages; }
    public int getPage() { return page; }
    public int getSize() { return size; }
}