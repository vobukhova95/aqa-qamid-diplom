package data;

import lombok.*;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import page.PaymentMethodPage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {
    private SQLHelper() {
    }

    private static final QueryRunner runner = new QueryRunner();

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/app", "user", "pass");
    }

    @SneakyThrows
    public static void cleanDatabase() {
        try (Connection conn = getConnection()) {
            runner.update(conn, "DELETE FROM payment_entity;");
            runner.update(conn, "DELETE FROM order_entity;");
            runner.update(conn, "DELETE FROM credit_request_entity;");
        }
    }

    @SneakyThrows
    public static PaymentEntity getPaymentEntity() {
        var dataSQL = "SELECT * FROM payment_entity ORDER BY created DESC LIMIT 1;";
        try (var conn = getConnection()) {
            return runner.query(conn, dataSQL, new BeanHandler<>(PaymentEntity.class));
        }
    }

    @SneakyThrows
    public static OrderEntity getOrderEntity() {
        var dataSQL = "SELECT * FROM order_entity ORDER BY created DESC LIMIT 1;";
        try (var conn = getConnection()) {
            return runner.query(conn, dataSQL, new BeanHandler<>(OrderEntity.class));
        }
    }

    @SneakyThrows
    public static CreditRequestEntity getCreditRequestEntity() {
        var dataSQL = "SELECT * FROM credit_request_entity ORDER BY created DESC LIMIT 1;";
        try (var conn = getConnection()) {
            return runner.query(conn, dataSQL, new BeanHandler<>(CreditRequestEntity.class));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentEntity {
        String transactionId;
        String status;
        int amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderEntity {
        String paymentId;
        String creditId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditRequestEntity {
        String status;
        String bankId;
    }
}
