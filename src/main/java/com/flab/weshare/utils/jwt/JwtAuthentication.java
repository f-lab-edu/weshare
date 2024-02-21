package com.flab.weshare.utils.jwt;

import java.util.Date;

public record JwtAuthentication(String token, Long id, Date expirationTime) {
}
