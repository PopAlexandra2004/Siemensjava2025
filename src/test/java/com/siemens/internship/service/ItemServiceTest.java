package com.siemens.internship.service;

import com.siemens.internship.model.Item;
import com.siemens.internship.repository.ItemRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @Mock  ItemRepository repo;
    @Mock  Executor        exec;
    @InjectMocks ItemService service;

    @BeforeEach void init() {
        MockitoAnnotations.openMocks(this);
        // use same thread for predictability
        exec = Runnable::run;
        service = new ItemService(repo, exec);
    }

    @Test
    void processesEveryItemOnce() {
        // given two IDs in DB
        Item a = new Item(1L,"A","",null,"a@x.com");
        Item b = new Item(2L,"B","",null,"b@x.com");
        when(repo.findAllIds()).thenReturn(List.of(1L,2L));
        when(repo.findById(1L)).thenReturn(Optional.of(a));
        when(repo.findById(2L)).thenReturn(Optional.of(b));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        // when
        List<Item> done = service.processItemsAsync().join();

        // then
        assertThat(done).containsExactlyInAnyOrder(a, b);
        assertThat(a.getStatus()).isEqualTo("PROCESSED");
        assertThat(b.getStatus()).isEqualTo("PROCESSED");
        verify(repo, times(2)).save(any(Item.class));
    }
}
