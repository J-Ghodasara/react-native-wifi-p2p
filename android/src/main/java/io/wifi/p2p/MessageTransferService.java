package io.wifi.p2p;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import static io.wifi.p2p.Utils.CHARSET;
import static io.wifi.p2p.Utils.copyBytes;

/**
 * Created by zyusk on 03.11.2018.
 */
public class MessageTransferService extends IntentService {
    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_MESSAGE = "io.wifi.p2p.SEND_MESSAGE";
    public static final String EXTRAS_DATA = "message";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
    private static final String TAG = "RNWiFiP2P";

    public MessageTransferService(String name) {
        super(name);
    }

    public MessageTransferService() {
        super("FileTransferService");
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_SEND_MESSAGE)) {
            String message = intent.getExtras().getString(EXTRAS_DATA);
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
            Socket socket = new Socket();

            try {
                Log.i(TAG, "Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                Log.i(TAG, "Client socket connected - " + socket.isConnected());
                OutputStream stream = socket.getOutputStream();
                InputStream is = new ByteArrayInputStream(message.getBytes(Charset.forName(CHARSET)));
                copyBytes(is, stream);
                Log.i(TAG, "Client: Data written");
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
