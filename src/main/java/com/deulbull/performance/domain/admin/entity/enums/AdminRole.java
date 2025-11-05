package com.deulbull.performance.domain.admin.entity.enums;

public enum AdminRole {
    VIEWER, // 보기만 가능
    EDITOR, // 수정 가능
    ADMIN, // 동아리별 최상위 관리자
    MASTER // 시스템 전체 최상위 관리자
}