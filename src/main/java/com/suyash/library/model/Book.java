package com.suyash.library.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books",
       indexes = {
           @Index(name = "idx_isbn", columnList = "isbn"),
           @Index(name = "idx_author", columnList = "author"),
           @Index(name = "idx_genre", columnList = "genre")
       })
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private Integer publishedYear;

    @Column(nullable = false)
    private Integer totalCopies;

    @Column(nullable = false)
    private Integer availableCopies;
}
