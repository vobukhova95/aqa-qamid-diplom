package data;

import dev.failsafe.internal.util.Assert;
import io.qameta.allure.Step;
import lombok.*;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.junit.jupiter.api.Assertions;
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
    private static PaymentEntity getPaymentEntity() {
        var dataSQL = "SELECT * FROM payment_entity ORDER BY created DESC LIMIT 1;";
        try (var conn = getConnection()) {
            return runner.query(conn, dataSQL, new BeanHandler<>(PaymentEntity.class));
        }
    }

    @SneakyThrows
    private static OrderEntity getOrderEntity() {
        var dataSQL = "SELECT * FROM order_entity ORDER BY created DESC LIMIT 1;";
        try (var conn = getConnection()) {
            return runner.query(conn, dataSQL, new BeanHandler<>(OrderEntity.class));
        }
    }

    @SneakyThrows
    private static CreditRequestEntity getCreditRequestEntity() {
        var dataSQL = "SELECT * FROM credit_request_entity ORDER BY created DESC LIMIT 1;";
        try (var conn = getConnection()) {
            return runner.query(conn, dataSQL, new BeanHandler<>(CreditRequestEntity.class));
        }
    }

    @SneakyThrows
    @Step("Check that last payment status is {expectedStatus}")
    public static void checkPaymentStatus(String expectedStatus) {
        PaymentEntity payment = getPaymentEntity();
        Assertions.assertEquals(expectedStatus, payment.getStatus(), "Payment status mismatch");
    }

    @SneakyThrows
    @Step("Check that last payment amount is {expectedAmount}")
    public static void checkPaymentAmount(int expectedAmount) {
        PaymentEntity payment = getPaymentEntity();
        Assertions.assertEquals(expectedAmount, payment.getAmount(), "Payment amount mismatch");
    }

    @SneakyThrows
    @Step("Check that order is linked to payment")
    public static void checkOrderLinkedToPayment() {
        PaymentEntity payment = getPaymentEntity();
        OrderEntity order = getOrderEntity();
        Assertions.assertEquals(payment.getTransactionId(), order.getPaymentId(), "Order is not linked to the correct payment");
    }

    @Step("Verify that no records exist in 'payment_entity' table")
    public static void assertNoPayments() {
        PaymentEntity payment = getPaymentEntity();
        Assertions.assertNull(payment, "Expected no records in 'payment_entity', but a record was found: " + payment);
    }

    @Step("Verify that no records exist in 'order_entity' table")
    public static void assertNoOrders() {
        OrderEntity order = getOrderEntity();
        Assertions.assertNull(order, "Expected no records in 'order_entity', but a record was found: " + order);
    }

    @Step("Verify that no records exist in 'credit_request_entity' table")
    public static void assertNoCreditRequests() {
        CreditRequestEntity creditRequest = getCreditRequestEntity();
        Assertions.assertNull(creditRequest, "Expected no records in 'credit_request_entity', but a record was found: " + creditRequest);
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
