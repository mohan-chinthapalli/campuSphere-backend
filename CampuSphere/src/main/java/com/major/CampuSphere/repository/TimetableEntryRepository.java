package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {
    List<TimetableEntry> findByUserIdAndDayOfWeekOrderBySortOrder(Long userId, int dayOfWeek);
    List<TimetableEntry> findByUserIdOrderByDayOfWeekAscSortOrderAsc(Long userId);
}
