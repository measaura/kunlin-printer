package cn.westlan.coding.login.bean;

public enum UserType {
    Customer("一般客户"),
    OEM("OEM"),
    Dealer("经销商");

    private String value;

    UserType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
