package com.portal;

import com.portal.entity.General;
import com.portal.repo.GeneralRepository;
import com.portal.service.GeneralService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeneralServiceTest {

    @Mock
    private GeneralRepository generalRepository;

    @InjectMocks
    private GeneralService generalService;

    private General general1;
    private General general2;

    @BeforeEach
    void setUp() {
        general1 = new General();
        general1.setId(1L);
        general1.setName("General 1");

        general2 = new General();
        general2.setId(2L);
        general2.setName("General 2");
    }

    @Test
    void getAllGenerals_ShouldReturnAllGenerals() {
        // Arrange
        List<General> expectedGenerals = Arrays.asList(general1, general2);
        when(generalRepository.findAll()).thenReturn(expectedGenerals);

        // Act
        List<General> actualGenerals = generalService.getAllGenerals();

        // Assert
        assertNotNull(actualGenerals);
        assertEquals(2, actualGenerals.size());
        assertEquals(expectedGenerals, actualGenerals);
        verify(generalRepository, times(1)).findAll();
    }

    @Test
    void createGeneral_ShouldSaveAndReturnGeneral() {
        // Arrange
        when(generalRepository.save(general1)).thenReturn(general1);

        // Act
        General result = generalService.createGeneral(general1);

        // Assert
        assertNotNull(result);
        assertEquals(general1, result);
        verify(generalRepository, times(1)).save(general1);
    }

    @Test
    void getGeneralById_WhenGeneralExists_ShouldReturnGeneral() {
        // Arrange
        Long generalId = 1L;
        when(generalRepository.findById(generalId)).thenReturn(Optional.of(general1));

        // Act
        General result = generalService.getGeneralById(generalId);

        // Assert
        assertNotNull(result);
        assertEquals(general1, result);
        verify(generalRepository, times(1)).findById(generalId);
    }

    @Test
    void getGeneralById_WhenGeneralNotExists_ShouldThrowException() {
        // Arrange
        Long generalId = 99L;
        when(generalRepository.findById(generalId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> generalService.getGeneralById(generalId));

        assertEquals("General not found with id: " + generalId, exception.getMessage());
        verify(generalRepository, times(1)).findById(generalId);
    }
}
