package uk.startup.grpc.test.client.services;

import uk.startup.grpc.test.client.models.ResourceData;

import java.util.List;

/**
 * Console writer - prints data / error to console
 */
public class ConsoleWriter implements Writer {

    private int maxPrintSize;

    public ConsoleWriter(int maxPrintSize) {
        this.maxPrintSize = maxPrintSize;
    }

    @Override
    public void writeData(ResourceData data) {
        System.out.println();
        System.out.println("Headers:");
        data.getHeaders().forEach((key ,value) -> {
            System.out.println(" - " + key + ": " + value);
        });
        System.out.println("Body:");

        String body = data.getBody();
        if (body.length() > maxPrintSize) {
            System.out.println(body.substring(0, maxPrintSize));
        } else {
            System.out.println(body);
        }
    }

    @Override
    public void writeErrors(List<String> errors) {
        System.out.println("Operation is failed: ");

        for (String error : errors) {
            System.out.println(" - " + error);
        }
    }
}
