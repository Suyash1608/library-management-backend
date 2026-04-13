package com.suyash.library.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private String publisher;
    private Integer publishedYear;
    private Integer totalCopies;
    private Integer availableCopies;
}
