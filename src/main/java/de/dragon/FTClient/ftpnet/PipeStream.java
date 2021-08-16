package de.dragon.FTClient.ftpnet;

import de.dragon.FTClient.frame.progressbar.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface PipeStream {

    public default void pipe(InputStream in, OutputStream out, ProgressBar progressBar, long size) throws IOException {
        byte[] buffer = new byte[16 * 1024];
        int am;
        int rounds = 0;
        while ((am = in.read(buffer)) > 0) {
            progressBar.updatePercent((rounds * buffer.length * 1D + am) / size);
            out.write(buffer, 0, am);
            out.flush();
            rounds++;
        }
    }

}
