package com.avelrune.gateway.security;

import java.security.interfaces.RSAPublicKey;

public interface JwtPublicKeyService {

    RSAPublicKey getPublicKey();

    void reload();
}
