package com.example.localvault.history.controller;

import com.example.localvault.global.response.ApiResponse;
import com.example.localvault.history.dto.HistoryResponse;
import com.example.localvault.history.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "History", description = "조회 이력 API")
@RestController
@RequestMapping("/api/v1/histories")
@RequiredArgsConstructor
public class HistoryController {

	private final HistoryService historyService;

	@Operation(summary = "조회 이력 검색")
	@GetMapping
	public ResponseEntity<ApiResponse<Page<HistoryResponse>>> getList(
			@RequestParam(value = "memberId", required = false) Long memberId,
			@RequestParam(value = "credentialId", required = false) Long credentialId,
			@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
			Pageable pageable) {
		Page<HistoryResponse> list = historyService.getList(memberId, credentialId, startDate, endDate, pageable);
		return ResponseEntity.ok(ApiResponse.ok(list));
	}
}