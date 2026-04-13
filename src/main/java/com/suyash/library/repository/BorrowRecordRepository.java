package com.suyash.library.repository;

import com.suyash.library.model.BorrowRecord;
import com.suyash.library.model.BorrowStatus;
import com.suyash.library.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByMember(Member member);
    List<BorrowRecord> findByStatus(BorrowStatus status);
    List<BorrowRecord> findByMemberAndStatus(Member member, BorrowStatus status);

    Optional<BorrowRecord> findByMemberAndBookIdAndStatus(Member member, Long bookId, BorrowStatus status);

    @Query("SELECT COUNT(b) FROM BorrowRecord b WHERE b.member = :member AND b.status = 'BORROWED'")
    int countActiveBorrowsByMember(Member member);

    @Query("SELECT b FROM BorrowRecord b WHERE b.status = 'BORROWED' AND b.dueDate < :today")
    List<BorrowRecord> findOverdueRecords(LocalDate today);
}
