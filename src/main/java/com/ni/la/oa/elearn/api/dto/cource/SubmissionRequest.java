package com.ni.la.oa.elearn.api.dto.cource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmissionRequest(
        @NotNull Long questionId,
        @NotBlank String answer
) {}