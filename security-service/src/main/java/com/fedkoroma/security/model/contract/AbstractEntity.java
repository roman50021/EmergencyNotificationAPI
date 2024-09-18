package com.fedkoroma.security.model.contract;

import lombok.Getter;
import jakarta.persistence.*;

import java.util.UUID;

@Getter
@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;
}
