package com.femcoders.sitme.space.dto;

import com.femcoders.sitme.space.SpaceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;


public record SpaceRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 60, message = "Name must be less than 60 characters")
        String name,

        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1")
        Integer capacity,

        @NotNull(message = "Type is required")
        SpaceType type,

        @NotNull(message = "Availability must be specified")
        Boolean isAvailable,

        @Size(max = 500, message = "Image URL must be less than 500 characters")
        @URL(message = "Image URL must be a valid URL")
        String imageUrl
) {
}

