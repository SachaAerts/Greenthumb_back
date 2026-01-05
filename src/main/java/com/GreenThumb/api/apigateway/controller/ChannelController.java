package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.Exception.CreatedException;
import com.GreenThumb.api.apigateway.dto.ChannelRequest;
import com.GreenThumb.api.apigateway.dto.ThreadRequest;
import com.GreenThumb.api.apigateway.service.ThreadService;
import com.GreenThumb.api.forum.application.dto.ChannelDto;
import com.GreenThumb.api.forum.application.service.ChannelService;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;
    private final ThreadService threadService;

    public ChannelController(ChannelService channelService, ThreadService threadService) {
        this.channelService = channelService;
        this.threadService = threadService;
    }

    @GetMapping
    public ResponseEntity<List<ChannelDto>> getAllChannel() {

        return ResponseEntity.ok(channelService.findAll());
    }


    @PostMapping("/{channelName}/threads")
    public ResponseEntity<?> addThread(
            @PathVariable String channelName,
            @RequestBody ThreadRequest request
    ) {
        log.info("[DEBUG] channel exist: " + channelService.existChannel(channelName));
        if (!channelService.existChannel(channelName)) {
            throw new NoFoundException("Aucun channel trouvé");
        }

        try {
            threadService.saveThread(request, channelName);
            return ResponseEntity.noContent().build();
        } catch (CreatedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> addChannel(
            @RequestBody ChannelRequest request
    ) {
        ChannelDto channelDto = new ChannelDto(request.name(), request.description(), new ArrayList<>());

        channelService.addChannel(channelDto);

        return ResponseEntity.noContent().build();
    }
}
