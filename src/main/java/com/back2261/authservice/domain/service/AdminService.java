package com.back2261.authservice.domain.service;

import com.back2261.authservice.interfaces.request.GameRequest;
import com.back2261.authservice.interfaces.request.KeywordRequest;
import com.back2261.authservice.interfaces.response.GamerResponse;
import com.back2261.authservice.interfaces.response.MessageResponse;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;

public interface AdminService {

    GamerResponse getBlockedUsers(String token);

    MessageResponse getReportedMessages(String token);

    DefaultMessageResponse banUser(String token, String userId);

    DefaultMessageResponse unbanUser(String token, String userId);

    DefaultMessageResponse addGame(String token, GameRequest gameRequest);

    DefaultMessageResponse addKeyword(String token, KeywordRequest keywordRequest);

    DefaultMessageResponse deleteReportedMessage(String token, String messageId);
}
