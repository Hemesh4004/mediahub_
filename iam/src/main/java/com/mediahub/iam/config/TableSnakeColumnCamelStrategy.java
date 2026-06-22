package com.mediahub.iam.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * Physical naming strategy that keeps table names in {@code snake_case} (the
 * inherited Spring Boot / Hibernate default) while leaving column names exactly
 * as declared on the entity fields, i.e. {@code camelCase}.
 *
 * <p>Spring Boot 4.0 dropped {@code SpringPhysicalNamingStrategy} and defaults
 * to Hibernate's {@link PhysicalNamingStrategySnakeCaseImpl}, which snake_cases
 * every identifier. We extend it and override only the column conversion so that
 * {@code firstName} stays {@code firstName} but entity {@code UserRole} still
 * maps to table {@code user_role}.
 */
public class TableSnakeColumnCamelStrategy extends PhysicalNamingStrategySnakeCaseImpl {

    @Override
    public Identifier toPhysicalColumnName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return logicalName;
    }
}
