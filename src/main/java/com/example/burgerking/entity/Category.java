package com.example.burgerking.entity;


public enum Category {
    SPECIAL_DISCOUNT("스페셜&할인팩", "BK001"),
    NEW("신제품", "BK002"),
    PREMIUM("프리미엄", "BK003"),
    WHOPPER_JUNIOR("와퍼&주니어", "BK004"),
    CHICKEN_SHRIMP("치킨&슈림프버거", "BK005"),
    ALLDAY_MORNING("올데이킹&킹모닝", "BK006"),
    SIDE("사이드", "BK007"),
    DRINK_DESSERT("음료&디저트", "BK008");

    private String viewName;
    private String category;

    Category(String viewName, String category) {
        this.viewName = viewName;
        this.category = category;
    }

    public String getViewName() {
        return this.viewName;
    }

    public String getCategory() {
        return this.category;
    }
}
