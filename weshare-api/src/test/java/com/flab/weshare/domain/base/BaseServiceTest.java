package com.flab.weshare.domain.base;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flab.weshare.domain.pay.service.CardService;

@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest {
	@InjectMocks
	CardService cardService;

}
