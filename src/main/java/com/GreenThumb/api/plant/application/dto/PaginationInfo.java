package com.GreenThumb.api.plant.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationInfo {
    private int currentPage;
    private int totalResults;
    private Integer nextPage;
    private Integer previousPage;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}
