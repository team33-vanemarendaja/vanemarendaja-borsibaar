package com.borsibaar.controller;

import com.borsibaar.dto.BarStationRequestDto;
import com.borsibaar.dto.BarStationResponseDto;
import com.borsibaar.dto.UserSummaryResponseDto;
import com.borsibaar.entity.Role;
import com.borsibaar.entity.User;
import com.borsibaar.exception.DuplicateResourceException;
import com.borsibaar.exception.NotFoundException;
import com.borsibaar.principal.UserPrincipal;
import com.borsibaar.service.BarStationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BarStationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BarStationService barStationService;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetAllStations_AsAdmin_Success() throws Exception {
        // Arrange: Create admin user
        User adminUser = createMockUser(1L, "ADMIN");
        setupSecurityContextWithUser(adminUser);

        // Arrange: Create mock station responses
        BarStationResponseDto station1 = createMockStationResponse(1L, 1L, "Station 1", true);
        BarStationResponseDto station2 = createMockStationResponse(2L, 1L, "Station 2", false);
        List<BarStationResponseDto> stations = List.of(station1, station2);

        // Arrange: Mock service
        when(barStationService.getAllStations(1L)).thenReturn(stations);

        // Act & Assert
        mockMvc.perform(get("/api/bar-stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Station 1"))
                .andExpect(jsonPath("$[0].isActive").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Station 2"))
                .andExpect(jsonPath("$[1].isActive").value(false));

        // Verify service was called
        verify(barStationService).getAllStations(1L);
    }

    @Test
    void testGetAllStations_AsNonAdmin_ReturnsForbidden() throws Exception {
        // Arrange: Create regular user
        User regularUser = createMockUser(1L, "USER");
        setupSecurityContextWithUser(regularUser);

        // Act & Assert: Non-admin should get 403 Forbidden
        mockMvc.perform(get("/api/bar-stations"))
                .andExpect(status().isForbidden());

        // Verify service was NOT called
        verify(barStationService, never()).getAllStations(anyLong());
    }

    @Test
    void testGetAllStations_Unauthenticated_ReturnsUnauthorized() throws Exception {
        // Arrange: No security context set (unauthenticated)

        // Act & Assert: Unauthenticated should get 401 Unauthorized
        mockMvc.perform(get("/api/bar-stations"))
                .andExpect(status().isUnauthorized());

        // Verify service was NOT called
        verify(barStationService, never()).getAllStations(anyLong());
    }

    @Test
    void testGetUserStations_Success() throws Exception {
        // Arrange: Create regular user
        UUID userId = UUID.randomUUID();
        User user = createMockUserWithId(userId, 1L, "USER");
        setupSecurityContextWithUser(user);

        // Arrange: Create mock station responses
        BarStationResponseDto station1 = createMockStationResponse(1L, 1L, "User Station 1", true);
        List<BarStationResponseDto> userStations = List.of(station1);

        // Arrange: Mock service
        when(barStationService.getUserStations(userId, 1L)).thenReturn(userStations);

        // Act & Assert
        mockMvc.perform(get("/api/bar-stations/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("User Station 1"))
                .andExpect(jsonPath("$[0].isActive").value(true));

        // Verify service was called
        verify(barStationService).getUserStations(userId, 1L);
    }

    @Test
    void testGetUserStations_NoStations_ReturnsEmptyList() throws Exception {
        // Arrange: Create user with no stations
        UUID userId = UUID.randomUUID();
        User user = createMockUserWithId(userId, 1L, "USER");
        setupSecurityContextWithUser(user);

        // Arrange: Mock service to return empty list
        when(barStationService.getUserStations(userId, 1L)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/bar-stations/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // Verify service was called
        verify(barStationService).getUserStations(userId, 1L);
    }

    @Test
    void testGetStationById_Success() throws Exception {
        // Arrange: Create user
        User user = createMockUser(1L, "USER");
        setupSecurityContextWithUser(user);

        // Arrange: Create mock station response
        BarStationResponseDto station = createMockStationResponse(1L, 1L, "Test Station", true);

        // Arrange: Mock service
        when(barStationService.getStationById(1L, 1L)).thenReturn(station);

        // Act & Assert
        mockMvc.perform(get("/api/bar-stations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Station"))
                .andExpect(jsonPath("$.isActive").value(true));

        // Verify service was called
        verify(barStationService).getStationById(1L, 1L);
    }

    @Test
    void testGetStationById_NotFound() throws Exception {
        // Arrange: Create user
        User user = createMockUser(1L, "USER");
        setupSecurityContextWithUser(user);

        // Arrange: Mock service to throw NotFoundException
        when(barStationService.getStationById(1L, 999L))
                .thenThrow(new NotFoundException("Bar station not found"));

        // Act & Assert
        mockMvc.perform(get("/api/bar-stations/999"))
                .andExpect(status().isNotFound());

        // Verify service was called
        verify(barStationService).getStationById(1L, 999L);
    }

    @Test
    void testCreateStation_AsAdmin_Success() throws Exception {
        // Arrange: Create admin user
        User adminUser = createMockUser(1L, "ADMIN");
        setupSecurityContextWithUser(adminUser);

        // Arrange: Create request DTO
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        BarStationRequestDto request = new BarStationRequestDto(
                "New Station",
                "Test Description",
                true,
                List.of(userId1, userId2));

        // Arrange: Create expected response
        BarStationResponseDto response = createMockStationResponse(
                1L, 1L, "New Station", true,
                List.of(
                        new UserSummaryResponseDto(userId1, "user1@test.com", "User 1", "USER"),
                        new UserSummaryResponseDto(userId2, "user2@test.com", "User 2", "USER")));

        // Arrange: Mock service
        when(barStationService.createStation(eq(1L), any(BarStationRequestDto.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/bar-stations")

                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Station"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.assignedUsers", hasSize(2)));


        // Verify service was called
        verify(barStationService).createStation(eq(1L), any(BarStationRequestDto.class));
    }

    @Test
    void testCreateStation_AsNonAdmin_ReturnsForbidden() throws Exception {
        // Arrange: Create regular user
        User regularUser = createMockUser(1L, "USER");
        setupSecurityContextWithUser(regularUser);

        // Arrange: Create request DTO
        BarStationRequestDto request = new BarStationRequestDto(
                "New Station",
                "Test Description",
                true,
                Collections.emptyList());

        // Act & Assert: Non-admin should get 403 Forbidden
        mockMvc.perform(post("/api/bar-stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // Verify service was NOT called
        verify(barStationService, never()).createStation(anyLong(), any(BarStationRequestDto.class));
    }

    @Test
    void testCreateStation_DuplicateName_ReturnsConflict() throws Exception {
        // Arrange: Create admin user
        User adminUser = createMockUser(1L, "ADMIN");
        setupSecurityContextWithUser(adminUser);

        // Arrange: Create request DTO
        BarStationRequestDto request = new BarStationRequestDto(
                "Duplicate Station",
                "Test Description",
                true,
                Collections.emptyList());

        // Arrange: Mock service to throw DuplicateResourceException
        when(barStationService.createStation(eq(1L), any(BarStationRequestDto.class)))
                .thenThrow(new DuplicateResourceException("A bar station with this name already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/bar-stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        // Verify service was called
        verify(barStationService).createStation(eq(1L), any(BarStationRequestDto.class));
    }

    @Test
    void testUpdateStation_AsAdmin_Success() throws Exception {
        // Arrange: Create admin user
        User adminUser = createMockUser(1L, "ADMIN");
        setupSecurityContextWithUser(adminUser);

        // Arrange: Create request DTO
        UUID userId = UUID.randomUUID();
        BarStationRequestDto request = new BarStationRequestDto(
                "Updated Station",
                "Updated Description",
                false,
                List.of(userId));

        // Arrange: Create expected response
        BarStationResponseDto response = createMockStationResponse(
                1L, 1L, "Updated Station", "Updated Description", false,
                List.of(new UserSummaryResponseDto(userId, "user@test.com", "User", "USER")));

        // Arrange: Mock service
        when(barStationService.updateStation(eq(1L), eq(1L), any(BarStationRequestDto.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/bar-stations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Station"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.isActive").value(false))
                .andExpect(jsonPath("$.assignedUsers", hasSize(1)));

        // Verify service was called
        verify(barStationService).updateStation(eq(1L), eq(1L), any(BarStationRequestDto.class));
    }

    @Test
    void testUpdateStation_AsNonAdmin_ReturnsForbidden() throws Exception {
        // Arrange: Create regular user
        User regularUser = createMockUser(1L, "USER");
        setupSecurityContextWithUser(regularUser);

        // Arrange: Create request DTO
        BarStationRequestDto request = new BarStationRequestDto(
                "Updated Station",
                "Updated Description",
                true,
                Collections.emptyList());

        // Act & Assert: Non-admin should get 403 Forbidden
        mockMvc.perform(put("/api/bar-stations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // Verify service was NOT called
        verify(barStationService, never()).updateStation(anyLong(), anyLong(), any(BarStationRequestDto.class));
    }

    @Test
    void testUpdateStation_NotFound() throws Exception {
        // Arrange: Create admin user
        User adminUser = createMockUser(1L, "ADMIN");
        setupSecurityContextWithUser(adminUser);

        // Arrange: Create request DTO
        BarStationRequestDto request = new BarStationRequestDto(
                "Updated Station",
                "Updated Description",
                true,
                Collections.emptyList());

        // Arrange: Mock service to throw NotFoundException
        when(barStationService.updateStation(eq(1L), eq(999L), any(BarStationRequestDto.class)))
                .thenThrow(new NotFoundException("Bar station not found"));

        // Act & Assert
        mockMvc.perform(put("/api/bar-stations/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // Verify service was called
        verify(barStationService).updateStation(eq(1L), eq(999L), any(BarStationRequestDto.class));
    }

    @Test
    void testUpdateStation_DuplicateName_ReturnsConflict() throws Exception {
        // Arrange: Create admin user
        User adminUser = createMockUser(1L, "ADMIN");
        setupSecurityContextWithUser(adminUser);

        // Arrange: Create request DTO
        BarStationRequestDto request = new BarStationRequestDto(
                "Duplicate Station",
                "Updated Description",
                true,
                Collections.emptyList());

        // Arrange: Mock service to throw DuplicateResourceException
        when(barStationService.updateStation(eq(1L), eq(1L), any(BarStationRequestDto.class)))
                .thenThrow(new DuplicateResourceException("A bar station with this name already exists"));

        // Act & Assert
        mockMvc.perform(put("/api/bar-stations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        // Verify service was called
        verify(barStationService).updateStation(eq(1L), eq(1L), any(BarStationRequestDto.class));
    }

    @Test
    void testDeleteStation_AsAdmin_Success() throws Exception {
        // Arrange: Create admin user
        User adminUser = createMockUser(1L, "ADMIN");
        setupSecurityContextWithUser(adminUser);

        // Arrange: Mock service (void method, no return)
        doNothing().when(barStationService).deleteStation(1L, 1L);

        // Act & Assert: DELETE returns 204 No Content
        mockMvc.perform(delete("/api/bar-stations/1"))
                .andExpect(status().isNoContent());

        // Verify service was called
        verify(barStationService).deleteStation(1L, 1L);
    }

    @Test
    void testDeleteStation_AsNonAdmin_ReturnsForbidden() throws Exception {
        // Arrange: Create regular user
        User regularUser = createMockUser(1L, "USER");
        setupSecurityContextWithUser(regularUser);

        // Act & Assert: Non-admin should get 403 Forbidden
        mockMvc.perform(delete("/api/bar-stations/1"))
                .andExpect(status().isForbidden());

        // Verify service was NOT called
        verify(barStationService, never()).deleteStation(anyLong(), anyLong());
    }

    @Test
    void testDeleteStation_NotFound() throws Exception {
        // Arrange: Create admin user
        User adminUser = createMockUser(1L, "ADMIN");
        setupSecurityContextWithUser(adminUser);

        // Arrange: Mock service to throw NotFoundException
        doThrow(new NotFoundException("Bar station not found"))
                .when(barStationService).deleteStation(1L, 999L);

        // Act & Assert
        mockMvc.perform(delete("/api/bar-stations/999"))
                .andExpect(status().isNotFound());

        // Verify service was called
        verify(barStationService).deleteStation(1L, 999L);
    }

    /**
     * Helper method to create a mock user with organization and role.
     */
    private User createMockUser(Long organizationId, String roleName) {
        Role role = Role.builder()
                .id(1L)
                .name(roleName)
                .build();

        return User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .name("Test User")
                .organizationId(organizationId)
                .role(role)
                .build();
    }

    /**
     * Helper method to create a mock user with specific ID, organization, and role.
     */
    private User createMockUserWithId(UUID userId, Long organizationId, String roleName) {
        Role role = Role.builder()
                .id(1L)
                .name(roleName)
                .build();

        return User.builder()
                .id(userId)
                .email("test@example.com")
                .name("Test User")
                .organizationId(organizationId)
                .role(role)
                .build();
    }

    /**
     * Helper method to create a mock bar station response DTO.
     */
    private BarStationResponseDto createMockStationResponse(
            Long id, Long organizationId, String name, Boolean isActive) {
        return new BarStationResponseDto(
                id,
                organizationId,
                name,
                "Test Description",
                isActive,
                Collections.emptyList(),
                Instant.now(),
                Instant.now());
    }

    /**
     * Helper method to create a mock bar station response DTO with assigned users.
     */
    private BarStationResponseDto createMockStationResponse(
            Long id, Long organizationId, String name, Boolean isActive,
            List<UserSummaryResponseDto> assignedUsers) {
        return new BarStationResponseDto(
                id,
                organizationId,
                name,
                "Test Description",
                isActive,
                assignedUsers,
                Instant.now(),
                Instant.now());
    }

    /**
     * Helper method to create a mock bar station response DTO with assigned users and description.
     */
    private BarStationResponseDto createMockStationResponse(
            Long id, Long organizationId, String name, String description,
            Boolean isActive, List<UserSummaryResponseDto> assignedUsers) {
        return new BarStationResponseDto(
                id,
                organizationId,
                name,
                description,
                isActive,
                assignedUsers,
                Instant.now(),
                Instant.now());
    }

    /**
     * Helper method to setup SecurityContext with a mock authenticated user.
     */
    private void setupSecurityContextWithUser(User user) {
        UserPrincipal principal = new UserPrincipal(user);
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())
        );
        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}