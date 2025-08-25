package com.ni.la.oa.elearn.repo;

import com.ni.la.oa.elearn.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByTitle(String title);
}
