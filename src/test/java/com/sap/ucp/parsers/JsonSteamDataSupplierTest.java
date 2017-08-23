package com.sap.ucp.parsers;

import com.sap.ucp.model.Product;
import com.sap.ucp.parsers.strategy.ProductStrategy;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;

import java.io.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(null, new ProductStrategy());
        assertThat(steamDataSupplier.hasNext(), is(false));
    }

    @Test
    public void invalidFileHasNotNext() throws Exception {
        File testingFile = temporaryFolder.newFile("invalid.json");
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(new FileInputStream(testingFile), new ProductStrategy());
        assertThat(steamDataSupplier.hasNext(), is(false));
    }

    @Test
    public void validInputHasNext() throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("partialProducts.json");
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(stream, new ProductStrategy());
        assertThat(steamDataSupplier.hasNext(), is(true));
    }

    @Test
    public void jsonIsInvalid_noNext() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("Products: {}".getBytes());
        JsonSteamDataSupplier<Product> steamDataSupplier = new JsonSteamDataSupplier<>(stream, new ProductStrategy());
        assertThat(steamDataSupplier.hasNext(), is(false));
    }
}