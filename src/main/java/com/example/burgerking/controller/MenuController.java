package com.example.burgerking.controller;

import com.example.burgerking.dto.MenuListResponseDto;
import com.example.burgerking.dto.MenuResponseDto;
import com.example.burgerking.dto.MenuRequestDto;
import com.example.burgerking.dto.ResponseDto;
import com.example.burgerking.exception.NoAuthorityException;
import com.example.burgerking.security.UserDetailsImpl;
import com.example.burgerking.service.MenuService;
import com.example.burgerking.vo.MenuVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    //메뉴 추가
    @PostMapping("/api/menus")
    public ResponseDto<MenuVo> createMenu(MenuRequestDto menuRequestDto,
                                          @RequestParam(value = "image", required = false) MultipartFile image,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
        try {
            return menuService.createMenu(menuRequestDto, image, userDetails.getUser());
        }
        catch (NoAuthorityException e){
            return new ResponseDto<>(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    //메뉴 수정
    @PatchMapping("/api/menus/{menuId}")
    public ResponseDto<MenuVo> updateMenu(@PathVariable Long menuId,
                                     MenuRequestDto menuRequestDto,
                                     @RequestParam(value = "image", required = false) MultipartFile image,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
        try {
            return menuService.updateMenu(menuId, menuRequestDto, image, userDetails.getUser());
        }
        catch (IllegalArgumentException e){ // 메뉴가 없는 경우
            return new ResponseDto<>(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
        catch (NoAuthorityException e){ //  Admin 권한이 없는 경우
            return new ResponseDto<>(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    //메뉴 삭제
    @DeleteMapping("/api/menus/{menuId}")
    public ResponseDto<MenuVo> deleteMenu(@PathVariable Long menuId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            return menuService.deleteMenu(menuId, userDetails.getUser());
        }
        catch (IllegalArgumentException e){ // 메뉴가 없는 경우
            return new ResponseDto<>(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
        catch (NoAuthorityException e){ //  Admin 권한이 없는 경우
            return new ResponseDto<>(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    //메뉴 카테고리별 조회
    @GetMapping("/api/menus/{category}")
    public MenuListResponseDto<MenuVo> getMenu(@PathVariable String category) {
        return menuService.getMenu(category);
    }

}
