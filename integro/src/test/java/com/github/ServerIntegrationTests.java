package com.github;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.mockwebserver.MockWebServer;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.io.IOException;

import static org.mockito.Mockito.times;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ServerIntegrationTests {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentRepository paymentRepository;

    private static MockWebServer mockWebServer;



    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("exchange-rate-api.base-url", () -> mockWebServer.url("/").url().toString());
    }

    @BeforeAll
    static void setupMockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void deleteEntities() {
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void createOrder() {
        CurrencyUnit euro = Monetary
                .getCurrency("EUR");
        MonetaryAmount monetaryAmount = Monetary
                .getDefaultAmountFactory()
                .setCurrency(euro)
                .setNumber(100)
                .create();
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setAmount(monetaryAmount);
        Order expectedOrder = orderService.createOrder(monetaryAmount);
        webClient.post().uri("/order")
                .body(Mono.just(orderRequest), Order.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Order.class)
                .value(order -> order.getAmount())

    }

    @Test
    void payOrder() {
        // TODO: протестируйте успешную оплату ранее созданного заказа валидной картой
        // используя webClient
        // Получите `id` заказа из базы данных, используя orderRepository
    }

    @Test
    void getReceipt() {
        // TODO: Протестируйте получение чека на заказ №1 c currency = USD
        // Создайте объект Order, Payment и выполните save, используя orderRepository
        // Используйте mockWebServer для получения conversion_rate
        // Сделайте запрос через webClient
    }
}
