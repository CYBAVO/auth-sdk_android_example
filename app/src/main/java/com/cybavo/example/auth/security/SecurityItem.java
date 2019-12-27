package com.cybavo.example.auth.security;

class SecurityItem {
    enum State {
        FATAL,
        WARNING,
        PASS,
    }
    int id;
    State state;
    SecurityItem(int id, State state) {
        this.id = id;
        this.state = state;
    }
}
