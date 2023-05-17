package com.example.burgerking.controller;

import com.example.burgerking.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/api/store")
    public String store(@RequestParam String city,
                        @RequestParam String district) {
        return storeService.store(city, district);

    }
}