package com.example.govservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {
    private Integer page;   // 1-based
    private Integer size;   // page size

    public int offset() {
        int p = page == null || page < 1 ? 1 : page;
        int s = size == null || size < 1 ? 10 : size;
        return (p - 1) * s;
    }

    public int limit() {
        return size == null || size < 1 ? 10 : size;
    }
}


