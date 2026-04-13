package com.suyash.library.dto;

import com.suyash.library.model.BorrowStatus;
import lombok.*;
import java.time.LocalDate;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BorrowResponse {
    private Long id;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private String memberName;
    private String memberEmail;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BorrowStatus status;
    private Double fine;
}
