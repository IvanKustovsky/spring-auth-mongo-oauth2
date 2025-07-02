package com.ivan.healthtracker.dto;


public record ErrorResponseDto(
        String timestamp,
        int status,
        String error,
        String message,
        String path) {
}