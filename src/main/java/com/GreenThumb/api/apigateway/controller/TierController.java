package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.user.application.dto.TierDto;
import com.GreenThumb.api.user.application.service.TierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/tiers")
public class TierController {

    private final TierService tierService;

    public TierController(TierService tierService) {
        this.tierService = tierService;
    }
    @GetMapping("/{name}/next")
    public ResponseEntity<TierDto> nextTier(@PathVariable String name) {
        return ResponseEntity.ok(tierService.findNextTier(name));
    }
}
