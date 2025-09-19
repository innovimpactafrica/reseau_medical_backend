package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.dto.RoomDTO;
import com.example.rml.back_office_rml.enums.RoomStatus;
import com.example.rml.back_office_rml.services.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    RoomService roomService;

    @Operation(summary = "Create a new room")
    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(
            @Parameter(description = "Room name", required = true)
            @RequestParam @NotBlank String name,

            @Parameter(description = "Room capacity")
            @RequestParam(required = false) @Positive Double capacity,

            @Parameter(description = "Room status")
            @RequestParam(required = false) RoomStatus status,

            @Parameter(description = "Health center ID", required = true)
            @RequestParam @NotNull Long healthCenterId
    ) {
        try {
            // Préparer le DTO
            RoomDTO roomDTO = new RoomDTO();
            roomDTO.setName(name);
            roomDTO.setCapacity(capacity);
            roomDTO.setStatus(status);
            roomDTO.setHealthCenterId(healthCenterId);

            // Appeler le service
            RoomDTO result = roomService.createRoom(roomDTO);

            // Return response
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
