package com.kenzie.appserver.repositories;

import com.kenzie.appserver.repositories.model.SavedRecipeEntity;
import com.kenzie.appserver.repositories.model.SavedRecipeKey;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableScan
public interface SavedRecipeRepository extends CrudRepository<SavedRecipeEntity, SavedRecipeKey> {
    List<SavedRecipeEntity> findAllByUserId(String userId);
}
