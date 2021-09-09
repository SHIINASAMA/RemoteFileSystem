package pers.kaoru.rfs.core;

public enum Error {

    PERMISSION_DENIED,      // 权限不足
    NO_TOKEN,               // 没有令牌 - 特殊的缺少参数，没有令牌
    INVALID_TOKEN,          // 令牌无效 - token 过期
    NO_USER,                // 不存在用户
    ILLEGAL_PATH,           // 非法路径 - 比如试图访问上级路径
    ILLEGAL_PARAMETER,      // 非法参数 - 一般是缺少参数
    REMOVE_OPERATE_FAIL,    // 移动失败
    FILE_NOT_FOUND,         // 文件不存在
    COPY_OPERATE_FAIL,      // 复制失败
    MOVE_OPERATE_FAIL,      // 移动失败
    MAKE_DIR_OPERATE_FAIL,  // 创建文件夹失败
    FILE_ALREADY_EXIST,     // 文件已经存在
    CREATE_FILE_FAIL,       // 创建文件失败
    ILLEGAL_SEEK_LOCATION,  // 非法的文件流跳转
    WRONG_PASSWORD          // 密码错误
}
