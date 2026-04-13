package com.suyash.library.service;

import com.suyash.library.dto.MemberResponse;
import com.suyash.library.model.MemberRole;

import java.util.List;

public interface MemberService {
    List<MemberResponse> getAllMembers();
    MemberResponse getMemberById(Long id);
    MemberResponse getMemberByEmail(String email);
    MemberResponse assignRole(Long id, MemberRole role);
    MemberResponse toggleStatus(Long id);
    void deleteMember(Long id);
}
