package com.mybatisflex.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;

public class TestInfrastructure {

    static final String FLEX_TEST_DDL = TestInfrastructure.class.getClassLoader().getResource("flex_test.sql").getPath();
    static final String FIELD_MAPPING_TEST_DDL = TestInfrastructure.class.getClassLoader().getResource("field_mapping_test.sql").getPath();
    static final String PATIENT_DATA_SPLIT_TEST_DDL = TestInfrastructure.class.getClassLoader().getResource("patient_data_split_test.sql").getPath();
    static final String DOCKER_INITDB_PATH = "/docker-entrypoint-initdb.d/";
    static GenericContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.2.0"))
        .waitingFor(Wait.forLogMessage(".*ready for connections.*\\n", 1))
        .withFileSystemBind(FLEX_TEST_DDL, DOCKER_INITDB_PATH + "flex_test.sql", BindMode.READ_ONLY)
        .withFileSystemBind(FIELD_MAPPING_TEST_DDL, DOCKER_INITDB_PATH + "field_mapping_test.sql", BindMode.READ_ONLY)
        .withFileSystemBind(PATIENT_DATA_SPLIT_TEST_DDL, DOCKER_INITDB_PATH + "patient_data_split_test.sql", BindMode.READ_ONLY)
        .withDatabaseName("flex_test")
        .withReuse(true)
        .withPassword("123456");

    @BeforeAll
    public static void start() {
        mysql.setPortBindings(Collections.singletonList("3306:3306"));
        mysql.start();
    }
}
