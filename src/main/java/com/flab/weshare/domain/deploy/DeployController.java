package com.flab.weshare.domain.deploy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("prod")
@RestController
public class DeployController {
	@Value("${container.color}")
	String containerColor;

	@GetMapping("/server")
	public String getServerInfo() {
		return containerColor;
	}
}
