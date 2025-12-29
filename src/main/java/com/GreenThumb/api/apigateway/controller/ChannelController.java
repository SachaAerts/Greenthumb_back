package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.forum.application.dto.ChannelDto;
import com.GreenThumb.api.forum.application.service.ChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
