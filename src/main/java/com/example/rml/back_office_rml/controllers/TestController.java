package com.example.rml.back_office_rml.controllers;

import com.example.rml.back_office_rml.entities.Utilisateur;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping("/role")
    public ResponseEntity<String> testRole(@RequestBody Utilisateur request) {
        return ResponseEntity.ok("Rôle reçu : " + request.getRole());
    }
}
