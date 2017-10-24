package ru.thecop.revotest.hibernate;

import com.google.inject.Singleton;

@Singleton
public class SomeBean {
    public SomeBean() {
    }

    public String say() {
        return "said!";
    }
}
