//package com.portal;
//
//import com.portal.entity.Chapter;
//import com.portal.entity.General;
//import com.portal.entity.Section;
//import com.portal.repo.ChapterRepository;
//import com.portal.repo.GeneralRepository;
//import com.portal.repo.SectionRepository;
//import com.portal.service.ChapterService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ChapterServiceTest {
//
//    @Mock
//    private ChapterRepository chapterRepository;
//
//    @Mock
//    private GeneralRepository generalRepository;
//
//    @Mock
//    private SectionRepository sectionRepository;
//
//    @InjectMocks
//    private ChapterService chapterService;
//
//    private Chapter chapter;
//    private General general;
//    private Section section1, section2;
//
//    @BeforeEach
//    void setUp() {
//        general = new General();
//        general.setId(1L);
//        general.setName("Test General");
//
//        section1 = new Section();
//        section1.setId(1L);
//        section1.setName("Section 1");
//
//        section2 = new Section();
//        section2.setId(2L);
//        section2.setName("Section 2");
//
//        chapter = new Chapter();
//        chapter.setId(1L);
//        chapter.setName("Test Chapter");
//        chapter.setGeneral(general);
//    }
//
//    @Test
//    void createChapter_WithValidGeneralId_ShouldCreateChapter() {
//        // Arrange
//        Long generalId = 1L;
//        when(generalRepository.findById(generalId)).thenReturn(Optional.of(general));
//        when(chapterRepository.save(chapter)).thenReturn(chapter);
//
//        // Act
//        Chapter result = chapterService.createChapter(chapter, generalId);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(chapter, result);
//        assertEquals(general, result.getGeneral());
//        verify(generalRepository, times(1)).findById(generalId);
//        verify(chapterRepository, times(1)).save(chapter);
//    }
//
//    @Test
//    void createChapter_WithInvalidGeneralId_ShouldThrowException() {
//        // Arrange
//        Long generalId = 99L;
//        when(generalRepository.findById(generalId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> chapterService.createChapter(chapter, generalId));
//
//        assertEquals("General not found with id: " + generalId, exception.getMessage());
//        verify(chapterRepository, never()).save(any());
//    }
//
//    @Test
//    void updateChapterSections_WithValidSectionIds_ShouldUpdateSections() {
//        // Arrange
//        Long chapterId = 1L;
//        List<Long> sectionIds = Arrays.asList(1L, 2L);
//        List<Section> sections = Arrays.asList(section1, section2);
//
//        when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(chapter));
//        when(sectionRepository.findAllById(sectionIds)).thenReturn(sections);
//        when(chapterRepository.save(chapter)).thenReturn(chapter);
//
//        // Act
//        chapterService.updateChapterSections(chapterId, sectionIds);
//
//        // Assert
//        assertEquals(sections, chapter.getSections());
//        verify(chapterRepository, times(1)).findById(chapterId);
//        verify(sectionRepository, times(1)).findAllById(sectionIds);
//        verify(chapterRepository, times(1)).save(chapter);
//    }
//
//    @Test
//    void updateChapterSections_WithEmptySectionIds_ShouldClearSections() {
//        // Arrange
//        Long chapterId = 1L;
//        List<Long> sectionIds = Collections.emptyList();
//
//        when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(chapter));
//        when(chapterRepository.save(chapter)).thenReturn(chapter);
//
//        // Act
//        chapterService.updateChapterSections(chapterId, sectionIds);
//
//        // Assert
//        assertTrue(chapter.getSections().isEmpty());
//        verify(chapterRepository, times(1)).findById(chapterId);
//        verify(sectionRepository, never()).findAllById(any());
//        verify(chapterRepository, times(1)).save(chapter);
//    }
//
//    @Test
//    void updateChapterSections_WithNullSectionIds_ShouldClearSections() {
//        // Arrange
//        Long chapterId = 1L;
//
//        when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(chapter));
//        when(chapterRepository.save(chapter)).thenReturn(chapter);
//
//        // Act
//        chapterService.updateChapterSections(chapterId, null);
//
//        // Assert
//        assertTrue(chapter.getSections().isEmpty());
//        verify(chapterRepository, times(1)).findById(chapterId);
//        verify(sectionRepository, never()).findAllById(any());
//        verify(chapterRepository, times(1)).save(chapter);
//    }
//
//    @Test
//    void updateChapterSections_WithInvalidChapterId_ShouldThrowException() {
//        // Arrange
//        Long chapterId = 99L;
//        List<Long> sectionIds = Arrays.asList(1L, 2L);
//
//        when(chapterRepository.findById(chapterId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class,
//                () -> chapterService.updateChapterSections(chapterId, sectionIds));
//
//        assertEquals("Chapter not found with id: " + chapterId, exception.getMessage());
//        verify(sectionRepository, never()).findAllById(any());
//        verify(chapterRepository, never()).save(any());
//    }
//
//    @Test
//    void getChaptersByGeneral_ShouldReturnChapters() {
//        // Arrange
//        Long generalId = 1L;
//        List<Chapter> expectedChapters = Arrays.asList(chapter);
//        when(chapterRepository.findByGeneralId(generalId)).thenReturn(expectedChapters);
//
//        // Act
//        List<Chapter> result = chapterService.getChaptersByGeneral(generalId);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(expectedChapters, result);
//        verify(chapterRepository, times(1)).findByGeneralId(generalId);
//    }
//}
