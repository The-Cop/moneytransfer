package ru.thecop.revotest.repository;

public interface TxExecutable<T> {
    T execute();
}
