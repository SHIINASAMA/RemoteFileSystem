import pers.kaoru.rfs.core.FileOperator;

import java.io.File;

public class TestFileOperator {
    public static void main(String[] args) {
        FileOperator operator = new FileOperator();

        // list show
        var files = operator.listShow(new File("E:/"));
        assert files != null;
        for (var file : files) {
            System.out.println(file);
        }

        // remove file
        var removeFile = operator.remove(new File("E:/test.txt"));
        // remove dir
        var removeDir = operator.remove(new File("E:/test"));
        System.out.println("remove file: " + removeFile);
        System.out.println("remove dir: " + removeDir);

        // move
        var move = operator.move(new File("E:/test0"), new File("E:/test1"));
        System.out.println("move: " + move);

        // copy file
        var copyFile = operator.copy(new File("E:/test0.txt"), new File("E:/test1.txt"));
        // copy dir
        var copyDir = operator.copy(new File("E:/test2"), new File("E:/test3"));
        System.out.println("copy file: " + copyFile);
        System.out.println("copy dir: " + copyDir);
    }
}
