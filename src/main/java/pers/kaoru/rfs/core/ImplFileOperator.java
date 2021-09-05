package pers.kaoru.rfs.core;

import java.io.File;

public interface ImplFileOperator {

    /**
     * 对应 ls 命令
     * @param source 必须为一个目录
     * @return 目录下文件列表
     */
    File[] listShow(File source);

    /**
     * 对应 rm 命令，支持递归删除文件夹
     * @param source 可以为文件或者目录
     * @return 是否操作成功
     */
    boolean remove(File source);

    /**
     * 对应 cp 命令，支持复制文件夹
     * @param source 源位置
     * @param destination 目标位置
     * @return 是否操作成功
     */
    boolean copy(File source, File destination);

    /**
     * 对应 mv 命令，支持移动文件夹
     * @param source 源位置
     * @param destination 目标位置
     * @return 是否操作成功
     */
    boolean move(File source, File destination);

}
