package com.example.burgerking.controller;

import com.example.burgerking.dto.MenuResponseDto;
import com.example.burgerking.dto.MenuRequestDto;
import com.example.burgerking.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    //메뉴 추가
    @PostMapping("/api/menus")
    public MenuResponseDto createMenu(MenuRequestDto menuRequestDto,
                                      @RequestParam(value = "image", required = false) MultipartFile image,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
        return menuService.createMenu(menuRequestDto, image);
    }

    //메뉴 수정
    @PatchMapping("/api/menus/{menuId}")
    public MenuResponseDto updateMenu(@PathVariable Long menuId,
                                     MenuRequestDto menuRequestDto,
                                     @RequestParam(value = "image", required = false) MultipartFile image,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
        return menuService.updateMenu(menuId, menuRequestDto, image, userDetails.getUser());
    }

    //메뉴 삭제
    @DeleteMapping("/api/menus/{menuId}")
    public String deleteMenu(@PathVariable Long menuId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return menuService.deleteMenu(menuId, userDetails.getUser());
    }

    //메뉴 카테고리별 조회
    @GetMapping("/api/menus/{category}")
    public List<MenuResponseDto> getMenu(@PathVariable String category) {
        return menuService.getMenu(category);
    }

}
