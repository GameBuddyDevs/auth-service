package com.back2261.authservice.interfaces.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeAgeRequest {
    @Min(value = 12, message = "Age must be at least 12 years old")
    @Max(value = 99, message = "Age must be at most 99 years old")
    private int age;
}
