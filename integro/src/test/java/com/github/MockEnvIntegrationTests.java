package com.github;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MockEnvIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
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
        mockMvc.perform( MockMvcRequestBuilders
                        .post("/order")

                        .content(asJsonString(orderRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").exists());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("/unpaid-order.sql")
    void payOrder() throws Exception {
        // TODO: протестируйте успешную оплату ранее созданного заказа валидной картой

    }

    @Test
    @Sql("/paid-order.sql")
    void getReceipt() throws Exception {
        // TODO: Протестируйте получение чека на заказ №1 c currency = USD
        // Примечание: используйте мок для ExchangeRateClient
    }
}
