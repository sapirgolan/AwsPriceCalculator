package com.sap.ucp.parsers;

import com.sap.ucp.model.Product;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by i062070 on 13/08/2017.
 */
public class JsonSteamDataSupplierProductTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public Timeout globalTimeOut = Timeout.seconds(2);

    @Test
    public void jsonHasNoProducts_noNext() throws Exception {
        String jsonWithNoProducts = "{\"formatVersion\":\"v1.0\",\"disclaimer\":\"This pricing list is for informational purposes only. All prices are subject to the additional terms included in the pricing pages on http://aws.amazon.com. All Free Tier prices are also subject to the terms included at https://aws.amazon.com/free/\",\"offerCode\":\"AmazonEC2\",\"version\":\"20170721022911\",\"publicationDate\":\"2017-07-21T02:29:11Z\"}";
        ByteArrayInputStream stream = new ByteArrayInputStream(jsonWithNoProducts.getBytes());
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(stream, new ProductStrategy());
        assertThat(steamDataSupplier.hasNext(), is(false));
    }

    @Test
    public void parseJsonWithSingleProduct() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("{\"products\":{\"76V3SF2FJC3ZR3GH\":{\"attributes\":{\"clockSpeed\":\"2.4 GHz\",\"currentGeneration\":\"Yes\",\"ecu\":\"56\",\"enhancedNetworkingSupported\":\"Yes\",\"instanceFamily\":\"Storage optimized\",\"instanceType\":\"d2.4xlarge\",\"licenseModel\":\"License Included\",\"location\":\"Asia Pacific (Mumbai)\",\"locationType\":\"AWS Region\",\"memory\":\"122 GiB\",\"networkPerformance\":\"High\",\"operatingSystem\":\"Windows\",\"operation\":\"RunInstances:0002\",\"physicalProcessor\":\"Intel Xeon E5-2676v3 (Haswell)\",\"preInstalledSw\":\"NA\",\"processorArchitecture\":\"64-bit\",\"processorFeatures\":\"Intel AVX; Intel AVX2; Intel Turbo\",\"servicecode\":\"AmazonEC2\",\"storage\":\"12 x 2000 HDD\",\"tenancy\":\"Host\",\"usagetype\":\"APS3-HostBoxUsage:d2.4xlarge\",\"vcpu\":\"16\"},\"productFamily\":\"Compute Instance\",\"sku\":\"76V3SF2FJC3ZR3GH\"}}}".getBytes());
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(stream, new ProductStrategy());
        steamDataSupplier.hasNext();
        Product next = steamDataSupplier.next();
        assertThat(next.getSku(), is("76V3SF2FJC3ZR3GH"));
        assertThat(next.getProductFamily(), is("Compute Instance"));
        assertThat(next.getInstanceType(), is("d2.4xlarge"));
        assertThat(next.getPreInstalledSw(), is("NA"));
        assertThat(next.getLocation(), is("Asia Pacific (Mumbai)"));
    }

    @Test
    public void givenTwoProducts_bothAreReturned() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("{\"products\":{\"QPN2H3V39T9H38UC\":{\"sku\":\"QPN2H3V39T9H38UC\",\"productFamily\":\"Compute Instance\",\"attributes\":{\"servicecode\":\"AmazonEC2\",\"location\":\"Asia Pacific (Tokyo)\",\"locationType\":\"AWS Region\",\"instanceType\":\"m3.xlarge\",\"currentGeneration\":\"Yes\",\"instanceFamily\":\"General purpose\",\"vcpu\":\"4\",\"physicalProcessor\":\"Intel Xeon E5-2670 v2 (Ivy Bridge/Sandy Bridge)\",\"clockSpeed\":\"2.5 GHz\",\"memory\":\"15 GiB\",\"storage\":\"2 x 40 SSD\",\"networkPerformance\":\"High\",\"processorArchitecture\":\"64-bit\",\"tenancy\":\"Host\",\"operatingSystem\":\"Windows\",\"licenseModel\":\"License Included\",\"usagetype\":\"APN1-HostBoxUsage:m3.xlarge\",\"operation\":\"RunInstances:0202\",\"ecu\":\"13\",\"preInstalledSw\":\"SQL Web\",\"processorFeatures\":\"Intel AVX; Intel Turbo\"}},\"76V3SF2FJC3ZR3GH\":{\"attributes\":{\"clockSpeed\":\"2.4 GHz\",\"currentGeneration\":\"Yes\",\"ecu\":\"56\",\"enhancedNetworkingSupported\":\"Yes\",\"instanceFamily\":\"Storage optimized\",\"instanceType\":\"d2.4xlarge\",\"licenseModel\":\"License Included\",\"location\":\"Asia Pacific (Mumbai)\",\"locationType\":\"AWS Region\",\"memory\":\"122 GiB\",\"networkPerformance\":\"High\",\"operatingSystem\":\"Windows\",\"operation\":\"RunInstances:0002\",\"physicalProcessor\":\"Intel Xeon E5-2676v3 (Haswell)\",\"preInstalledSw\":\"NA\",\"processorArchitecture\":\"64-bit\",\"processorFeatures\":\"Intel AVX; Intel AVX2; Intel Turbo\",\"servicecode\":\"AmazonEC2\",\"storage\":\"12 x 2000 HDD\",\"tenancy\":\"Host\",\"usagetype\":\"APS3-HostBoxUsage:d2.4xlarge\",\"vcpu\":\"16\"},\"productFamily\":\"Compute Instance\",\"sku\":\"76V3SF2FJC3ZR3GH\"}}}".getBytes());
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(stream, new ProductStrategy());
        List<Product> products = new ArrayList<>();
        while (steamDataSupplier.hasNext())
            products.add(steamDataSupplier.next());

        //assertion
        assertThat(products, hasSize(2));
        Product product_1 = products.get(0);
        Product product_2 = products.get(1);
        assertThat(product_1.getSku(), isOneOf("76V3SF2FJC3ZR3GH", "QPN2H3V39T9H38UC"));
    }
}