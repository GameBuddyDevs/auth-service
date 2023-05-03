package com.back2261.authservice.domain.service;

import com.back2261.authservice.infrastructure.entity.*;
import com.back2261.authservice.infrastructure.repository.*;
import com.back2261.authservice.interfaces.dto.GamerDto;
import com.back2261.authservice.interfaces.dto.GamerResponseBody;
import com.back2261.authservice.interfaces.dto.MessageDto;
import com.back2261.authservice.interfaces.dto.MessageResponseBody;
import com.back2261.authservice.interfaces.request.GameRequest;
import com.back2261.authservice.interfaces.request.KeywordRequest;
import com.back2261.authservice.interfaces.response.GamerResponse;
import com.back2261.authservice.interfaces.response.MessageResponse;
import io.github.GameBuddyDevs.backendlibrary.base.BaseBody;
import io.github.GameBuddyDevs.backendlibrary.base.Status;
import io.github.GameBuddyDevs.backendlibrary.enums.Role;
import io.github.GameBuddyDevs.backendlibrary.enums.TransactionCode;
import io.github.GameBuddyDevs.backendlibrary.exception.BusinessException;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageBody;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import io.github.GameBuddyDevs.backendlibrary.service.JwtService;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultAdminService implements AdminService {

    private final GamerRepository gamerRepository;
    private final AvatarsRepository avatarsRepository;
    private final MessageRepository messageRepository;
    private final SessionRepository sessionRepository;
    private final GamesRepository gamesRepository;
    private final KeywordsRepository keywordsRepository;
    private final JwtService jwtService;

    @Override
    public GamerResponse getBlockedUsers(String token) {
        checkAdmin(token);

        List<Gamer> blockedUsers = gamerRepository.findAllByIsBlockedTrue();
        List<GamerDto> blockedUsersDto = new ArrayList<>();

        for (Gamer gamer : blockedUsers) {
            GamerDto gamerDto = new GamerDto();
            BeanUtils.copyProperties(gamer, gamerDto);
            gamerDto.setUsername(gamer.getGamerUsername());
            Avatars avatar = avatarsRepository.findById(gamer.getAvatar()).orElse(new Avatars());
            gamerDto.setAvatar(avatar.getImage());
            blockedUsersDto.add(gamerDto);
        }

        GamerResponse gamerResponse = new GamerResponse();
        GamerResponseBody body = new GamerResponseBody();
        body.setBlockedUsers(blockedUsersDto);
        gamerResponse.setBody(new BaseBody<>(body));
        gamerResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return gamerResponse;
    }

    @Override
    public MessageResponse getReportedMessages(String token) {
        checkAdmin(token);
        List<Message> reportedMessages = messageRepository.findAllByIsReportedTrue();
        List<MessageDto> reportedMessagesDto = new ArrayList<>();

        for (Message message : reportedMessages) {
            MessageDto messageDto = new MessageDto();
            messageDto.setMessageDate(message.getDate());
            messageDto.setMessage(message.getMessageBody());
            messageDto.setSenderId(message.getSender());
            messageDto.setId(message.getId());
            reportedMessagesDto.add(messageDto);
        }

        MessageResponse messageResponse = new MessageResponse();
        MessageResponseBody body = new MessageResponseBody();
        body.setReportedMessages(reportedMessagesDto);
        messageResponse.setBody(new BaseBody<>(body));
        messageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return messageResponse;
    }

    @Override
    public DefaultMessageResponse banUser(String token, String userId) {
        checkAdmin(token);
        Gamer user = gamerRepository
                .findById(userId)
                .orElseThrow(() -> new BusinessException(TransactionCode.USER_NOT_FOUND));
        user.setIsBlocked(true);
        gamerRepository.save(user);

        Session userSession = sessionRepository.findByEmail(user.getEmail()).orElse(new Session());
        sessionRepository.delete(userSession);

        DefaultMessageResponse response = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("User " + user.getGamerUsername() + " blocked successfully");
        response.setBody(new BaseBody<>(body));
        response.setStatus(new Status(TransactionCode.DEFAULT_100));
        return response;
    }

    @Override
    public DefaultMessageResponse unbanUser(String token, String userId) {
        checkAdmin(token);
        Gamer user = gamerRepository
                .findById(userId)
                .orElseThrow(() -> new BusinessException(TransactionCode.USER_NOT_FOUND));
        if (Boolean.FALSE.equals(user.getIsBlocked())) {
            throw new BusinessException(TransactionCode.USER_NOT_BLOCKED);
        }
        user.setIsBlocked(false);
        gamerRepository.save(user);

        DefaultMessageResponse response = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("User " + user.getGamerUsername() + " unblocked successfully");
        response.setBody(new BaseBody<>(body));
        response.setStatus(new Status(TransactionCode.DEFAULT_100));
        return response;
    }

    @Override
    public DefaultMessageResponse addGame(String token, GameRequest gameRequest) {
        checkAdmin(token);
        Games game = new Games();
        game.setGameId(UUID.randomUUID().toString());
        game.setGameName(gameRequest.getGameName());
        game.setDescription(gameRequest.getGameDescription());
        game.setGameIcon(gameRequest.getGameIcon());
        game.setCategory(gameRequest.getCategory());
        game.setAvgVote(gameRequest.getRating());

        gamesRepository.save(game);

        DefaultMessageResponse response = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Game '" + game.getGameName() + "' added successfully");
        response.setBody(new BaseBody<>(body));
        response.setStatus(new Status(TransactionCode.DEFAULT_100));
        return response;
    }

    @Override
    public DefaultMessageResponse addKeyword(String token, KeywordRequest keywordRequest) {
        checkAdmin(token);
        Keywords keyword = new Keywords();
        keyword.setId(UUID.randomUUID());
        keyword.setCreatedDate(new Date());
        keyword.setKeywordName(keywordRequest.getKeyword());
        keyword.setDescription(keywordRequest.getDescription());

        keywordsRepository.save(keyword);

        DefaultMessageResponse response = new DefaultMessageResponse();
        DefaultMessageBody body =
                new DefaultMessageBody("Keyword '" + keyword.getKeywordName() + "' added successfully");
        response.setBody(new BaseBody<>(body));
        response.setStatus(new Status(TransactionCode.DEFAULT_100));
        return response;
    }

    @Override
    public DefaultMessageResponse deleteReportedMessage(String token, String messageId) {
        checkAdmin(token);
        Message reportedMessage = messageRepository
                .findByIdAndIsReportedTrue(messageId)
                .orElseThrow(() -> new BusinessException(TransactionCode.MESSAGE_NOT_REPORTED));
        reportedMessage.setIsReported(false);
        messageRepository.save(reportedMessage);

        DefaultMessageResponse response = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Report for message " + reportedMessage.getId() + " deleted");
        response.setBody(new BaseBody<>(body));
        response.setStatus(new Status(TransactionCode.DEFAULT_100));
        return response;
    }

    private void checkAdmin(String token) {
        String adminMail = jwtService.extractUsername(token);
        Gamer admin = gamerRepository
                .findByEmail(adminMail)
                .orElseThrow(() -> new BusinessException(TransactionCode.USER_NOT_FOUND));
        if (Boolean.FALSE.equals(admin.getRole().equals(Role.ADMIN))) {
            throw new BusinessException(TransactionCode.NOT_ADMIN);
        }
    }
}
