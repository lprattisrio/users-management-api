package com.users.management.demo.controller.dto;

import java.util.Map;

public record UserMetadataDto(
        Map<String, String> preferences
){}
