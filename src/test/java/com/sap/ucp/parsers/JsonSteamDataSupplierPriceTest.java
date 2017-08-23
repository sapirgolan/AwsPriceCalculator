package com.sap.ucp.parsers;

import com.sap.ucp.model.Price;
import com.sap.ucp.model.Product;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by i062070 on 16/08/2017.
 */
public class JsonSteamDataSupplierPriceTest {

    @Test
    public void jsonHasNoProducts_noNext() throws Exception {
        String jsonWithNoProducts = "{\"formatVersion\":\"v1.0\",\"disclaimer\":\"This pricing list is for informational purposes only. All prices are subject to the additional terms included in the pricing pages on http://aws.amazon.com. All Free Tier prices are also subject to the terms included at https://aws.amazon.com/free/\",\"offerCode\":\"AmazonEC2\",\"version\":\"20170721022911\",\"publicationDate\":\"2017-07-21T02:29:11Z\"}";
        ByteArrayInputStream stream = new ByteArrayInputStream(jsonWithNoProducts.getBytes());
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(stream, new PriceStrategy());
        assertThat(steamDataSupplier.hasNext(), is(false));
    }

    @Test
    public void parseJsonWithSinglePrice() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("{\"terms\":{\"OnDemand\":{\"MMWYT6C5AE7DJWT7\":{\"MMWYT6C5AE7DJWT7.JRTCKXETXF\":{\"offerTermCode\":\"JRTCKXETXF\",\"sku\":\"MMWYT6C5AE7DJWT7\",\"effectiveDate\":\"2017-07-01T00:00:00Z\",\"priceDimensions\":{\"MMWYT6C5AE7DJWT7.JRTCKXETXF.6YS6EN2CT7\":{\"rateCode\":\"MMWYT6C5AE7DJWT7.JRTCKXETXF.6YS6EN2CT7\",\"description\":\"$1.158 per On Demand SUSE c3.4xlarge Instance Hour\",\"beginRange\":\"0\",\"endRange\":\"Inf\",\"unit\":\"Hrs\",\"pricePerUnit\":{\"USD\":\"1.1580000000\"},\"appliesTo\":[]}},\"termAttributes\":{}}}}}}".getBytes());
        JsonSteamDataSupplier<Price> steamDataSupplier = new JsonSteamDataSupplier<>(stream, new PriceStrategy());
        steamDataSupplier.hasNext();
        Price next = steamDataSupplier.next();
        assertThat(next.getSku(), is("MMWYT6C5AE7DJWT7"));
        assertThat(next.getDescription(), is("$1.158 per On Demand SUSE c3.4xlarge Instance Hour"));
        assertThat(next.getUnit(), is("Hrs"));
        assertThat(next.getPrice(), closeTo(1.1580000000, 0.00001));
    }

    @Test
    public void priceDimensionsNotExists_returnDefault() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("{\"terms\":{\"OnDemand\":{\"MMWYT6C5AE7DJWT7\":{\"MMWYT6C5AE7DJWT7.JRTCKXETXF\":{\"offerTermCode\":\"JRTCKXETXF\",\"sku\":\"MMWYT6C5AE7DJWT7\",\"effectiveDate\":\"2017-07-01T00:00:00Z\"},\"termAttributes\":{}}}}}".getBytes());
        JsonSteamDataSupplier<Price> steamDataSupplier = new JsonSteamDataSupplier<>(stream, new PriceStrategy());
        steamDataSupplier.hasNext();
        Price next = steamDataSupplier.next();
        assertThat(next.getSku(), is("MMWYT6C5AE7DJWT7"));
        assertThat(next.getDescription(), is(StringUtils.EMPTY));
        assertThat(next.getUnit(), is(StringUtils.EMPTY));
        assertThat(next.getPrice(), closeTo(0.0, 0.00001));
    }

    @Test
    public void priceDimensionsMissingPrice_returnDefault() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("{\"terms\":{\"OnDemand\":{\"MMWYT6C5AE7DJWT7\":{\"MMWYT6C5AE7DJWT7.JRTCKXETXF\":{\"offerTermCode\":\"JRTCKXETXF\",\"sku\":\"MMWYT6C5AE7DJWT7\",\"effectiveDate\":\"2017-07-01T00:00:00Z\",\"priceDimensions\":{\"MMWYT6C5AE7DJWT7.JRTCKXETXF.6YS6EN2CT7\":{\"rateCode\":\"MMWYT6C5AE7DJWT7.JRTCKXETXF.6YS6EN2CT7\",\"description\":\"$1.158 per On Demand SUSE c3.4xlarge Instance Hour\",\"beginRange\":\"0\",\"endRange\":\"Inf\",\"unit\":\"Hrs\",\"pricePerUnit\":{},\"appliesTo\":[]}},\"termAttributes\":{}}}}}}".getBytes());
        JsonSteamDataSupplier<Price> steamDataSupplier = new JsonSteamDataSupplier<>(stream, new PriceStrategy());
        steamDataSupplier.hasNext();
        Price next = steamDataSupplier.next();
        assertThat(next.getPrice(), closeTo(0.0, 0.00001));
    }

    @Test
    public void allPricesAreParsed_small() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("{\"terms\":{\"OnDemand\":{\"Y24656PFTEN95P6T\":{\"Y24656PFTEN95P6T.JRTCKXETXF\":{\"offerTermCode\":\"JRTCKXETXF\",\"sku\":\"Y24656PFTEN95P6T\",\"effectiveDate\":\"2017-07-01T00:00:00Z\",\"priceDimensions\":{\"Y24656PFTEN95P6T.JRTCKXETXF.6YS6EN2CT7\":{\"rateCode\":\"Y24656PFTEN95P6T.JRTCKXETXF.6YS6EN2CT7\",\"description\":\"$0.000 per Windows with SQL Std r4.2xlarge Dedicated Host Instance hour\",\"beginRange\":\"0\",\"endRange\":\"Inf\",\"unit\":\"Hrs\",\"pricePerUnit\":{\"USD\":\"0.0000000000\"},\"appliesTo\":[]}},\"termAttributes\":{}}},\"MMWYT6C5AE7DJWT7\":{\"MMWYT6C5AE7DJWT7.JRTCKXETXF\":{\"offerTermCode\":\"JRTCKXETXF\",\"sku\":\"MMWYT6C5AE7DJWT7\",\"effectiveDate\":\"2017-07-01T00:00:00Z\",\"priceDimensions\":{\"MMWYT6C5AE7DJWT7.JRTCKXETXF.6YS6EN2CT7\":{\"rateCode\":\"MMWYT6C5AE7DJWT7.JRTCKXETXF.6YS6EN2CT7\",\"description\":\"$1.158 per On Demand SUSE c3.4xlarge Instance Hour\",\"beginRange\":\"0\",\"endRange\":\"Inf\",\"unit\":\"Hrs\",\"pricePerUnit\":{},\"appliesTo\":[]}},\"termAttributes\":{}}}}}}".getBytes());
        JsonSteamDataSupplier<Price> steamDataSupplier = new JsonSteamDataSupplier<>(stream, new PriceStrategy());
        List<Price> list = steamDataSupplier.getStream().collect(Collectors.toList());
        assertThat(list, hasSize(2));
        list.stream().forEach(p -> assertThat(p.getSku(), notNullValue()));
    }

    @Test
    public void allPricesAreParsed_fromActualData() throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("terms_ec2.minify.json");
        JsonSteamDataSupplier<Price> steamDataSupplier = new JsonSteamDataSupplier<>(stream, new PriceStrategy());

        assertThat(steamDataSupplier.getStream().count(), is(19007L));
    }
}