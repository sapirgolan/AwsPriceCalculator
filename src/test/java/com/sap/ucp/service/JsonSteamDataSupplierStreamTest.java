package com.sap.ucp.service;

import com.sap.ucp.model.Product;
import com.sap.ucp.parsers.ProductStrategy;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by i062070 on 16/08/2017.
 */
public class JsonSteamDataSupplierStreamTest {

    @Rule
    public Timeout globalTimeOut = Timeout.seconds(5);

    @Test
    public void givenTwoProducts_sizeIsTwo() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("{\"products\":{\"QPN2H3V39T9H38UC\":{\"sku\":\"QPN2H3V39T9H38UC\",\"productFamily\":\"Compute Instance\",\"attributes\":{\"servicecode\":\"AmazonEC2\",\"location\":\"Asia Pacific (Tokyo)\",\"locationType\":\"AWS Region\",\"instanceType\":\"m3.xlarge\",\"currentGeneration\":\"Yes\",\"instanceFamily\":\"General purpose\",\"vcpu\":\"4\",\"physicalProcessor\":\"Intel Xeon E5-2670 v2 (Ivy Bridge/Sandy Bridge)\",\"clockSpeed\":\"2.5 GHz\",\"memory\":\"15 GiB\",\"storage\":\"2 x 40 SSD\",\"networkPerformance\":\"High\",\"processorArchitecture\":\"64-bit\",\"tenancy\":\"Host\",\"operatingSystem\":\"Windows\",\"licenseModel\":\"License Included\",\"usagetype\":\"APN1-HostBoxUsage:m3.xlarge\",\"operation\":\"RunInstances:0202\",\"ecu\":\"13\",\"preInstalledSw\":\"SQL Web\",\"processorFeatures\":\"Intel AVX; Intel Turbo\"}},\"76V3SF2FJC3ZR3GH\":{\"attributes\":{\"clockSpeed\":\"2.4 GHz\",\"currentGeneration\":\"Yes\",\"ecu\":\"56\",\"enhancedNetworkingSupported\":\"Yes\",\"instanceFamily\":\"Storage optimized\",\"instanceType\":\"d2.4xlarge\",\"licenseModel\":\"License Included\",\"location\":\"Asia Pacific (Mumbai)\",\"locationType\":\"AWS Region\",\"memory\":\"122 GiB\",\"networkPerformance\":\"High\",\"operatingSystem\":\"Windows\",\"operation\":\"RunInstances:0002\",\"physicalProcessor\":\"Intel Xeon E5-2676v3 (Haswell)\",\"preInstalledSw\":\"NA\",\"processorArchitecture\":\"64-bit\",\"processorFeatures\":\"Intel AVX; Intel AVX2; Intel Turbo\",\"servicecode\":\"AmazonEC2\",\"storage\":\"12 x 2000 HDD\",\"tenancy\":\"Host\",\"usagetype\":\"APS3-HostBoxUsage:d2.4xlarge\",\"vcpu\":\"16\"},\"productFamily\":\"Compute Instance\",\"sku\":\"76V3SF2FJC3ZR3GH\"}}}".getBytes());
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(stream, new ProductStrategy());

        //verify there are two items
        assertThat(steamDataSupplier.getStream().count(), is(2L));
    }

    @Test
    public void givenTwoProducts_theyHaveDifferentSKUs() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("{\"products\":{\"QPN2H3V39T9H38UC\":{\"sku\":\"QPN2H3V39T9H38UC\",\"productFamily\":\"Compute Instance\",\"attributes\":{\"servicecode\":\"AmazonEC2\",\"location\":\"Asia Pacific (Tokyo)\",\"locationType\":\"AWS Region\",\"instanceType\":\"m3.xlarge\",\"currentGeneration\":\"Yes\",\"instanceFamily\":\"General purpose\",\"vcpu\":\"4\",\"physicalProcessor\":\"Intel Xeon E5-2670 v2 (Ivy Bridge/Sandy Bridge)\",\"clockSpeed\":\"2.5 GHz\",\"memory\":\"15 GiB\",\"storage\":\"2 x 40 SSD\",\"networkPerformance\":\"High\",\"processorArchitecture\":\"64-bit\",\"tenancy\":\"Host\",\"operatingSystem\":\"Windows\",\"licenseModel\":\"License Included\",\"usagetype\":\"APN1-HostBoxUsage:m3.xlarge\",\"operation\":\"RunInstances:0202\",\"ecu\":\"13\",\"preInstalledSw\":\"SQL Web\",\"processorFeatures\":\"Intel AVX; Intel Turbo\"}},\"76V3SF2FJC3ZR3GH\":{\"attributes\":{\"clockSpeed\":\"2.4 GHz\",\"currentGeneration\":\"Yes\",\"ecu\":\"56\",\"enhancedNetworkingSupported\":\"Yes\",\"instanceFamily\":\"Storage optimized\",\"instanceType\":\"d2.4xlarge\",\"licenseModel\":\"License Included\",\"location\":\"Asia Pacific (Mumbai)\",\"locationType\":\"AWS Region\",\"memory\":\"122 GiB\",\"networkPerformance\":\"High\",\"operatingSystem\":\"Windows\",\"operation\":\"RunInstances:0002\",\"physicalProcessor\":\"Intel Xeon E5-2676v3 (Haswell)\",\"preInstalledSw\":\"NA\",\"processorArchitecture\":\"64-bit\",\"processorFeatures\":\"Intel AVX; Intel AVX2; Intel Turbo\",\"servicecode\":\"AmazonEC2\",\"storage\":\"12 x 2000 HDD\",\"tenancy\":\"Host\",\"usagetype\":\"APS3-HostBoxUsage:d2.4xlarge\",\"vcpu\":\"16\"},\"productFamily\":\"Compute Instance\",\"sku\":\"76V3SF2FJC3ZR3GH\"}}}".getBytes());
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(stream, new ProductStrategy());

        //verify there are different SKUs
        List<Product> products = steamDataSupplier.getStream().collect(Collectors.toList());
        //assert all SKU exists
        products.stream().map(Product::getSku).forEach(sku -> assertThat(sku, isOneOf("76V3SF2FJC3ZR3GH", "QPN2H3V39T9H38UC")));
    }

    @Test
    public void validateSmallSizeJsonNotCrash() throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("partialProducts.json");
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(stream, new ProductStrategy());

        assertThat(steamDataSupplier.getParallelStream().count(), is(1985L));
    }

    @Test
    public void validateXLSizeJsonNotCrash_parallel() throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("index.json");
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(stream, new ProductStrategy());

        assertThat(steamDataSupplier.getParallelStream().count(), greaterThan(15000L));
    }

    @Test
    public void validateXLSizeJsonNotCrash_sequential() throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("index.json");
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(stream, new ProductStrategy());

        assertThat(steamDataSupplier.getStream().count(), greaterThan(15000L));
    }
}