import pers.kaoru.rfs.client.Router;

public class TestRouter {
    public static void main(String[] args) {
        Router router = new Router();
        System.out.println(router.back());
        System.out.println(router);

        router.enter("dir1");
        router.enter("dir2");
        System.out.println(router);
        System.out.println(router.current());
    }
}
