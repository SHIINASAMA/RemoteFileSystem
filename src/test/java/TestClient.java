import pers.kaoru.rfs.client.LoginForm;

public class TestClient {
    public static void main(String[] args) {
        var form = new LoginForm();
        if (form.getState()) {
            System.out.println(form.getHost());
            System.out.println(form.getPort());
            System.out.println(form.getUserName());
            System.out.println(form.getPwdMd5());
        }
    }
}
