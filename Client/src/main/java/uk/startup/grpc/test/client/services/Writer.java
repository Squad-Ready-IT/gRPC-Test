package uk.startup.grpc.test.client.services;

import uk.startup.grpc.test.client.models.ResourceData;

import java.util.List;

/**
 * Prints data / errors
 */
public interface Writer {

    /**
     * Prints resource headers and body to console
     * @param data contains resource headers and body
     */
    void writeData(ResourceData data);

    /**
     * Prints errors to console
     * @param errors errors list
     */
    void writeErrors(List<String> errors);
}
