package com.flab.weshare.config;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestContainerConfig {
	static final String MYSQL_IMAGE = "mysql:8";

	@Container
	static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer(MYSQL_IMAGE);
}
