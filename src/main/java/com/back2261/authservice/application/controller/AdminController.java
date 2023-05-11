package com.back2261.authservice.application.controller;

import com.back2261.authservice.domain.service.AdminService;
import com.back2261.authservice.interfaces.request.GameRequest;
import com.back2261.authservice.interfaces.request.KeywordRequest;
import com.back2261.authservice.interfaces.response.GamerResponse;
import com.back2261.authservice.interfaces.response.MessageResponse;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_MESSAGE = "Authorization field cannot be empty";

    @GetMapping("/get/blocked/users")
    public ResponseEntity<GamerResponse> getBlockedUsers(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token) {
        return new ResponseEntity<>(adminService.getBlockedUsers(token.substring(7)), HttpStatus.OK);
    }

    @GetMapping("/get/reported/messages")
    public ResponseEntity<MessageResponse> getReportedMessages(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token) {
        return new ResponseEntity<>(adminService.getReportedMessages(token.substring(7)), HttpStatus.OK);
    }

    @PostMapping("/ban/user/{userId}")
    public ResponseEntity<DefaultMessageResponse> blockUser(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @PathVariable String userId) {
        return new ResponseEntity<>(adminService.banUser(token.substring(7), userId), HttpStatus.OK);
    }

    @PostMapping("/unban/user/{userId}")
    public ResponseEntity<DefaultMessageResponse> unblockUser(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @PathVariable String userId) {
        return new ResponseEntity<>(adminService.unbanUser(token.substring(7), userId), HttpStatus.OK);
    }

    @PostMapping("/add/game")
    public ResponseEntity<DefaultMessageResponse> addGame(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody GameRequest gameRequest) {
        return new ResponseEntity<>(adminService.addGame(token.substring(7), gameRequest), HttpStatus.OK);
    }

    @PostMapping("/add/keyword")
    public ResponseEntity<DefaultMessageResponse> addKeyword(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody KeywordRequest keywordRequest) {
        return new ResponseEntity<>(adminService.addKeyword(token.substring(7), keywordRequest), HttpStatus.OK);
    }

    @DeleteMapping("/delete/reported/message/{messageId}")
    public ResponseEntity<DefaultMessageResponse> deleteReportedMessage(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @PathVariable String messageId) {
        return new ResponseEntity<>(adminService.deleteReportedMessage(token.substring(7), messageId), HttpStatus.OK);
    }
}
