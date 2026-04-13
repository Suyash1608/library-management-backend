package com.suyash.library.serviceimpl;

import com.suyash.library.dto.MemberResponse;
import com.suyash.library.exception.ResourceNotFoundException;
import com.suyash.library.model.BorrowStatus;
import com.suyash.library.model.Member;
import com.suyash.library.model.MemberRole;
import com.suyash.library.repository.BorrowRecordRepository;
import com.suyash.library.repository.MemberRepository;
import com.suyash.library.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    @Override
    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public MemberResponse getMemberById(Long id) {
        return mapToResponse(findOrThrow(id));
    }

    @Override
    public MemberResponse getMemberByEmail(String email) {
        return mapToResponse(memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + email)));
    }

    @Override
    @Transactional
    public MemberResponse assignRole(Long id, MemberRole role) {
        Member member = findOrThrow(id);
        member.setRole(role);
        return mapToResponse(memberRepository.save(member));
    }

    @Override
    @Transactional
    public MemberResponse toggleStatus(Long id) {
        Member member = findOrThrow(id);
        member.setActive(!member.getActive());
        return mapToResponse(memberRepository.save(member));
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        Member member = findOrThrow(id);
        int activeBorrows = borrowRecordRepository.countActiveBorrowsByMember(member);
        if (activeBorrows > 0) {
            throw new IllegalArgumentException(
                "Cannot delete member — they have " + activeBorrows + " active borrow(s)");
        }
        memberRepository.deleteById(id);
    }

    private Member findOrThrow(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    private MemberResponse mapToResponse(Member m) {
        int activeBorrows = borrowRecordRepository.countActiveBorrowsByMember(m);
        return MemberResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .email(m.getEmail())
                .phone(m.getPhone())
                .role(m.getRole())
                .active(m.getActive())
                .memberSince(m.getMemberSince())
                .activeBorrows(activeBorrows)
                .build();
    }
}
