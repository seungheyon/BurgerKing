package com.example.burgerking.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.burgerking.dto.MenuRequestDto;
import com.example.burgerking.dto.MenuResponseDto;
import com.example.burgerking.entity.Menu;
import com.example.burgerking.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    public MenuResponseDto createMenu(MenuRequestDto menuRequestDto, MultipartFile image) throws IOException {
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
                .user(user)
                .build();

        menu = menuRepository.save(menu);
        return new MenuResponseDto(menu);
    }

    //메뉴 수정
    public MenuResponseDto updateMenu(Long menuId, MenuRequestDto menuRequestDto, MultipartFile image, User user) throws IOException {
        Menu menu = menuRepository.findById(menuId).orElseThrow(
                () -> new IllegalArgumentException()
        );
        if (user.getRole() != UserRoleEnum.ADMIN) {
            throw new IllegalArgumentException();
        }

        // image 가 있는데 title or contents 없는 경우
        List<MenuRequestDto> list = new ArrayList<>();
        list.add(menuRequestDto);

        if (image == null) {        // image 가 없는 경우
            for (MenuRequestDto requestDto : list) {
                if (requestDto.getMenuname() != null) menu.setMenuname(requestDto.getMenuname()); // image && title 있는 경우
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

            if (menuRequestDto.getMenuname() == null) {
                menuRequestDto.setMenuname(menu.getMenuname());
            }
            if (menuRequestDto.getCategory() == null) {
                menuRequestDto.setCategory(menu.getCategory());
            }

            menu = menu.builder()
                    .menuRequestDto(menuRequestDto)
                    .imageUrl(imageUrl)
                    .user(user)
                    .build();

            menu.updateMenu(menuRequestDto, imageUrl);
        }

        return new MenuResponseDto(menu);
    }

    //메뉴 삭제
    public String deleteMenu(Long menuId, User user) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(
                () -> new IllegalArgumentException()
        );
        if (user.getRole() != UserRoleEnum.ADMIN) {
            throw new IllegalArgumentException();
        }
        menuRepository.deleteById(menuId);
        return "삭제 완료";
    }

    public List<MenuResponseDto> getMenu(String category) {
        //menuRepository에서 findbycategory
        //MenuResponseDto 리스트에 붙여주기
        List<MenuResponseDto> responseDto = new ArrayList<>();
        List<Menu> menuList = menuRepository.findByCategoryOrderByCreatedDateDesc(category);
        for (Menu menu : menuList) {
            responseDto.add(new MenuResponseDto(menu));
        }
        return responseDto;
    }

}