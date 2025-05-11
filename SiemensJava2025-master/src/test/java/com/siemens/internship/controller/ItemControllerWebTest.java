package com.siemens.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.internship.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.siemens.internship.service.ItemService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerWebTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @MockBean ItemService svc;

    @Test
    void postBadEmailReturns400() throws Exception {
        Item bad = new Item(null,"X","",null,"not-an-email");
        mvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(bad)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bulkProcessEndpointReturns200() throws Exception {
        when(svc.processItemsAsync()).thenReturn(
                CompletableFuture.completedFuture(List.of())
        );
        mvc.perform(get("/api/items/process"))
                .andExpect(status().isOk());
    }
}
