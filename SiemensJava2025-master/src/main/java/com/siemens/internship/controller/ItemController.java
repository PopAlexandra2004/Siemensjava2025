/* ItemController.java  (controller) */
package com.siemens.internship.controller;

import com.siemens.internship.model.Item;
import com.siemens.internship.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService svc;

    @GetMapping
    public ResponseEntity<List<Item>> getAll() { return ResponseEntity.ok(svc.findAll()); }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Item item, BindingResult br) {
        if (br.hasErrors()) return ResponseEntity.badRequest().body(br.getFieldErrors());
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.save(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody Item item, BindingResult br) {
        if (br.hasErrors()) return ResponseEntity.badRequest().body(br.getFieldErrors());
        if (!svc.existsById(id)) return ResponseEntity.notFound().build();
        item.setId(id);
        return ResponseEntity.ok(svc.save(item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!svc.existsById(id)) return ResponseEntity.notFound().build();
        svc.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/process")
    public CompletableFuture<ResponseEntity<List<Item>>> process() {
        return svc.processItemsAsync().thenApply(ResponseEntity::ok);
    }
}
