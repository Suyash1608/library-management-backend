package com.suyash.library.service;

import com.suyash.library.dto.BorrowResponse;

import java.util.List;

public interface BorrowService {
    BorrowResponse borrowBook(Long bookId, String memberEmail);
    BorrowResponse returnBook(Long borrowRecordId, String memberEmail);
    List<BorrowResponse> getMyBorrowHistory(String memberEmail);
    List<BorrowResponse> getMyActiveborrows(String memberEmail);
    List<BorrowResponse> getAllBorrowRecords();
    List<BorrowResponse> getOverdueRecords();
    void markOverdueRecords();
}
