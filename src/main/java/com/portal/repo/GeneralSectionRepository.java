package com.portal.repo;

import com.portal.entity.General;
import com.portal.entity.GeneralSection;
import com.portal.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeneralSectionRepository extends JpaRepository<GeneralSection, Long> {

    GeneralSection findGeneralSectionByGeneralAndSection(General general, Section section);

    List<GeneralSection> findGeneralSectionByGeneral(General general);
}
