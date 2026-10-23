package com.encyclopedia.auth;

public interface AuthService {
    String authenticate(String username, String password);
    User register(String username, String email, String password);
}
