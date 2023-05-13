package com.example.burgerking.entity;

public enum MenuCategoryEnum {
    SPECIAL_DISCOUNT(CategoryCode.SPECIAL_DISCOUNT),
    NEW("BK002"),
    PREMIUM("BK003"),
    WHOPPER_JUNIOR("BK004"),
    CHICKEN_SHRIMP("BK005"),
    ALLDAY_MORNING("BK006"),
    SIDE("BK007"),
    DRINK_DESSERT("BK008");

    private final String categoryCode;

    MenuCategoryEnum(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategory() {
        return this.categoryCode;
    }

    public static class CategoryCode {
        public static final String SPECIAL_DISCOUNT = "categoryCode_BK001";
        public static final String NEW = "categoryCode_BK002";
        public static final String PREMIUM = "categoryCode_BK003";
        public static final String WHOPPER_JUNIOR = "categoryCode_BK004";
        public static final String CHICKEN_SHRIMP = "categoryCode_BK005";
        public static final String ALLDAY_MORNING = "categoryCode_BK006";
        public static final String SIDE = "categoryCode_BK007";
        public static final String DRINK_DESSERT = "categoryCode_BK008";


    }
}
