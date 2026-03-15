package com.sapotest.flashsale.model.dto;

/**
 * A generic wrapper for all API responses to ensure a consistent structure.
 * @param <T> The type of the data payload.
 */
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    /**
     * Default constructor required for JSON serialization/deserialization.
     */
    public ApiResponse() {
    }

    /**
     * Constructor with all fields.
     * @param status  HTTP status code or custom business status code.
     * @param message Descriptive message about the response.
     * @param data    The actual data payload.
     */
    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}