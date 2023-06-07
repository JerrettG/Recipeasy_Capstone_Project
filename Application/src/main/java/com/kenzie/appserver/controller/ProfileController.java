package com.kenzie.appserver.controller;

import com.kenzie.capstone.service.client.ApiGatewayException;
import com.kenzie.capstone.service.client.ProfileServiceClient;
import com.kenzie.capstone.service.model.profile.service.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profileService/profile")
public class ProfileController {
    private final ProfileServiceClient profileServiceClient;
    public ProfileController() {
        this.profileServiceClient = new ProfileServiceClient();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Profile> getProfile(@PathVariable("userId") String userId) {
        try {
            Profile profile = profileServiceClient.getProfile(userId);

            return ResponseEntity.ok(profile);
        } catch (ApiGatewayException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @PostMapping
    public ResponseEntity<Profile> createProfile(@RequestBody String body) {
        try {
            Profile profile = profileServiceClient.createProfile(body);

            return ResponseEntity.status(HttpStatus.CREATED).body(profile);
        }
        catch (ApiGatewayException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @PutMapping
    public ResponseEntity<Profile> updateProfile(@RequestBody String body) {
        try {
            Profile profile = profileServiceClient.updateProfile(body);

            return ResponseEntity.ok(profile);
        } catch (ApiGatewayException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Profile> deleteProfile(@PathVariable String userId) {
        try {
            Profile profile = profileServiceClient.deleteProfile(userId);

            return ResponseEntity.ok(profile);
        } catch (ApiGatewayException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

}