package com.sap.ucp.utils;

import com.sap.ucp.model.OrderUcp;
import com.sap.ucp.model.Product;
import com.sap.ucp.types.OSType;
import org.junit.Test;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OSUtilTest {
    @Test
    public void whenPartOfInputIsNull_returnFalse() throws Exception {
        assertThat(OSUtil.equal(null, null), is(false));
        assertThat(OSUtil.equal(new Product(), null), is(false));
        assertThat(OSUtil.equal(null, new OrderUcp()), is(false));
    }

    @Test
    public void windowsEqualToAnyStringWithWindows() throws Exception {
        Product product = mock(Product.class);
        when(product.getOS()).thenReturn(OSType.Windows.toString());

        OrderUcp orderUcp = new OrderUcp(null, null, "Microsoft Windows Server");
        assertThat(OSUtil.equal(product, orderUcp), is(true));
    }

    @Test
    public void suseEqualToAnyStingContaingSuse() throws Exception {
        Product product = mock(Product.class);
        when(product.getOS()).thenReturn(OSType.SUSE.toString());

        Stream.of("suse", "SUSE", "suse-")
                .map(str -> new OrderUcp(null, null, str))
                .forEach(orderUcp -> assertThat(OSUtil.equal(product, orderUcp), is(true)));
    }

    @Test
    public void ubuntuEqualToAnyStingContaingSuse() throws Exception {
        Product product = mock(Product.class);
        when(product.getOS()).thenReturn(OSType.Linux.toString());

        Stream.of("Ubuntu", "Ubuntu server")
                .map(str -> new OrderUcp(null, null, str))
                .forEach(orderUcp -> assertThat(OSUtil.equal(product, orderUcp), is(true)));
    }

    @Test
    public void rHELEqualToAnyStingContaingSuse() throws Exception {
        Product product = mock(Product.class);
        when(product.getOS()).thenReturn(OSType.RHEL.toString());

        Stream.of("Red Hat Enterprise Linux")
                .map(str -> new OrderUcp(null, null, str))
                .forEach(orderUcp -> assertThat(OSUtil.equal(product, orderUcp), is(true)));
    }

    @Test
    public void productIsNotSupported_returnFalse() throws Exception {
        Product product = mock(Product.class);
        when(product.getOS()).thenReturn("UnsupportedNewOs");

        OrderUcp orderUcp = new OrderUcp(null, null, "Microsoft Windows Server");
        assertThat(OSUtil.equal(product, orderUcp), is(false));

    }
}