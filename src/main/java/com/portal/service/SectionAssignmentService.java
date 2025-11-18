package com.portal.service;

import com.portal.entity.Project;
import com.portal.entity.Section;
import com.portal.entity.SectionAssignment;
import com.portal.entity.User;
import com.portal.enums.UserRole;
import com.portal.repo.SectionAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SectionAssignmentService {

    private final SectionAssignmentRepository sectionAssignmentRepository;
    private final UserService userService;

    public SectionAssignmentService(SectionAssignmentRepository sectionAssignmentRepository, UserService userService) {
        this.sectionAssignmentRepository = sectionAssignmentRepository;
        this.userService = userService;
    }

    public SectionAssignment assignUserToSection(Project project, Section section, User user) {
        // Удаляем существующие назначения для этого раздела в проекте
        sectionAssignmentRepository.deleteByProjectAndSection(project, section);

        // Создаем новое назначение
        SectionAssignment assignment = new SectionAssignment(project, section, user);
        return sectionAssignmentRepository.save(assignment);
    }

    public List<SectionAssignment> getAssignmentsForProject(Project project) {
        return sectionAssignmentRepository.findByProject(project);
    }

    public List<SectionAssignment> getAssignmentsForSection(Project project, Section section) {
        return sectionAssignmentRepository.findByProjectAndSection(project, section);
    }

    public List<User> getAssignedUsersForSection(Project project, Section section) {
        return sectionAssignmentRepository.findByProjectAndSection(project, section)
                .stream()
                .map(SectionAssignment::getAssignedUser)
                .toList();
    }

    public void removeAssignment(Project project, Section section, User user) {
        sectionAssignmentRepository.findByProjectAndSectionAndAssignedUser(project, section, user)
                .ifPresent(sectionAssignmentRepository::delete);
    }

    public void removeAllAssignmentsForSection(Project project, Section section) {
        sectionAssignmentRepository.deleteByProjectAndSection(project, section);
    }

    public boolean isUserAssignedToSection(Project project, Section section, User user) {
        return sectionAssignmentRepository.findByProjectAndSectionAndAssignedUser(project, section, user)
                .isPresent();
    }

    public List<User> getAvailableContractors() {
        return userService.getUsersByRole(UserRole.CONTRACTOR);
    }

    public List<SectionAssignment> getAssignmentsForUser(User user) {
        return sectionAssignmentRepository.findSectionAssignmentByAssignedUser(user);
    }
    public List<SectionAssignment> getAssignmentsByProjectAndUser(Project project, User user) {
        return sectionAssignmentRepository.findSectionAssignmentByAssignedUserAndAndProject(user, project);
    }

}