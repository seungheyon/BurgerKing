package com.example.burgerking.repository;

import com.example.burgerking.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByCategoryOrderByCreatedDateDesc(String category);
    List<Menu> findAllByOrderByCreatedDateDesc();
}
