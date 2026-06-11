package com.example.controller;

import cn.hutool.core.util.StrUtil;
import com.example.common.Result;
import com.example.common.enums.RoleEnum;
import com.example.common.exception.BusinessErrorEnum;
import com.example.common.exception.BusinessException;
import com.example.mapper.AdminMapper;
import com.example.utils.AliyunOSSOperator;
import com.example.utils.UserUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;
    @Resource
    private AdminMapper adminMapper;
//    @Resource
//    private TeacherService teacherService;
    /**
     * 文件上传到阿里云OSS
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try {
            byte[] bytes = file.getBytes();
            String ossUrl = aliyunOSSOperator.upload(bytes, fileName);
            Map<String, Object> currentUser = UserUtil.getCurrentUser();
            if (currentUser == null) {
                throw new BusinessException(BusinessErrorEnum.TOKEN_INVALID);
            }
            Integer id = (Integer) currentUser.get("id");
            String role = currentUser.get("role").toString();
            if (RoleEnum.ADMIN.name().equals(role)) {
                adminMapper.updateAvatar(id, ossUrl);
            } else if (RoleEnum.TEACHER.name().equals(role)) {
//                teacherService.updatePassword(accountDTO);
            }
            return Result.success(ossUrl);
        } catch (Exception e) {
            log.error(fileName + "--文件上传失败", e);
            return Result.error("文件上传失败");
        }
    }

    /**
     * 获取文件（保留此方法以保持兼容性）
     */
    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) {
        OutputStream os;
        try {
            if (StrUtil.isNotEmpty(fileName)) {
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
                response.setContentType("application/octet-stream");
                os = response.getOutputStream();
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            log.warn("文件下载失败：" + fileName);
        }
    }
}
