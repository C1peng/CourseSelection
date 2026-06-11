//package com.example.common.enums;
//
//public enum PermissionEnum {
//    // 管理员权限
//    ADMIN_VIEW(PermissionEnum.ADMIN_VIEW_CODE, "管理员查看"),
//    ADMIN_ADD(PermissionEnum.ADMIN_ADD_CODE, "管理员新增"),
//    ADMIN_EDIT(PermissionEnum.ADMIN_EDIT_CODE, "管理员编辑"),
//    ADMIN_DELETE(PermissionEnum.ADMIN_DELETE_CODE, "管理员删除"),
//
//    // 教师权限
//    TEACHER_VIEW(PermissionEnum.TEACHER_VIEW_CODE, "教师查看"),
//    TEACHER_ADD(PermissionEnum.TEACHER_ADD_CODE, "教师新增"),
//    TEACHER_EDIT(PermissionEnum.TEACHER_EDIT_CODE, "教师编辑"),
//    TEACHER_DELETE(PermissionEnum.TEACHER_DELETE_CODE, "教师删除"),
//
//    // 学生权限
//    STUDENT_VIEW(PermissionEnum.STUDENT_VIEW_CODE, "学生查看"),
//    STUDENT_EDIT(PermissionEnum.STUDENT_EDIT_CODE, "学生编辑"),
//    STUDENT_ADD(PermissionEnum.STUDENT_ADD_CODE, "学生新增"),
//    STUDENT_DELETE(PermissionEnum.STUDENT_DELETE_CODE, "学生删除");
//
//    // 管理员权限常量
//    public static final String ADMIN_VIEW_CODE = RoleEnum.ADMIN_CODE + ":VIEW";
//    public static final String ADMIN_ADD_CODE = RoleEnum.ADMIN_CODE + ":ADD";
//    public static final String ADMIN_EDIT_CODE = RoleEnum.ADMIN_CODE + ":EDIT";
//    public static final String ADMIN_DELETE_CODE = RoleEnum.ADMIN_CODE + ":DELETE";
//
//    // 教师权限常量
//    public static final String TEACHER_VIEW_CODE = RoleEnum.TEACHER_CODE + ":VIEW";
//    public static final String TEACHER_ADD_CODE = RoleEnum.TEACHER_CODE + ":ADD";
//    public static final String TEACHER_EDIT_CODE = RoleEnum.TEACHER_CODE + ":EDIT";
//    public static final String TEACHER_DELETE_CODE = RoleEnum.TEACHER_CODE + ":DELETE";
//
//    // 学生权限常量
//    public static final String STUDENT_VIEW_CODE = RoleEnum.STUDENT_CODE + ":VIEW";
//    public static final String STUDENT_ADD_CODE = RoleEnum.STUDENT_CODE + ":ADD";
//    public static final String STUDENT_EDIT_CODE = RoleEnum.STUDENT_CODE + ":EDIT";
//    public static final String STUDENT_DELETE_CODE = RoleEnum.STUDENT_CODE + ":DELETE";
//
//    private final String code;
//    private final String description;
//
//    PermissionEnum(String code, String description) {
//        this.code = code;
//        this.description = description;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//}
