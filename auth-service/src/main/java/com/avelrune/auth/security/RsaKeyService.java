package com.avelrune.auth.security;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface RsaKeyService {

    PrivateKey getPrivateKey();

    PublicKey getPublicKey();
}
