package ru.thecop.revotest.model;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractEntity {

    protected Long id;
}
