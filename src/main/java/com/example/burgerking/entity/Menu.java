package com.example.burgerking.entity;

import com.example.burgerking.dto.MenuRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Menu extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String menuname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column
    private String imageUrl;

    @Builder
    public Menu(MenuRequestDto menuRequestDto, String imageUrl) {
        this.menuname = menuRequestDto.getMenuname();
        this.category = menuRequestDto.getCategory();
        this.imageUrl = imageUrl;
    }

    public void updateMenu(MenuRequestDto menuRequestDto, String imageUrl) {
        this.menuname = menuRequestDto.getMenuname();
        this.category = menuRequestDto.getCategory();
        this.imageUrl = imageUrl;
    }
}