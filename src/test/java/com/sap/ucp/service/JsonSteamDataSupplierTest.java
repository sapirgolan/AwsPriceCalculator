package com.sap.ucp.service;

import com.sap.ucp.model.Product;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

import java.io.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

/**
 * Created by i062070 on 13/08/2017.
 */
public class JsonSteamDataSupplierTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public Timeout globalTimeOut = Timeout.seconds(2);

    @Test
    public void onNullStream_noNext() throws IOException {
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(null, Product.class);
        assertThat(steamDataSupplier.hasNext(), is(false));
    }

    @Test
    public void invalidFileHasNotNext() throws Exception {
        File testingFile = temporaryFolder.newFile("invalid.json");
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(new FileInputStream(testingFile), Product.class);
        assertThat(steamDataSupplier.hasNext(), is(false));
    }

    @Test
    public void validInputHasNext() throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("partialProducts.json");
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(stream, Product.class);
        assertThat(steamDataSupplier.hasNext(), is(true));
    }

    @Test
    public void jsonIsInvalid_noNext() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("Products: {}".getBytes());
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(stream, Product.class);
        assertThat(steamDataSupplier.hasNext(), is(false));
    }

    @Test
    public void jsonHasNoProducts_noNext() throws Exception {
        String jsonWithNoProducts = "{\"formatVersion\":\"v1.0\",\"disclaimer\":\"This pricing list is for informational purposes only. All prices are subject to the additional terms included in the pricing pages on http://aws.amazon.com. All Free Tier prices are also subject to the terms included at https://aws.amazon.com/free/\",\"offerCode\":\"AmazonEC2\",\"version\":\"20170721022911\",\"publicationDate\":\"2017-07-21T02:29:11Z\"}";
        ByteArrayInputStream stream = new ByteArrayInputStream(jsonWithNoProducts.getBytes());
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(stream, Product.class);
        assertThat(steamDataSupplier.hasNext(), is(false));
    }

    @Test
    public void parseJsonWithSingleProduct() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("{\"products\":{\"76V3SF2FJC3ZR3GH\":{\"attributes\":{\"clockSpeed\":\"2.4 GHz\",\"currentGeneration\":\"Yes\",\"ecu\":\"56\",\"enhancedNetworkingSupported\":\"Yes\",\"instanceFamily\":\"Storage optimized\",\"instanceType\":\"d2.4xlarge\",\"licenseModel\":\"License Included\",\"location\":\"Asia Pacific (Mumbai)\",\"locationType\":\"AWS Region\",\"memory\":\"122 GiB\",\"networkPerformance\":\"High\",\"operatingSystem\":\"Windows\",\"operation\":\"RunInstances:0002\",\"physicalProcessor\":\"Intel Xeon E5-2676v3 (Haswell)\",\"preInstalledSw\":\"NA\",\"processorArchitecture\":\"64-bit\",\"processorFeatures\":\"Intel AVX; Intel AVX2; Intel Turbo\",\"servicecode\":\"AmazonEC2\",\"storage\":\"12 x 2000 HDD\",\"tenancy\":\"Host\",\"usagetype\":\"APS3-HostBoxUsage:d2.4xlarge\",\"vcpu\":\"16\"},\"productFamily\":\"Compute Instance\",\"sku\":\"76V3SF2FJC3ZR3GH\"}}}".getBytes());
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(stream, Product.class);
        steamDataSupplier.hasNext();
        Product next = steamDataSupplier.next();
        assertThat(next.getSku(), is("76V3SF2FJC3ZR3GH"));
        assertThat(next.getProductFamily(), is("Compute Instance"));
        assertThat(next.getInstanceType(), is("d2.4xlarge"));
        assertThat(next.getPreInstalledSw(), is("NA"));
        assertThat(next.getLocation(), is("Asia Pacific (Mumbai)"));
    }
}