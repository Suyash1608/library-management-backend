package com.suyash.library.repository;

import com.suyash.library.model.Member;
import com.suyash.library.model.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Member> findByActive(Boolean active);
    List<Member> findByRole(MemberRole role);
}
