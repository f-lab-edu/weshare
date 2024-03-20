package com.flab.weshare.domain.party.entity;

public enum PartyCapsuleStatus {
	EMPTY //빈 상태
	, OCCUPIED //파티원에 의해 점유중
	, WITHDRAWN //파티원이 파티에서 이탈
	, DELETED //삭제됨
	, CLOSED //파티가 닫힘으로 인해 종료.
}
