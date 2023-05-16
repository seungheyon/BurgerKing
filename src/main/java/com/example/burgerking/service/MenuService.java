package com.example.burgerking.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.burgerking.dto.MenuListResponseDto;
import com.example.burgerking.dto.MenuRequestDto;
import com.example.burgerking.dto.MenuResponseDto;
import com.example.burgerking.dto.ResponseDto;
import com.example.burgerking.entity.Menu;
import com.example.burgerking.entity.User;
import com.example.burgerking.entity.UserRoleEnum;
import com.example.burgerking.exception.NoAuthorityException;
import com.example.burgerking.repository.MenuRepository;
import com.example.burgerking.vo.MenuVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private static final String S3_BUCKET_PREFIX = "S3";

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final AmazonS3 amazonS3;

    // 메뉴 저장
    public ResponseDto<MenuVo> createMenu(MenuRequestDto menuRequestDto, MultipartFile image, User user) throws IOException {

        if (user.getRole() != UserRoleEnum.ADMIN) {
            throw new NoAuthorityException("권한이 없습니다.");
        }
        Menu menu = new Menu();

        // 파일명 새로 부여를 위한 현재 시간 알아내기
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        int millis = now.get(ChronoField.MILLI_OF_SECOND);

        String imageUrl = null;

        // 새로 부여한 이미지명
        String newFileName = "image" + hour + minute + second + millis;
        String fileExtension = '.' + image.getOriginalFilename().replaceAll("^.*\\.(.*)$", "$1");
        String imageName = S3_BUCKET_PREFIX + newFileName + fileExtension;

        // 메타데이터 설정
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(image.getContentType());
        objectMetadata.setContentLength(image.getSize());

        InputStream inputStream = image.getInputStream();

        amazonS3.putObject(new PutObjectRequest(bucketName, imageName, inputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        imageUrl = amazonS3.getUrl(bucketName, imageName).toString();
        menu = menu.builder()
                .menuRequestDto(menuRequestDto)
                .imageUrl(imageUrl)
                .build();

        menu = menuRepository.save(menu);
        return new ResponseDto<>("성공", HttpStatus.OK.value());
    }

    //메뉴 수정
    public ResponseDto<MenuVo> updateMenu(Long menuId, MenuRequestDto menuRequestDto, MultipartFile image, User user) throws IOException {

        if (user.getRole() != UserRoleEnum.ADMIN) {
            throw new NoAuthorityException("권한이 없습니다.");
        }
        Menu menu = menuRepository.findById(menuId).orElseThrow(
                () -> new IllegalArgumentException("메뉴가 없습니다.")
        );

        // image 가 있는데 title or contents 없는 경우
        List<MenuRequestDto> list = new ArrayList<>();
        list.add(menuRequestDto);

        if (image == null) {        // image 가 없는 경우
            for (MenuRequestDto requestDto : list) {
                if (requestDto.getMenuName() != null) menu.setMenuName(requestDto.getMenuName()); // image && title 있는 경우
                if (requestDto.getCategory() != null) menu.setCategory(requestDto.getCategory()); // image && contents 있는 경우
            }
        }

        if (image != null) {
            // 메뉴 저장
            // 파일명 새로 부여를 위한 현재 시간 알아내기
            LocalDateTime now = LocalDateTime.now();
            int hour = now.getHour();
            int minute = now.getMinute();
            int second = now.getSecond();
            int millis = now.get(ChronoField.MILLI_OF_SECOND);

            String imageUrl;

            // 새로 부여한 이미지명
            String newFileName = "image" + hour + minute + second + millis;
            String fileExtension = '.' + image.getOriginalFilename().replaceAll("^.*\\.(.*)$", "$1");
            String imageName = S3_BUCKET_PREFIX + newFileName + fileExtension;

            // 메타데이터 설정
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(image.getContentType());
            objectMetadata.setContentLength(image.getSize());

            InputStream inputStream = image.getInputStream();

            amazonS3.putObject(new PutObjectRequest(bucketName, imageName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            imageUrl = amazonS3.getUrl(bucketName, imageName).toString();

            if (menuRequestDto.getMenuName() == null) {
                menuRequestDto.setMenuName(menu.getMenuName());
            }
            if (menuRequestDto.getCategory() == null) {
                menuRequestDto.setCategory(menu.getCategory());
            }

            menu.updateMenu(menuRequestDto, imageUrl);
        }

        return new ResponseDto<>("성공", HttpStatus.OK.value());
    }

    //메뉴 삭제
    public ResponseDto<MenuVo> deleteMenu(Long menuId, User user) {

        if (user.getRole() != UserRoleEnum.ADMIN) {
            throw new NoAuthorityException("권한이 없습니다.");
        }
        Menu menu = menuRepository.findById(menuId).orElseThrow(
                () -> new IllegalArgumentException("메뉴가 없습니다.")
        );

        menuRepository.deleteById(menuId);
        return new ResponseDto<>("성공", HttpStatus.OK.value());
    }

    //메뉴 전체조회
    public MenuListResponseDto<MenuVo> getMenu() {
        List<MenuVo> menuVoList = new ArrayList<>();
        List<Menu> menuList = menuRepository.findAllByOrderByCreatedDateDesc();
        for (Menu menu : menuList) {
            menuVoList.add(new MenuVo(menu.getId(), menu.getMenuName(), menu.getCategory(), menu.getImageUrl()));
            //responseDto.add(new MenuResponseDto(menu));
        }
        return new MenuListResponseDto<>("성공", HttpStatus.OK.value(), menuVoList);
    }

    //메뉴 카테고리별 조회
    public MenuListResponseDto<MenuVo> getMenus(String category) {
        //menuRepository에서 findbycategory
        //MenuResponseDto 리스트에 붙여주기
        List<MenuVo> menuVoList = new ArrayList<>();
        //List<MenuResponseDto> responseDto = new ArrayList<>();
        List<Menu> menuList = menuRepository.findByCategoryOrderByCreatedDateDesc(category);
        for (Menu menu : menuList) {
            menuVoList.add(new MenuVo(menu.getId(), menu.getMenuName(), menu.getCategory(), menu.getImageUrl()));
            //responseDto.add(new MenuResponseDto(menu));
        }
        return new MenuListResponseDto<>("성공", HttpStatus.OK.value(), menuVoList);
    }

    //메뉴 상세조회
    @Transactional(readOnly = true)
    public ResponseDto<MenuVo> getDetail(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(
                () -> new IllegalArgumentException("메뉴가 없습니다.")
        );
        MenuVo menuVo = new MenuVo(menu.getId(), menu.getMenuName(), menu.getCategory(), menu.getImageUrl());
        return new ResponseDto<>("성공",HttpStatus.OK.value(),menuVo);
    }
}