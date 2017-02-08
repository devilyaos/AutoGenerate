package com.yaos.autogenerate;

import java.io.IOException;

/**
 * @AUTHOR yaos
 * @DATE 2017-02-08
 */
public class Test {
    public static void main(String[] args) {
        try {
            AutoGen.use().init().create();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
