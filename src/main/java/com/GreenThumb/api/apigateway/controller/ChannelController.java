package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.ChannelRequest;
import com.GreenThumb.api.forum.application.dto.ChannelDto;
import com.GreenThumb.api.forum.application.service.ChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto>> getAllChannel() {
        return ResponseEntity.ok(channelService.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addChannel(
            @RequestBody ChannelRequest request
    ) {
        ChannelDto channelDto = new ChannelDto(request.name(), request.description(), new ArrayList<>());

        channelService.addChannel(channelDto);

        return ResponseEntity.noContent().build();
    }
}
