package cn.westlan.coding.login.bean;

import lombok.Data;

@Data
public class UserInfo {
    private String account;
    private String password;
    private String name;
    private String email;
    private String companyName;
    private String userType;
}
