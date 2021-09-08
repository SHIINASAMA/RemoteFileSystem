import pers.kaoru.rfs.core.MD5Utils;

public class TestMD5Utils {
    public static void main(String[] args) {
        var s1 = MD5Utils.GenerateMD5("hello");
        var s2 = MD5Utils.GenerateMD5("hi");
        var s3 = MD5Utils.GenerateMD5("hello");

        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s3);
    }
}