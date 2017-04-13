package edu.buffalo.cse.cse486586.groupmessenger2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 *
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {
    static final String TAG = GroupMessengerActivity.class.getSimpleName();
    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";

    static final String[] REMOTE_PORT = {REMOTE_PORT0, REMOTE_PORT1, REMOTE_PORT2, REMOTE_PORT3, REMOTE_PORT4};
    static final int SERVER_PORT = 10000;
    String myPort;
    String isDeliverable = "false";
    String portStr;
//    int max_proSqn = 0;
//    String msg;

    protected Uri mUri;
    static int global_sequence = 0;
    static int sqNum = 0;
    static int mid = 0;
    static int sender;
    static int agreeSqn = 0;
    static int proSqn = 0;
//    static int max_proSqn = 0;

    PriorityBlockingQueue<Message> holdQueue = new PriorityBlockingQueue<Message>(100, new msgComparator());
    PriorityBlockingQueue<Message> priQueue = new PriorityBlockingQueue<Message>(100, new MessageComparator());
    ArrayList<Message> msgList = new ArrayList<Message>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
            myPort = String.valueOf((Integer.parseInt(portStr) * 2));
            sender = (Integer.parseInt(portStr)-5554)/2;

            try {
                ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);

            } catch (IOException e) {
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }

//        Socket socket = new Socket(InetAddress.getByAddress(ip,myPort));
//
//        send: myPort;type: join;
//        receive: str1, str2;


        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
//        Log.e(TAG, "displaying fail");

        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */
        final EditText editText = (EditText) findViewById(R.id.editText1);
        final Button button = (Button) findViewById(R.id.button4);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.button4) {

                    String msg =  editText.getText().toString() + "\n";
                    Message message = new Message("send", msg, mid, sender, sqNum, "false");
//                    message.msg = editText.getText().toString() + "\n";
                    editText.setText("");
                    new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, message);
                    message.sender = (Integer.parseInt(portStr)-5554)/2;
                    mid++;

                }
                return;
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    String msg = editText.getText().toString() + "\n";
                    editText.setText(""); // This is one way to reset the input box.
