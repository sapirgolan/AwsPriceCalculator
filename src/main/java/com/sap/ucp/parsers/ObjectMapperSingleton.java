package com.sap.ucp.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

/**
 * Created by i062070 on 20/08/2017.
 */
public class ObjectMapperSingleton {
    private static ObjectMapper instance;
    private static final Object key = new Object();

    private ObjectMapperSingleton() {
    }

    public static ObjectMapper getInstance() {
        if (instance == null) {
            synchronized (key) {
                if (instance == null)
                    instance = invoke();
            }
        }
        return instance;
    }

    private static ObjectMapper invoke() {
        return new ObjectMapper().registerModule(new Jdk8Module());
    }
}
