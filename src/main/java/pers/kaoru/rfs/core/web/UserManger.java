package pers.kaoru.rfs.core.web;

import java.util.TreeMap;

public class UserManger {

    private TreeMap<String ,UserInfo> users;

    public void addUser(UserInfo userInfo) {
        users.put(userInfo.getName(), userInfo);
    }

    public UserInfo getUser(String name){
        return users.get(name);
    }

    public static boolean VerifyPermission(UserInfo user, UserPermission permission){
        // return user.getPermission() - permission;
        return user.getPermission().compareTo(permission) >= 0;
    }
}
