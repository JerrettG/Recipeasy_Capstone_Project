package com.kenzie.appserver.lambda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenzie.appserver.IntegrationTest;
import com.kenzie.appserver.util.QueryUtility;
import com.kenzie.capstone.service.model.profile.service.Profile;
import com.kenzie.capstone.service.model.recipe.proxy.Diet;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
public class GetProfileTest {

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
    public void getProfile_profileExists_returnsProfile() throws Exception {
        String userId = mockNeat.users().valStr();
        String name = mockNeat.names().valStr();
        String email = mockNeat.emails().valStr();
        List<Diet> diets = Arrays.asList(Diet.GLUTEN_FREE);

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

        queryUtility.profileServiceClient.getProfile(userId)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("userId").value(userId),
                        jsonPath("name").value(name),
                        jsonPath("email").value(email),
                        jsonPath("createdDate").exists(),
                        jsonPath("lastUpdated").exists(),
                        jsonPath("diets").exists(),
                        jsonPath("diets.length()").value(diets.size()),
                        jsonPath("diets[0]").value(diets.get(0).toString())
                );
    }

    @Test
    public void getProfile_profileDoesNotExist_notFound() throws Exception {
        String userId = mockNeat.users().valStr();

        queryUtility.profileServiceClient.getProfile(userId)
                .andExpect(status().isNotFound());
    }
}