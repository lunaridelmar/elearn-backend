package com.ni.la.oa.elearn.api.dto.cource;

import jakarta.validation.constraints.NotBlank;

public record CourseRequest(@NotBlank String title, String description) {}
