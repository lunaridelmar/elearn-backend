package com.ni.la.oa.elearn.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LessonRequest(@NotBlank String title, String content, Long courseId) {}
