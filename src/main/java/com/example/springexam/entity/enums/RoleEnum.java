package com.example.springexam.entity.enums;

public enum RoleEnum {
    ROLE_USER("user"), //tengkuchli == RolEnum enum = new RoleEnum("Role USER")
    ROLE_ADMINUSER("admin"),
    ROLE_SUPERADMIN("superadmin");
    private String description;

    RoleEnum(String description) {
        this.description = description;
    }
}
