package com.chaquo.python.demo;

import android.app.Application;

import com.chaquo.python.PyObject;

public class ECDaemonReplActivity extends ReplActivity {

    @Override protected Class<? extends Task> getTaskClass() {
        return Task.class;
    }

    public static class Task extends ReplActivity.Task {
        public Task(Application app) {
            super(app);
        }

        @Override public void run() {
            PyObject builtins = py.getBuiltins();

            // Create config options
            PyObject config_options = builtins.callAttr("dict");
            config_options.callAttr("__setitem__", "cmd", "daemon");
            config_options.callAttr("__setitem__", "verbose", true);

            // Generate config
            PyObject SimpleConfig = py.getModule("lib").get("SimpleConfig");
            PyObject config = SimpleConfig.call(config_options);

            // Generate wallet storage
            PyObject WalletStorage = py.getModule("lib.storage").get("WalletStorage");
            PyObject storage = WalletStorage.call(config.callAttr("get_wallet_path"));

            // Start daemon
            PyObject daemon = py.getModule("lib").get("daemon");
            PyObject fd_daemon = daemon.callAttr("get_fd_or_server", config);
            PyObject fd = fd_daemon.callAttr("__getitem__", 0);
            PyObject d = daemon.callAttr("Daemon", config, fd, true);
            d.callAttr("start");

            super.run();
        }
    }
}
