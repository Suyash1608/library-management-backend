package com.suyash.library.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BookRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    @NotBlank(message = "Genre is required")
    private String genre;

    @NotBlank(message = "Publisher is required")
    private String publisher;

    @NotNull @Min(value = 1000, message = "Invalid published year")
    private Integer publishedYear;

    @NotNull @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;
}
