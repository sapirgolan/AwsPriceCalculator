package com.sap.ucp.accessors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sap.ucp.config.PropertiesResolver;
import com.sap.ucp.model.CurrencyRate;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.Strict.class)
public class CurrencyExchangeAccessorTest {

  @Mock
  private RestTemplate restTemplate;
  @Mock
  private PropertiesResolver propertiesResolver;
  private CurrencyExchangeAccessor accessor;

  @Before
  public void setUp() {
    when(propertiesResolver.getProperty(CurrencyExchangeAccessor.FIXER_ACCESS_KEY))
        .thenReturn("1234");
    accessor = new CurrencyExchangeAccessor(restTemplate, propertiesResolver);
    injectProperties();
  }

  @Test
  public void getEuroCurrency_isCalledWithUrlFromProperties()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    when(restTemplate.getForObject(anyString(), eq(CurrencyRate.class)))
        .thenReturn(new CurrencyRate("EUR", LocalDate.now().toString(), 0.89));

    Method postConstruct =  CurrencyExchangeAccessor.class.getDeclaredMethod("init"); // methodName,parameters
    postConstruct.setAccessible(true);
    postConstruct.invoke(accessor);

    CurrencyRate euroCurrency = accessor.getEuroCurrency();
    assertThat(euroCurrency.getBase())
        .isEqualTo("EUR");
    assertThat(euroCurrency.getRates())
        .isEqualTo(0.89);

    verify(restTemplate).getForObject("http://www.fixTest.data/api/latest?access_key=1234&symbols=USD", CurrencyRate.class);
  }

  private void injectProperties() {
    Field baseUrl = ReflectionUtils.findField(CurrencyExchangeAccessor.class, "baseUrl");
    baseUrl.setAccessible(true);
    ReflectionUtils.setField(baseUrl, accessor, "www.fixTest.data/api");
  }
}