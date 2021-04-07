package com.envisioncn.gssc.libra.security;

import lombok.Data;

/**
 * @author jonnas
 * @date 2021-04-07
 */
@Data
public class User {
    private String username;
    private String password;
    private String[] roles;
}

