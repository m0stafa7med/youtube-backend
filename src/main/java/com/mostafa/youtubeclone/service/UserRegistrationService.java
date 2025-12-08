//package com.mostafa.youtubeclone.service;
//
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.mostafa.youtubeclone.dto.UserInfoDto;
//import com.mostafa.youtubeclone.model.User;
//import com.mostafa.youtubeclone.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.util.Optional;
//
//
//@Service
//@RequiredArgsConstructor
//public class UserRegistrationService {
//
//    @Value("${auth0.userInfoEndPoint}")
//    private String userInfoEndpoint;
//
//    private final UserRepository userRepository;
//
//    public String registerUser(String tokenValue) {
//        HttpRequest httpRequest = HttpRequest.newBuilder()
//                .GET()
//                .uri(URI.create(userInfoEndpoint))
//                .setHeader("Authorization", String.format("Bearer %s", tokenValue))
//                .build();
//
//        HttpClient httpClient = HttpClient.newBuilder()
//                .version(HttpClient.Version.HTTP_2)
//                .build();
//
//        try {
//            HttpResponse<String> responseString = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//            String body = responseString.body();
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//            UserInfoDto userInfoDTO = objectMapper.readValue(body, UserInfoDto.class);
//
//            Optional<User> userBySubject = userRepository.findBySub(userInfoDTO.getSub());
//            if (userBySubject.isPresent())
//                return userBySubject.get().getId();
//
//            User user = User.builder()
//                    .firstName(userInfoDTO.getGivenName())
//                    .lastName(userInfoDTO.getFamilyName())
//                    .fullName(userInfoDTO.getName())
//                    .emailAddress(userInfoDTO.getEmail())
//                    .sub(userInfoDTO.getSub())
//                    .build();
//
//            return userRepository.save(user).getId();
//
//
//        } catch (Exception exception) {
//            throw new RuntimeException("Exception occurred while registering user", exception);
//        }
//
//    }
//}