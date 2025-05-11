package com.siemens.internship.service;

import com.siemens.internship.model.Item;
import com.siemens.internship.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository repo;

    @Qualifier("applicationTaskExecutor")
    private final Executor taskExecutor;

    public List<Item> findAll()           { return repo.findAll(); }
    public boolean    existsById(Long id) { return repo.existsById(id); }
    public Item       save(Item i)        { return repo.save(i); }
    public void       deleteById(Long id) { repo.deleteById(id); }

    @Async
    @Transactional
    public CompletableFuture<List<Item>> processItemsAsync() {

        List<CompletableFuture<Item>> futures = repo.findAllIds().stream()
                .map(id -> CompletableFuture.supplyAsync(() -> {
                    Item it = repo.findById(id)
                            .orElseThrow(() -> new IllegalStateException("No item " + id));
                    it.setStatus("PROCESSED");
                    return repo.save(it);
                }, taskExecutor))
                .toList();

        return CompletableFuture
                .allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }
}
