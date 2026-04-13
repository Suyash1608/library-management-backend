package com.suyash.library.controller;

import com.suyash.library.dto.*;
import com.suyash.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    // Member endpoints
    @PostMapping("/{bookId}")
    public ResponseEntity<ApiResponse<BorrowResponse>> borrowBook(
            @PathVariable Long bookId, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book borrowed successfully",
                        borrowService.borrowBook(bookId, principal.getName())));
    }

    @PutMapping("/return/{borrowRecordId}")
    public ResponseEntity<ApiResponse<BorrowResponse>> returnBook(
            @PathVariable Long borrowRecordId, Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Book returned successfully",
                borrowService.returnBook(borrowRecordId, principal.getName())));
    }

    @GetMapping("/my/history")
    public ResponseEntity<ApiResponse<List<BorrowResponse>>> getMyHistory(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Borrow history",
                borrowService.getMyBorrowHistory(principal.getName())));
    }

    @GetMapping("/my/active")
    public ResponseEntity<ApiResponse<List<BorrowResponse>>> getMyActive(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success("Active borrows",
                borrowService.getMyActiveborrows(principal.getName())));
    }

    // Librarian endpoints
    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<BorrowResponse>>> getAllRecords() {
        return ResponseEntity.ok(ApiResponse.success("All borrow records",
                borrowService.getAllBorrowRecords()));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<List<BorrowResponse>>> getOverdue() {
        return ResponseEntity.ok(ApiResponse.success("Overdue records",
                borrowService.getOverdueRecords()));
    }

    @PutMapping("/mark-overdue")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResponse<Void>> markOverdue() {
        borrowService.markOverdueRecords();
        return ResponseEntity.ok(ApiResponse.success("Overdue records updated", null));
    }
}
