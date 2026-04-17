package com.example.localvault.credential.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.localvault.credential.domain.Credential;
import com.example.localvault.credential.domain.CredentialType;
import com.example.localvault.credential.dto.CredentialRequest;
import com.example.localvault.credential.dto.CredentialResponse;
import com.example.localvault.credential.repository.CredentialRepository;
import com.example.localvault.global.exception.BusinessException;
import com.example.localvault.global.exception.ErrorCode;
import com.example.localvault.global.util.CryptoUtil;
import com.example.localvault.history.domain.ActionType;
import com.example.localvault.history.service.HistoryService;
import com.example.localvault.member.domain.Member;
import com.example.localvault.member.domain.MemberRole;
import com.example.localvault.member.repository.MemberRepository;
import com.example.localvault.permission.repository.PermissionRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CredentialService {

	private final CredentialRepository credentialRepository;
	private final MemberRepository memberRepository;
	private final CryptoUtil cryptoUtil;
	private final HistoryService historyService;
	private final PermissionRepository permissionRepository;

	// 비밀번호 등록
	@Transactional
	public Long create(CredentialRequest request, Long memberId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new BusinessException(ErrorCode.PERM_MEMBER_NOT_FOUND));

		// 비밀번호 암호화
		CryptoUtil.EncryptedData encrypted = cryptoUtil.encrypt(request.getPassword());

		Credential credential = Credential.builder().serviceName(request.getServiceName())
				.credentialType(request.getCredentialType()).url(request.getUrl()).username(request.getUsername())
				.encryptedPassword(encrypted.cipherText()).iv(encrypted.iv()).port(request.getPort())
				.description(request.getDescription()).createdBy(member).build();

		return credentialRepository.save(credential).getId();
	}

	// 목록 조회 (마스킹)
	public Page<CredentialResponse> getList(CredentialType type, String keyword, Long memberId, Pageable pageable) {
	    Member member = memberRepository.findById(memberId)
	            .orElseThrow(() -> new BusinessException(ErrorCode.PERM_MEMBER_NOT_FOUND));

	    Page<Credential> credentials;
	    
	    if (member.getRole() == MemberRole.ADMIN) {
	        // 관리자는 전체 조회
	        credentials = credentialRepository.findAllByCondition(type, keyword, pageable);
	    } else {
	        // 일반 사용자는 권한 있는 것만
	        credentials = credentialRepository.findAllByConditionAndMemberId(type, keyword, memberId, pageable);
	    }

	    return credentials.map(CredentialResponse::ofMasked);
	}

	// 상세 조회 (복호화)
	@Transactional
	public CredentialResponse reveal(Long id, Long memberId, HttpServletRequest request) {
	    Credential credential = credentialRepository.findById(id)
	            .orElseThrow(() -> new BusinessException(ErrorCode.PERM_CREDENTIAL_NOT_FOUND));

	    if (credential.isDeleted()) {
	        throw new BusinessException(ErrorCode.PERM_CREDENTIAL_NOT_FOUND);
	    }

	    // 권한 체크 (관리자는 패스)
	    Member member = memberRepository.findById(memberId)
	            .orElseThrow(() -> new BusinessException(ErrorCode.PERM_MEMBER_NOT_FOUND));
	    
	    if (member.getRole() != MemberRole.ADMIN) {
	        // 일반 사용자는 권한 체크
	        if (!permissionRepository.existsByMemberIdAndCredentialId(memberId, id)) {
	            throw new BusinessException(ErrorCode.PERM_ACCESS_DENIED);
	        }
	    }

	    // 복호화
	    String decrypted = cryptoUtil.decrypt(credential.getEncryptedPassword(), credential.getIv());

	    // 조회 이력 기록
	    String ipAddress = getClientIp(request);
	    String userAgent = request.getHeader("User-Agent");
	    historyService.record(memberId, id, ActionType.VIEW, ipAddress, userAgent);

	    return CredentialResponse.ofRevealed(credential, decrypted);
	}

	// 수정
	@Transactional
	public void update(Long id, CredentialRequest request) {
		Credential credential = credentialRepository.findById(id)
				.orElseThrow(() -> new BusinessException(ErrorCode.PERM_CREDENTIAL_NOT_FOUND));

		// 비밀번호 암호화
		CryptoUtil.EncryptedData encrypted = cryptoUtil.encrypt(request.getPassword());

		credential.update(request.getServiceName(), request.getCredentialType(), request.getUrl(),
				request.getUsername(), encrypted.cipherText(), encrypted.iv(), request.getPort(),
				request.getDescription());
	}

	// 삭제 (Soft Delete)
	@Transactional
	public void delete(Long id) {
		Credential credential = credentialRepository.findById(id)
				.orElseThrow(() -> new BusinessException(ErrorCode.PERM_CREDENTIAL_NOT_FOUND));

		credential.delete();
	}

	private String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.isEmpty()) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}