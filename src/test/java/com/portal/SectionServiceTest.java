package com.portal;

import com.portal.entity.Section;
import com.portal.repo.SectionRepository;
import com.portal.service.SectionService;
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
class SectionServiceTest {

    @Mock
    private SectionRepository sectionRepository;

    @InjectMocks
    private SectionService sectionService;

    private Section section1;
    private Section section2;

    @BeforeEach
    void setUp() {
        section1 = new Section();
        section1.setId(1L);
        section1.setName("Section 1");

        section2 = new Section();
        section2.setId(2L);
        section2.setName("Section 2");
    }

    @Test
    void getAllSections_ShouldReturnAllSections() {
        // Arrange
        List<Section> expectedSections = Arrays.asList(section1, section2);
        when(sectionRepository.findAll()).thenReturn(expectedSections);

        // Act
        List<Section> actualSections = sectionService.getAllSections();

        // Assert
        assertNotNull(actualSections);
        assertEquals(2, actualSections.size());
        assertEquals(expectedSections, actualSections);
        verify(sectionRepository, times(1)).findAll();
    }

    @Test
    void createSection_ShouldSaveAndReturnSection() {
        // Arrange
        when(sectionRepository.save(section1)).thenReturn(section1);

        // Act
        Section result = sectionService.createSection(section1);

        // Assert
        assertNotNull(result);
        assertEquals(section1, result);
        verify(sectionRepository, times(1)).save(section1);
    }

    @Test
    void getSectionById_ShouldReturnSection() {
        // Arrange
        Long sectionId = 1L;
        when(sectionRepository.getReferenceById(sectionId)).thenReturn(section1);

        // Act
        Section result = sectionService.getSectionById(sectionId);

        // Assert
        assertNotNull(result);
        assertEquals(section1, result);
        verify(sectionRepository, times(1)).getReferenceById(sectionId);
    }
}