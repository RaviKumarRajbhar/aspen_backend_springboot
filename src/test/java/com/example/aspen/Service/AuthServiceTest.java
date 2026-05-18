package com.example.aspen.Service;

import com.example.aspen.CustomException.InvalidCredentialsException;
import com.example.aspen.CustomException.ResourceNotFoundException;
import com.example.aspen.CustomException.UserAlreadyExistsException;
import com.example.aspen.CustomException.WrongAuthProviderException;
import com.example.aspen.Dto.LoginRequest;
import com.example.aspen.Dto.LoginResponse;
import com.example.aspen.Dto.RegisterRequest;
import com.example.aspen.Entities.User;
import com.example.aspen.Repository.UserRepository;
import com.example.aspen.Security.AuthProvider;
import com.example.aspen.Security.JwtUtil;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private GoogleTokenVerifierService verifierService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserSuccessfully() {

        // request from user
        RegisterRequest request = new RegisterRequest();

        request.setEmail("test@gmail.com");
        request.setUsername("reaper");
        request.setPassword("123456");

        when(userRepository.existsByEmail("test@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("123456"))
                .thenReturn("hashedPassword");

        User savedUser = new User(
                "reaper",
                "hashedPassword",
                "test@gmail.com"
        );

        savedUser.setId(UUID.randomUUID());

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(jwtUtil.generateAccessToken(anyString()))
                .thenReturn("access_token");

        when(jwtUtil.generateRefreshToken(anyString()))
                .thenReturn("refresh_token");

        // when
        LoginResponse response =
                authService.register(request);

        // then
        assertNotNull(response);

        verify(userRepository)
                .save(any(User.class));

        assertEquals("access_token" , response.getAccessToken() );
        assertEquals("refresh_token" , response.getRefreshToken());
    }

    @Test
    void shouldThrowUserAlreadyExistsException() {

        RegisterRequest request = new RegisterRequest();
        request.setEmail("myemail@gmail.com");
        request.setPassword("789789");

        when(userRepository.existsByEmail("myemail@gmail.com"))
                .thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
        () -> authService.register(request));

        verify(userRepository,never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsBlank(){

        RegisterRequest request = new RegisterRequest();

        request.setEmail("fake@gmail.com");
        request.setPassword("");

        when(userRepository.existsByEmail("fake@email.com"))
                .thenReturn(false);

        verify(userRepository , never()).save(any(User.class));

        assertThrows(RuntimeException.class , () -> authService.register(request));
    }

    @Test
    void shouldCreateRandomUsernameWhenNotProvided() {

        RegisterRequest request = new RegisterRequest();

        request.setEmail("random@gmail.com");
        request.setPassword("789654");

        when(userRepository.existsByEmail("random@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("789654"))
                .thenReturn("hashedPassword");

        User savedUser = new User();
        savedUser.setEmail("random@gamil.com");
        savedUser.setPassword("hashedPassword");

        savedUser.setId(UUID.randomUUID());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(jwtUtil.generateAccessToken(anyString())).thenReturn("access_token");

        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("refresh_token");

        LoginResponse response = authService.register(request);

        assertEquals("access_token", response.getAccessToken());
        assertEquals("refresh_token" , response.getRefreshToken());

        verify(userRepository).save(any(User.class));

        verify(userRepository)
                .save(argThat(user ->
                        user.getUsername() != null && user.getUsername().startsWith("user_")
                        ));

    }

    @Test
    void shouldLoginSuccessfully () {

        LoginRequest request = new LoginRequest();

        request.setEmail("real@gmail.com");
        request.setPassword("123456");

        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setAuthProvider(AuthProvider.LOCAL);
        existingUser.setPassword("hashedPassword");
        existingUser.setEmail("real@gmail.com");

        when(userRepository.findByEmail("real@gmail.com"))
                .thenReturn(Optional.of(existingUser));

        when(passwordEncoder.matches("123456" , "hashedPassword"))
                .thenReturn(true);

        when(jwtUtil.generateAccessToken(existingUser.getId().toString()))
                .thenReturn("access_token");

        when(jwtUtil.generateRefreshToken(existingUser.getId().toString()))
                .thenReturn("refresh_token");

        LoginResponse response = authService.login(request);

        assertEquals("access_token" , response.getAccessToken());
        assertEquals("refresh_token" , response.getRefreshToken());

        verify(refreshTokenService).saveRefreshToken(anyString() , eq("refresh_token"));
    }

    @Test
    void shouldCreateNewGoogleUserSuccessfully() throws  Exception {
        String fakeGoogleToken = "google_token";


        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(payload.getEmail())
                .thenReturn("googleUser@gmail.com");

        when(payload.get("name")).thenReturn("GoogleUser");

        when(userRepository.findByEmail("googleUser@gmail.com"))
                .thenReturn(Optional.empty());

        User savedUser = new User();
        savedUser.setEmail("googleUser@gmail.com");
        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername("GoogleUser");
        savedUser.setAuthProvider(AuthProvider.GOOGLE);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(jwtUtil.generateAccessToken(savedUser.getId().toString())).thenReturn("access_token");
        when(jwtUtil.generateRefreshToken(savedUser.getId().toString())).thenReturn("refresh_token");

        when(verifierService.verify(fakeGoogleToken))
                .thenReturn(payload);

        LoginResponse response = authService.loginWithGoogle(fakeGoogleToken);


        assertEquals("access_token" , response.getAccessToken());
        assertEquals("refresh_token" , response.getRefreshToken());


        verify(userRepository).save(argThat(user ->
                user.getAuthProvider() == AuthProvider.GOOGLE && Objects.equals(user.getUsername(), "GoogleUser")));

    }

    @Test
    void shouldLoginGoogleUserSuccessfully() throws Exception {

        final String googleToken = "fakeGoogleToken";

        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(payload.get("name")).thenReturn("realName");
        when(payload.getEmail()).thenReturn("real@gmail.com");

        when(verifierService.verify(googleToken)).thenReturn(payload);

        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setEmail("real@gmail.com");
        existingUser.setPassword("123456");
        existingUser.setAuthProvider(AuthProvider.GOOGLE);

        when(userRepository.findByEmail("real@gmail.com")).thenReturn(Optional.of(existingUser));
        when(jwtUtil.generateAccessToken(existingUser.getId().toString())).thenReturn("access_token");
        when(jwtUtil.generateRefreshToken(existingUser.getId().toString())).thenReturn("refresh_token");

        LoginResponse response = authService.loginWithGoogle(googleToken);

        assertEquals("access_token" , response.getAccessToken());
        assertEquals("refresh_token" , response.getRefreshToken());

        verify(refreshTokenService).saveRefreshToken(anyString(), eq("refresh_token"));
        verify(userRepository , never()).save(any(User.class));
        verify(jwtUtil).generateAccessToken(existingUser.getId().toString());
        verify(jwtUtil).generateRefreshToken(existingUser.getId().toString());
    }

    @Test
    void shouldThrowExceptionForWrongAuthProvider() throws  Exception {

        String fakeGoogleToken = "google_token";

        GoogleIdToken.Payload payload = mock(GoogleIdToken.Payload.class);

        when(verifierService.verify(fakeGoogleToken)).thenReturn(payload);

        when(payload.getEmail()).thenReturn("fake@gmail.com");
        when(payload.get("name")).thenReturn("name");

        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setEmail("fake@gmail.com");
        existingUser.setAuthProvider(AuthProvider.LOCAL);
        existingUser.setPassword("123456");

        when(userRepository.findByEmail("fake@gmail.com")).thenReturn(Optional.of(existingUser));

        assertThrows(WrongAuthProviderException.class , () -> authService.loginWithGoogle(fakeGoogleToken));

        verify(userRepository , never()).save(any(User.class));

        verify(refreshTokenService , never()).saveRefreshToken(anyString() , anyString());

        verify(jwtUtil, never()).generateAccessToken(anyString());
        verify(jwtUtil , never()).generateRefreshToken(anyString());
    }

    @Test
    void shouldThrowInvalidGoogleTokenException() throws Exception {

        String fakeGoogleToken = "invalidToken";

        when(verifierService.verify(fakeGoogleToken)).thenThrow(new RuntimeException("Invalid Google Token"));

        assertThrows(RuntimeException.class , ()-> authService.loginWithGoogle(fakeGoogleToken));

        verify(userRepository , never()).findByEmail(anyString());
        verify(userRepository , never()).save(any(User.class));
        verify(jwtUtil , never()).generateAccessToken(anyString());
        verify(jwtUtil , never()).generateRefreshToken(anyString());
        verify(refreshTokenService , never()).saveRefreshToken(anyString() , anyString());

    }

    @Test
    void shouldThrowUserNotFoundException() {

        LoginRequest request = new LoginRequest();
        request.setEmail("fake@gmail.com");
        request.setPassword("123456");

        when(userRepository.findByEmail("fake@gmail.com")).thenThrow(new ResourceNotFoundException("User not Found"));

        assertThrows(ResourceNotFoundException.class , () -> authService.login(request));
        verify(refreshTokenService , never()).saveRefreshToken(anyString() , anyString());
        verify(jwtUtil , never()).generateRefreshToken(anyString());
        verify(jwtUtil , never()).generateAccessToken(anyString());
    }

    @Test
    void shouldThrowInvalidCredentialsException() {

        LoginRequest request = new LoginRequest();
        request.setEmail("real@gmail.com");
        request.setPassword("fakePassword");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setAuthProvider(AuthProvider.LOCAL);
        savedUser.setUsername("realUser");
        savedUser.setEmail("real@gmail.com");
        savedUser.setPassword("realPassword");

        when(passwordEncoder.matches(
                request.getPassword(),
                savedUser.getPassword()
        )).thenReturn(false);
        when(userRepository.findByEmail("real@gmail.com")).thenReturn(Optional.of(savedUser));

        assertThrows(InvalidCredentialsException.class , () -> authService.login(request));

        verify(jwtUtil , never()).generateAccessToken(anyString());
        verify(jwtUtil , never()).generateRefreshToken(anyString());
        verify(refreshTokenService , never()).saveRefreshToken(anyString() , anyString());

    }
 }