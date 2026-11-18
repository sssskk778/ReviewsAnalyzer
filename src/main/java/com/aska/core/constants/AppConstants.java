package com.aska.core.constants;

public final class AppConstants {
    public static final String JWT_SECRET_KEY = "Q7mZp2vR9xKd4TnB8wLc1YhF6sJe3UaG5oPiXr0V";
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final long JWT_EXPIRATION_MS = 86400000;

    public static final String QUEUE_NAME = "analyses-queue";
    public static final String EXCHANGE_NAME = "analyses-exchange";
    public static final String ROUTING_KEY = "analyses.routing.key";
}
