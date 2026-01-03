package com.GreenThumb.api.user.infrastructure.repository;

import com.GreenThumb.api.user.domain.entity.Tier;
import com.GreenThumb.api.user.domain.exception.NoFoundException;
import com.GreenThumb.api.user.domain.repository.ThreadLimitTierRepository;
import com.GreenThumb.api.user.infrastructure.entity.ThreadLimitTierEntity;
import com.GreenThumb.api.user.infrastructure.mapper.TierMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JpaThreadLimitTierRepoistory implements ThreadLimitTierRepository {

    private final SpringDataThreadLimitTierRepository threadLimitTierRepository;

    public JpaThreadLimitTierRepoistory(SpringDataThreadLimitTierRepository threadLimitTierRepository) {
        this.threadLimitTierRepository = threadLimitTierRepository;
    }

    public Tier findNextTier(String name) {
        ThreadLimitTierEntity current = threadLimitTierRepository.findByTierName(name)
                .orElseThrow(() -> new NoFoundException("Aucun tier trouvé"));

        ThreadLimitTierEntity next = threadLimitTierRepository.findNextTierByCurrentId(current.getIdTier())
                .orElseThrow(() -> new NoFoundException("Aucun tier trouvé"));
        return TierMapper.toDomain(next);
    }
}
