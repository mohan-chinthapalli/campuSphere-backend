package com.major.CampuSphere.service.impl;

import com.major.CampuSphere.dto.request.UpdateFacultyProfileRequest;
import com.major.CampuSphere.dto.request.UpdateStudentProfileRequest;
import com.major.CampuSphere.dto.response.FacultyProfileResponse;
import com.major.CampuSphere.dto.response.StudentProfileResponse;
import com.major.CampuSphere.entity.*;
import com.major.CampuSphere.exception.ResourceNotFoundException;
import com.major.CampuSphere.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl {

    private final UserRepository userRepo;
    private final StudentProfileRepository studentProfileRepo;
    private final FacultyProfileRepository facultyProfileRepo;
    private final StudentSkillRepository skillRepo;

    // ─── Student Profile ──────────────────────────────────────────

    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentProfile(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        StudentProfile sp = studentProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile for user", userId));
        List<String> skills = skillRepo.findByUserId(userId).stream()
                .map(StudentSkill::getSkill)
                .toList();
        return toStudentResponse(user, sp, skills);
    }

    @Transactional
    public StudentProfileResponse updateStudentProfile(Long userId, UpdateStudentProfileRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        StudentProfile sp = studentProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile for user", userId));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
            userRepo.save(user);
        }
        if (request.getBio() != null) {
            sp.setBio(request.getBio());
        }
        studentProfileRepo.save(sp);

        // Replace skills
        if (request.getSkills() != null) {
            skillRepo.deleteByUserId(userId);
            request.getSkills().stream()
                    .filter(s -> s != null && !s.isBlank())
                    .distinct()
                    .map(s -> StudentSkill.builder().user(user).skill(s.trim()).build())
                    .forEach(skillRepo::save);
        }

        List<String> skills = skillRepo.findByUserId(userId).stream()
                .map(StudentSkill::getSkill)
                .toList();

        log.info("Student profile updated for userId={}", userId);
        return toStudentResponse(user, sp, skills);
    }

    // ─── Faculty Profile ──────────────────────────────────────────

    @Transactional(readOnly = true)
    public FacultyProfileResponse getFacultyProfile(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        FacultyProfile fp = facultyProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile for user", userId));
        return toFacultyResponse(user, fp);
    }

    @Transactional
    public FacultyProfileResponse updateFacultyProfile(Long userId, UpdateFacultyProfileRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        FacultyProfile fp = facultyProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile for user", userId));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
            userRepo.save(user);
        }
        if (request.getBio() != null) fp.setBio(request.getBio());
        if (request.getOffice() != null) fp.setOffice(request.getOffice());
        if (request.getOfficeHours() != null) fp.setOfficeHours(request.getOfficeHours());

        facultyProfileRepo.save(fp);
        log.info("Faculty profile updated for userId={}", userId);
        return toFacultyResponse(user, fp);
    }

    // ─── Mapping ──────────────────────────────────────────────────

    private StudentProfileResponse toStudentResponse(User user, StudentProfile sp, List<String> skills) {
        String initials = buildInitials(user.getName());
        return StudentProfileResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .rollNumber(sp.getRollNumber())
                .branch(sp.getBranch())
                .year(sp.getYear())
                .semester(sp.getSemester())
                .cgpa(sp.getCgpa())
                .attendance(sp.getAttendance())
                .credits(sp.getCredits())
                .bio(sp.getBio())
                .skills(skills)
                .initials(initials)
                .build();
    }

    private FacultyProfileResponse toFacultyResponse(User user, FacultyProfile fp) {
        String initials = buildInitials(user.getName());
        return FacultyProfileResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .title(fp.getTitle())
                .department(fp.getDepartment())
                .office(fp.getOffice())
                .officeHours(fp.getOfficeHours())
                .bio(fp.getBio())
                .publications(fp.getPublications())
                .citations(fp.getCitations())
                .studentCount(fp.getStudentCount())
                .initials(initials)
                .build();
    }

    private String buildInitials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return (String.valueOf(parts[0].charAt(0)) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
