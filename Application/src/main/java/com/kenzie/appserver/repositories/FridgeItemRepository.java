package com.kenzie.appserver.repositories;

import com.kenzie.appserver.repositories.model.FridgeItemEntity;
import com.kenzie.appserver.repositories.model.FridgeItemKey;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableScan
public interface FridgeItemRepository extends CrudRepository<FridgeItemEntity, FridgeItemKey> {

    List<FridgeItemEntity> findAllByUserId(String userId);
}
