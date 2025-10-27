package com.GreenThumb.api.resources.domain.repository;

import com.GreenThumb.api.resources.domain.entity.Resource;

import java.util.List;

public interface ResourceRepository {
    List<Resource> getThreeResource();
}
