package com.GreenThumb.api.user.domain.repository;

public interface UserRepository {

    String getUsername(Long id_user);
    long count();
}
