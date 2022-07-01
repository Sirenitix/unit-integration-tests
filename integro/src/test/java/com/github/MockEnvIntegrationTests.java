package com.github;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MockEnvIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExchangeRateClient exchangeRateClient;

    @Test
    void createOrder() throws Exception {
        // TODO: протестируйте успешное создание заказа на 100 евро
        String currencyCode = "EUR";
        CurrencyUnit euro = Monetary
                .getCurrency(currencyCode);
        MonetaryAmount monetaryAmount = Monetary
                .getDefaultAmountFactory()
                .setCurrency(euro)
                .setNumber(100)
                .create();
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setAmount(monetaryAmount);
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("CustomOrderRequestSerializer", new Version(1, 0, 0, null, null, null));
        module.addSerializer(OrderRequest.class, new CustomOrderRequestSerializer());
        mapper.registerModule(module);
        String orderRequestJson = mapper.writeValueAsString(orderRequest);
        mockMvc.perform( MockMvcRequestBuilders
                        .post("/order")
                        .content(orderRequestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(100));
    }




    @Test
    @Sql("/unpaid-order.sql")
    void payOrder() throws Exception {
        // TODO: протестируйте успешную оплату ранее созданного заказа валидной картой\
        ObjectMapper objectMapper = new ObjectMapper();
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setCreditCardNumber("4530116592655166");
        String orderRequestAsString = objectMapper.writeValueAsString(paymentRequest);
        mockMvc.perform( MockMvcRequestBuilders
                        .post("/order/1/payment")
                        .content(orderRequestAsString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.creditCardNumber").value("4530116592655166"));
    }

    @Test
    @Sql("/paid-order.sql")
    void getReceipt() {
        // TODO: Протестируйте получение чека на заказ №1 c currency = USD
        // Примечание: используйте мок для ExchangeRateClient
        String currencyCodeEur = "EUR";
        CurrencyUnit euro = Monetary
                .getCurrency(currencyCodeEur);
        String currencyCodeUsd = "USD";
        CurrencyUnit usd = Monetary
                .getCurrency(currencyCodeUsd);
        BigDecimal rate = exchangeRateClient.getExchangeRate(euro, usd);
        log.info(rate + " - rate");
        MonetaryAmount convertedAmount = Money.of(new BigDecimal(100).multiply(rate), usd);
        log.info(convertedAmount + " - convertedAmount");
        assertThat(convertedAmount).isNotNull();
    }
}
