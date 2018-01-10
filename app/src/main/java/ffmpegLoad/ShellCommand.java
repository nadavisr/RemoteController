/*
 * Created by Administrator on 09/01/2018
 * Last modified 13:52 28/12/17
 */

package ffmpegLoad;

import android.util.Log;

import java.io.IOException;

/**
 * Created by Administrator on 28/12/2017.
 */

public class ShellCommand {



    Process run(String[] commandString) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(commandString);
        } catch (IOException e) {
            Log.e("Exception while run: " + commandString, e.getMessage());
        }
        return process;
    }

    CommandResult runWaitFor(String[] s) {
        Process process = run(s);

        Integer exitValue = null;
        String output = null;
        try {
            if (process != null) {
                exitValue = process.waitFor();

                if (CommandResult.success(exitValue)) {
                    output = Util.convertInputStreamToString(process.getInputStream());
                } else {
                    output = Util.convertInputStreamToString(process.getErrorStream());
                }
            }
        } catch (InterruptedException e) {
            Log.e("Interrupt exception", e.getMessage());
        } finally {
            Util.destroyProcess(process);
        }

        return new CommandResult(CommandResult.success(exitValue), output);
    }

}