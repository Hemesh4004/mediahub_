package com.mediahub.contentcatalog.controller;

import com.mediahub.contentcatalog.entity.Creator;
import com.mediahub.contentcatalog.service.CreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mediahub/contentCatalog/creator")
public class CreatorController {

    // ✅ Logger added
    private static final Logger logger = LoggerFactory.getLogger(CreatorController.class);

    @Autowired
    CreatorService creatorService;

    // ✅ CREATE
    @PostMapping("/createCreator")
    public ResponseEntity<String> createCreator(@RequestBody Creator creator) {
        logger.info("Received request to create creator");

        return ResponseEntity.status(201)
                .body(creatorService.createCreator(creator));
    }

    // ✅ GET ALL
    @GetMapping("/fetchCreators")
    public ResponseEntity<List<Creator>> getAllCreators() {
        logger.info("Received request to fetch all creators");

        return ResponseEntity.ok(creatorService.getAllCreators());
    }

    // ✅ GET BY ID
    @GetMapping("/fetchCreatorById/{creatorId}")
    public ResponseEntity<Creator> getCreatorById(@PathVariable int creatorId) {
        logger.info("Received request to fetch creator with ID: {}", creatorId);

        return ResponseEntity.ok(creatorService.getCreatorById(creatorId));
    }

    // ✅ UPDATE CREATOR
    @PutMapping("/updateCreator/{creatorId}")
    public ResponseEntity<String> updateCreator(@PathVariable int creatorId,
                                                @RequestBody Creator creator) {

        logger.info("Received request to update creator with ID: {}", creatorId);

        return ResponseEntity.ok(creatorService.updateCreator(creatorId, creator));
    }

    // ✅ UPDATE STATUS
    @PutMapping("/updateCreatorStatus/{creatorId}")
    public ResponseEntity<String> updateCreatorStatus(@PathVariable int creatorId,
                                                      @RequestBody Map<String, String> body) {

        logger.info("Received request to update creator status for ID: {}", creatorId);

        return ResponseEntity.ok(
                creatorService.updateCreatorStatus(creatorId, body.get("status"))
        );
    }
 // ✅ VALIDATE CREATOR FOR ROYALTY MODULE
    @GetMapping("/validateCreator/{creatorId}")
    public ResponseEntity<Boolean> validateCreator(
            @PathVariable int creatorId) {

        Creator creator =
                creatorService.getCreatorById(creatorId);

        return ResponseEntity.ok(
                creator != null);
    }
}