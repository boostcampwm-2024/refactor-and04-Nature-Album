package com.and04.naturealbum.data.dto

data class GreenEyeRequestBody(
    val version: String = "v1", // 서비스 버전
    val requestId: String = "", // API 호출 UUID (선택)
    val timestamp: Long, // 요청 시간의 타임스탬프 값
    val images: List<GreenEyeRequestBodyImages>,
)

data class GreenEyeRequestBodyImages(
    val name: String = "img_name",  // 이미지 식별 이름
    val data: String, // Base64로 인코딩한 이미지 바이트
)
