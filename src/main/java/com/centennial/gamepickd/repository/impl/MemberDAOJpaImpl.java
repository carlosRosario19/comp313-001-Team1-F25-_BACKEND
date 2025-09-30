package com.centennial.gamepickd.repository.impl;

import com.centennial.gamepickd.entities.Member;
import com.centennial.gamepickd.repository.contracts.MemberDAO;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MemberDAOJpaImpl implements MemberDAO {

    private final EntityManager entityManager;

    @Autowired
    public MemberDAOJpaImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void create(Member member) {
        entityManager.persist(member);
    }
}
