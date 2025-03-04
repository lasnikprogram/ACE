package com.kuhakupixel.atg.backend;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

public class ACEServer {
    /*
     * Get thread to start the server
     * */
    public static Thread GetStarterThread(Context context, Long pid, Integer portNum) throws IOException {
        Thread thread = new Thread(
                () -> {
                    Log.i("ATG", String.format("Running engine server at port %d", portNum));
                    String path = "";
                    try {
                        path = Binary.GetBinPath(context, Binary.Type.server);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                    assert (new File(path).exists());

                    System.out.println("Binary path is " + path);
                    String[] cmds = new String[]{path, "attach-pid", "--pid", pid.toString(), "--port", portNum.toString()};

                    String cmd_string = String.join(" ", cmds);
                    System.out.printf("Running command %s\n", cmd_string);
                    try {
                        // To run the server, we can't use topjohnwu's libsu
                        // because I think there is a bug where multiple shell
                        // can't be run in pararrel, despite building new instance of Shell
                        // https://github.com/topjohnwu/libsu/issues/91
                        // so this hacky solution will do for now,
                        // we can still use libsu for the client for
                        // getting the output of the client
                        Root.sudo(cmd_string);
                    } catch (IOException e) {
                        System.out.printf("Error when starting server %s\n", e.getMessage());
                    }

                }

        );

        return thread;
    }


}
