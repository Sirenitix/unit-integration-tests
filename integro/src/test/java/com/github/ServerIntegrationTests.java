package com.github;


import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ServerIntegrationTests {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private static MockWebServer mockWebServer;

    @Autowired
    private  ExchangeClientProperties properties;


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

    void requestToCreateOrder(String currencyCode){
        CurrencyUnit euro = Monetary
                .getCurrency(currencyCode);
        MonetaryAmount monetaryAmount = Monetary
                .getDefaultAmountFactory()
                .setCurrency(euro)
                .setNumber(100)
                .create();
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setAmount(monetaryAmount);
        webClient.post().uri("/order")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(orderRequest), OrderRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Order.class)
                .consumeWith(result -> {
                    Order order = result.getResponseBody();
                    assert order != null;
                    assertThat(order.getAmount().intValue()).isEqualTo(new BigDecimal(100).intValue());
                });
    }

    @Test
    void createOrder() {
        String currencyCode  =  "EUR";
        requestToCreateOrder(currencyCode);
    }

    @Test
    void payOrder() {
        // TODO: протестируйте успешную оплату ранее созданного заказа валидной картой
        createOrder();
        Long orderId = orderRepository.findAll()
                                      .get(0)
                                      .getId();
        log.info(orderId + " - orderId");
        String creditCardNumber= "4109842488675601";
       requestToPayOrder(orderId, creditCardNumber);


    }

    void requestToPayOrder(Long orderId ,String creditCardNumber){
        PaymentRequest paymentRequest = new PaymentRequest(creditCardNumber);
        webClient.post().uri("/order/" + orderId + "/payment")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(paymentRequest), PaymentRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PaymentResponse.class)
                .consumeWith(result -> {
                    PaymentResponse paymentResponse = result.getResponseBody();
                    assert paymentResponse != null;
                    assertThat(paymentResponse.getOrderId()).isEqualTo(orderId);
                    assertThat(paymentResponse.getCreditCardNumber()).isEqualTo(creditCardNumber);
                    log.info(paymentResponse.getCreditCardNumber());
                });

    }

    @Test
    void getReceipt() {
        // TODO: Протестируйте получение чека на заказ №1 c currency = USD

        String currencyCode = "USD";
        requestToCreateOrder(currencyCode);

        String creditCardNumber= "3702466327559538";
        Long orderId = orderRepository.findAll()
                .get(0)
                .getId();
        log.info(String.valueOf(orderId));
        requestToPayOrder(orderId, creditCardNumber);

        CurrencyUnit currencyUnit = Monetary
                .getCurrency(currencyCode);

        String apiKey = properties.getApiKey();
        String baseUrl ="https://v6.exchangerate-api.com/";
        ExchangeResponse exchangeResponse = new ExchangeResponse();
        webClient.get().uri(baseUrl + "/v6/{apiKey}/pair/{from}/{to}", apiKey, Monetary.getCurrency("EUR"), currencyUnit)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(ExchangeResponse.class)
                .consumeWith(response -> {
                    exchangeResponse.setResult(Objects.requireNonNull(response.getResponseBody()).getResult());
                    exchangeResponse.setConversionRate(response.getResponseBody().getConversionRate());
                });
        assertThat(exchangeResponse.getResult()).isNotNull();
        assertThat(exchangeResponse.getConversionRate()).isNotNull();

    }
}
