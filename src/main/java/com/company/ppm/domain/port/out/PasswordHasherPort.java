package com.company.ppm.domain.port.out;

public interface PasswordHasherPort {
    boolean matches(String rawPassword, String encodedPassword);

    String encode(String rawPassword);
}
