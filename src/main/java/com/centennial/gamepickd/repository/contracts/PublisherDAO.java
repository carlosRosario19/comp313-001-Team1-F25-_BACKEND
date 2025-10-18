package com.centennial.gamepickd.repository.contracts;

import com.centennial.gamepickd.entities.Publisher;
import com.centennial.gamepickd.util.enums.PublisherType;

import java.util.Optional;
import java.util.Set;

public interface PublisherDAO {
    Optional<Publisher> findByLabel(PublisherType label);
    Set<PublisherType> findAll();
}
