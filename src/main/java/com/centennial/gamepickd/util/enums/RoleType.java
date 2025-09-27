package com.centennial.gamepickd.util.enums;

public enum RoleType {
    ADMIN("ROLE_ADMIN", "ADMIN"),
    CONTRIBUTOR("ROLE_CONTRIBUTOR", "CONTRIBUTOR"),
    MEMBER("ROLE_MEMBER", "MEMBER");

    private final String str;
    private final String val;

    RoleType(String str, String val){
        this.str = str;
        this.val = val;
    }

    public String str(){
        return this.str;
    }
    public String val() {
        return this.val;
    }
}
