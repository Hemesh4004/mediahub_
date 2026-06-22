package com.mediahub.editorial.controller;

import com.mediahub.editorial.model.EditorialReview;
import com.mediahub.editorial.service.EditorialReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/MediaHub/editorial")
public class EditorialReviewController {

    @Autowired
    private EditorialReviewService service;

    // API 1 — POST /reviews
    @PostMapping("/reviews")
    public ResponseEntity<Map<String, Object>> submitReview(
            @RequestBody EditorialReview review) {
        Map<String, Object> res = service.submitReview(review);
        int code = (int) res.remove("statusCode");
        return ResponseEntity.status(code).body(res);
    }

    // API 2 — GET /reviews
    @GetMapping("/reviews")
    public ResponseEntity<List<EditorialReview>> getAllReviews() {
        return ResponseEntity.ok(service.getAllReviews());
    }

    // API 3 — GET /reviews/{reviewID}
    @GetMapping("/reviews/{reviewID}")
    public ResponseEntity<Map<String, Object>> getReviewById(
            @PathVariable int reviewID) {
        Map<String, Object> res =
                service.getReviewById(reviewID);
        int code = (int) res.remove("statusCode");
        return ResponseEntity.status(code).body(res);
    }

    // API 4 — POST /reviews/{reviewID}/approve
    @PostMapping("/reviews/{reviewID}/approve")
    public ResponseEntity<Map<String, Object>> approveReview(
            @PathVariable int reviewID,
            @RequestBody Map<String, String> body) {
        Map<String, Object> res = service.approveReview(
                reviewID, body.get("remarks"));
        int code = (int) res.remove("statusCode");
        return ResponseEntity.status(code).body(res);
    }

    // API 5 — POST /reviews/{reviewID}/reject
    @PostMapping("/reviews/{reviewID}/reject")
    public ResponseEntity<Map<String, Object>> rejectReview(
            @PathVariable int reviewID,
            @RequestBody Map<String, String> body) {
        Map<String, Object> res = service.rejectReview(
                reviewID, body.get("remarks"));
        int code = (int) res.remove("statusCode");
        return ResponseEntity.status(code).body(res);
    }

    // API 6 — POST /reviews/{reviewID}/revise
    @PostMapping("/reviews/{reviewID}/revise")
    public ResponseEntity<Map<String, Object>> requestRevision(
            @PathVariable int reviewID,
            @RequestBody Map<String, String> body) {
        Map<String, Object> res = service.requestRevision(
                reviewID, body.get("remarks"));
        int code = (int) res.remove("statusCode");
        return ResponseEntity.status(code).body(res);
    }
}
