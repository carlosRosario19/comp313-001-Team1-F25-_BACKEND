package com.centennial.gamepickd.services.impl;

import com.centennial.gamepickd.repository.contracts.PublisherDAO;
import com.centennial.gamepickd.services.contracts.PublisherService;
import com.centennial.gamepickd.util.enums.PublisherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PublisherServiceImpl implements PublisherService {

    private final PublisherDAO publisherDAO;

    @Autowired
    public PublisherServiceImpl(@Qualifier("publisherDAOJpaImpl") PublisherDAO publisherDAO) {
        this.publisherDAO = publisherDAO;
    }

    @Override
    @Cacheable(value = "publishersCache")
    public Set<String> getAll() {
        return publisherDAO.findAll().stream()
                .map(PublisherType::getVal)
                .collect(Collectors.toSet());
    }
}
