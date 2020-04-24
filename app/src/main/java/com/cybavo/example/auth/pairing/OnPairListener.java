package com.cybavo.example.auth.pairing;

import com.cybavo.auth.service.auth.Pairing;

public interface OnPairListener {
    void onPairSuccess(Pairing pairing);
    void onPairError(Throwable error);
}
