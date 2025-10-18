package com.centennial.gamepickd.repository.contracts;

import com.centennial.gamepickd.entities.Publisher;
import com.centennial.gamepickd.util.enums.PublisherType;

import java.util.Optional;

public interface PublisherDAO {
    Optional<Publisher> findByLabel(PublisherType label);
}
