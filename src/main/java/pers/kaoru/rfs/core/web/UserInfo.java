package pers.kaoru.rfs.core.web;

public class UserInfo {
    private final String name;
    private final String password;
    private final UserPermission permission;

    public UserInfo(String name, String password){
        this.name = name;
        this.password = password;
        this.permission = UserPermission.READ;
    }

    public UserInfo(String name, String password, UserPermission permission) {
        this.name = name;
        this.password = password;
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public UserPermission getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", permission=" + permission +
                '}';
    }
}
