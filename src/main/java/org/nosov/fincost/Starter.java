package org.nosov.fincost;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class Starter {
    public static Properties property = new Properties();

    public static void main(String[] args) {
        System.out.println("Print args: " + Arrays.toString(args));
        String path = args.length != 0 && args[0].equalsIgnoreCase("prod")
                ? "src/main/resources/prod.properties"
                : "src/main/resources/dev.properties";
        System.out.println("Print path " + path);
        try (FileInputStream fis = new FileInputStream(path)) {
            property.load(fis);
        } catch (IOException e) {
            System.err.println("Error: Config not found!");
        }
        new Bot().serve();
    }
}
