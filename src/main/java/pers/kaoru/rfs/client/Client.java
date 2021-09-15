package pers.kaoru.rfs.client;

import pers.kaoru.rfs.ImplExecutable;
import pers.kaoru.rfs.client.ui.MainWindow;

public class Client implements ImplExecutable {

    @Override
    public void exec() {
        new MainWindow();
    }
}
