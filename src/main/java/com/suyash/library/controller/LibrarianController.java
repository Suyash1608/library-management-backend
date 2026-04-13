package com.suyash.library.controller;

import com.suyash.library.dto.*;
import com.suyash.library.model.MemberRole;
import com.suyash.library.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/librarian/members")
@PreAuthorize("hasRole('LIBRARIAN')")
@RequiredArgsConstructor
public class LibrarianController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAllMembers() {
        return ResponseEntity.ok(ApiResponse.success("All members", memberService.getAllMembers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Member found", memberService.getMemberById(id)));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<MemberResponse>> assignRole(
            @PathVariable Long id, @RequestParam MemberRole role) {
        return ResponseEntity.ok(ApiResponse.success("Role assigned", memberService.assignRole(id, role)));
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<MemberResponse>> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Status toggled", memberService.toggleStatus(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok(ApiResponse.success("Member deleted", null));
    }
}
