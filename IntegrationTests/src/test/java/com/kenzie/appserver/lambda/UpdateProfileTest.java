package com.kenzie.appserver.lambda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenzie.appserver.IntegrationTest;
import com.kenzie.appserver.util.QueryUtility;

import com.kenzie.capstone.service.model.profile.service.Profile;
import com.kenzie.capstone.service.model.recipe.proxy.Diet;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@IntegrationTest
public class UpdateProfileTest {

    @Autowired
    private MockMvc mvc;
    private QueryUtility queryUtility;

    private final MockNeat mockNeat = MockNeat.threadLocal();

    private final ObjectMapper mapper = new ObjectMapper();
    @BeforeEach
    public void setup() {
        this.queryUtility = new QueryUtility(mvc);
    }

    @Test
    public void updateProfile_profileExists_updatesProfile() throws Exception {
        String userId = mockNeat.users().valStr();
        String name = mockNeat.names().valStr();
        String email = mockNeat.emails().valStr();
        List<Diet> diets =  Arrays.asList(Diet.GLUTEN_FREE);

        /* CREATE OBJECT */

        // Create a new profile with the generated user ID, name, email, and initial diets list
        queryUtility.profileServiceClient.createProfile(userId, name, email, diets)
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("userId").value(userId),
                        jsonPath("name").value(name),
                        jsonPath("email").value(email),
                        jsonPath("createdDate").exists(),
                        jsonPath("lastUpdated").exists(),
                        jsonPath("diets").exists(),
                        jsonPath("diets.length()").value(diets.size()),
                        jsonPath("diets[0]").value(diets.get(0).toString())
                );

        // Create updated list of diets
        List<Diet> updatedDiets =  Arrays.asList(Diet.GLUTEN_FREE,
                Diet.VEGAN);


        /* UPDATE OBJECT */

        // Update the profile with the new diets list
        queryUtility.profileServiceClient.updateProfile(userId, name, email, updatedDiets)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("userId").value(userId),
                        jsonPath("name").value(name),
                        jsonPath("email").value(email),
                        jsonPath("createdDate").exists(),
                        jsonPath("lastUpdated").exists(),
                        jsonPath("diets").exists(),
                        jsonPath("diets.length()").value(updatedDiets.size()),
                        jsonPath("diets[0]").value(updatedDiets.get(0).toString()),
                        jsonPath("diets[1]").value(updatedDiets.get(1).toString())
                );
    }

    @Test
    public void updateProfile_profileDoesNotExist_notFound() throws Exception {
        queryUtility.profileServiceClient.updateProfile("doesNotExistId",
                "doesNotExistName", "doesNotExist@email.com", Collections.emptyList())
                .andExpect(status().isNotFound());
    }

}