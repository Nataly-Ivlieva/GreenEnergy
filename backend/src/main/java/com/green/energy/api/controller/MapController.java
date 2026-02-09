package com.green.energy.api.controller;

import com.green.energy.api.dto.GeneratorStatusResponse;
import com.green.energy.api.service.GeneratorStatusService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/map")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class MapController {

    private final GeneratorStatusService service;

    public MapController(GeneratorStatusService service) {
        this.service = service;
    }

    @GetMapping
    public List<GeneratorStatusResponse> map() {
        return service.getCurrentStatus();
    }
}
