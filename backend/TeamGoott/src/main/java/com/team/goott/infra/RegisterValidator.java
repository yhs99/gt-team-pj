package com.team.goott.infra;

import java.util.regex.Pattern;

import org.springframework.web.multipart.MultipartFile;

import com.team.goott.user.register.domain.UserRegisterDTO;

public class RegisterValidator {

	private final String SUCCESS = "success";
	// 이메일 형식 검증
    private String validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!Pattern.matches(emailRegex, email)) {
            return "이메일 형식이 올바르지 않습니다.";
        }
        return SUCCESS;
    }

    // 비밀번호 8자리 이상 검증
    private String validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return "비밀번호는 최소 8자리 이상이어야 합니다.";
        }
        return SUCCESS;
    }

    // 이름은 한글만 허용
    private String validateName(String name) {
        String nameRegex = "^[가-힣]+$";
        if (!Pattern.matches(nameRegex, name)) {
            return "이름은 한글만 입력 가능합니다.";
        }
        return SUCCESS;
    }

    // 핸드폰 번호 형식 검증
    private String validatePhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\d{3}-\\d{4}-\\d{4}$";
        if (!Pattern.matches(phoneRegex, phoneNumber)) {
            return "핸드폰 번호 형식은 ###-####-#### 이어야 합니다.";
        }
        return SUCCESS;
    }

    // 성별은 M 또는 W만 허용
    private String validateGender(String gender) {
        if (!"M".equals(gender) && !"W".equals(gender)) {
            return "성별을 선택하세요.";
        }
        return SUCCESS;
    }

    // 이미지 파일 형식 검증 (PNG, JPG만 허용)
    private String validateImageFile(MultipartFile file) {
    	if(file.getOriginalFilename().length() <= 0) {
    		System.out.println(file.getOriginalFilename().isEmpty());
    		return SUCCESS;
    	}
        if (file != null && file.getOriginalFilename().length() > 0) {
	        String fileName = file.getOriginalFilename();
	        if (fileName != null) {
	            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
	            if (!"png".equals(fileExtension) && !"jpg".equals(fileExtension)) {
	                return "이미지 파일 형식은 PNG 또는 JPG만 가능합니다.";
	            }
	        }
        }
        return SUCCESS;
    }
    
    public String batchValidate(UserRegisterDTO user) {
    	String errorMsg = "success";
    	
    	errorMsg = validateEmail(user.getEmail());
    	if(errorMsg != SUCCESS) return errorMsg;
    	errorMsg = validateGender(user.getGender());
    	if(errorMsg != SUCCESS) return errorMsg;
    	errorMsg = validateImageFile(user.getImageFile());
    	if(errorMsg != SUCCESS) return errorMsg;
    	errorMsg = validateName(user.getName());
    	if(errorMsg != SUCCESS) return errorMsg;
    	errorMsg = validatePassword(user.getPassword());
    	if(errorMsg != SUCCESS) return errorMsg;
    	errorMsg = validatePhoneNumber(user.getMobile());
    	if(errorMsg != SUCCESS) return errorMsg;
    	
    	return errorMsg;
    }
}
