package org.knowm.xchange.deribit.v2.dto.marketdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class DeribitOrderTest {

    @Test
    public void deserializeOrderTest() throws Exception {

        // given
        InputStream is =
                DeribitOrder.class.getResourceAsStream(
                        "/org/knowm/xchange/deribit/v2/dto/marketdata/example-order.json");

        // when
        ObjectMapper mapper = new ObjectMapper();
        DeribitOrder order = mapper.readValue(is, DeribitOrder.class);

        // then
        assertThat(order).isNotNull();

        assertThat(order.getQuantity()).isEqualTo(300);
        assertThat(order.getAmount()).isEqualTo(300);
        assertThat(order.getPrice()).isEqualTo(new BigDecimal("141.42"));
        assertThat(order.getCm()).isEqualTo(300);
        assertThat(order.getCmAmount()).isEqualTo(300);
    }
}