//                    TextView textView = (TextView) findViewById(R.id.textView1);
//                    textView.append("\t" + msg + "\n"); // This is one way to display a string.
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }

    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {
        
        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];

            try {
                while (true) {

                    Socket clientSocket = serverSocket.accept();
                    InputStream is = clientSocket.getInputStream();
                    DataInputStream dis = new DataInputStream(is);
                    String s = dis.readUTF();
                    String[] msg1 = s.split("\t");
                    // publishProgress(msg1[0]);

                    Message message = new Message(msg1[0], msg1[1], Integer.parseInt(msg1[2]),
                            Integer.parseInt(msg1[3]), Integer.parseInt(msg1[4]), msg1[5]);

//                    publishProgress(message.toString());

                    if (message.type.equals("send")){

//                        Deliver holdbackqueue = new Deliver(message.type, msg1[1], Integer.parseInt(msg1[2]),
//                                Integer.parseInt(msg1[3]), Integer.parseInt(msg1[4]), isDeliverable);

                        proSqn = Math.max(proSqn,agreeSqn)+1;
//                        mid++;

                        message.type = "sendback";
                        message.sqNum = proSqn;
//                        message.sender = (Integer.parseInt(portStr)-5554)/2;
                        priQueue.put(message);

//                        publishProgress(priQueue.toString());

                        OutputStream os = clientSocket.getOutputStream();
                        DataOutputStream dos = new DataOutputStream(os);
                        dos.writeUTF(message.toString());

                        dos.flush();
                        dos.close();

                    } else if (message.type.equals("Multicast")){

                        OutputStream os = clientSocket.getOutputStream();
                        DataOutputStream dos = new DataOutputStream(os);
                        dos.writeUTF("Socket Open");

//                        dos.flush();

                        Log.i(TAG, "sqNum " + message.sqNum);

//                        message.sender = (Integer.parseInt(myPort)-11108)/4;
                        agreeSqn = Math.max(message.sqNum,agreeSqn);
                        proSqn = message.sqNum;

                        if (message.msg != null ){

                            Iterator<Message> it = priQueue.iterator();

                            while (it.hasNext()) {

                                Message tempMsg = it.next();

//                                Log.i(TAG, "previous " + tempMsg.toString());

                                if ((tempMsg.mid == message.mid) && (tempMsg.sender == message.sender)) {
                                    it.remove();
                                    tempMsg.isDeliverable = "true";
                                    tempMsg.type = "Multicast";
                                    tempMsg.sqNum = message.sqNum;
                                    priQueue.put(tempMsg);
//                                    publishProgress(priQueue.toString());
                                }
//                                Log.i(TAG, "after " + tempMsg.toString());

                            }

                        }


                    }

                    Iterator<Message> iter = priQueue.iterator();

                    while(iter.hasNext()){
                        Message finalMsg = iter.next();
                        if (finalMsg.isDeliverable.equals("true")){

                            iter.remove();
                            publishProgress(finalMsg.toString());

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("key", Integer.toString(global_sequence));
                            contentValues.put("value", finalMsg.msg);
                            mUri = getContentResolver().insert(GroupMessengerProvider.CONTENT_URI, contentValues);
                            global_sequence++;

                        } else{
                            break;
                        }
                    }

                    dis.close();
                    clientSocket.close();
                }

            } catch (StreamCorruptedException e){
                Log.e(TAG, "StreamCorrupted error");
            } catch (IOException e) {
                Log.e(TAG, "serverSocket error");
            }
            return null;
        }

        protected void onProgressUpdate(String... strings) {
            String strReceived = strings[0].trim();
            TextView textView = (TextView) findViewById(R.id.textView1);
            textView.append(strReceived + "\t\n");
//            ContentValues contentValues = new ContentValues();
//            contentValues.put("key", global_sequence);
//            contentValues.put("value", strReceived);
//            mUri = getContentResolver().insert(GroupMessengerProvider.CONTENT_URI, contentValues);
//            global_sequence++;

        }

    }

    private class ClientTask extends AsyncTask<Message, Void, Void> {
        int max_proSqn = 0;

        @Override
        protected Void doInBackground(Message... msgs) {

            Message msgToSend = msgs[0];

            try {

                for (int i = 0; i < REMOTE_PORT.length; i++) {

                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress("10.0.2.2", Integer.parseInt(REMOTE_PORT[i])), 8000);
                    OutputStream os = socket.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(os);
                    dos.writeUTF(msgToSend.toString());

                    dos.flush();

                    //receive sendback message
                    InputStream is = socket.getInputStream();
                    DataInputStream dis = new DataInputStream(is);
                    String strReceived = dis.readUTF();
                    String[] str = strReceived.split("\t");
//                    Log.i(TAG, " sqm " + str[4] + " mid " + str[2] + " sender " + str[3]);

                    max_proSqn = Math.max(max_proSqn, Integer.parseInt(str[4]));
//                    Log.d(TAG, "number " + max_proSqn);
//                    dos.close();
//                    dis.close();
                    dos.close();
                    socket.close();

                }

            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");

            } catch (SocketTimeoutException e) {
                Log.e(TAG, "Socket Timeout");

            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException");

            }

            msgToSend.sqNum = max_proSqn;

            try {

                for (int i = 0; i < REMOTE_PORT.length; i++) {

                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress("10.0.2.2", Integer.parseInt(REMOTE_PORT[i])), 8000);
                    OutputStream os = socket.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(os);
                    msgToSend.type = "Multicast";

                    dos.writeUTF(msgToSend.toString());

                    dos.flush();

//                    //receive response
//                    InputStream is = socket.getInputStream();
//                    DataInputStream dis = new DataInputStream(is);
//                    String strReceived = dis.readUTF();
//                    Log.i(TAG,"printout " + strReceived);

//                    str = strReceived.split("\t");
//                    Log.e(TAG, "failed " + msgToSend.toString());
                    dos.close();
//                    dis.close();
                    socket.close();

                }

            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");

            } catch (SocketTimeoutException e) {
                Log.e(TAG, "Socket Timeout");

            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException" + msgToSend.sqNum);

            }

            return null;

        }

    }

}