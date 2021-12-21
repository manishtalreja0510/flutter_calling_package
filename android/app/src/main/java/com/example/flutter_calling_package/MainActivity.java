package com.example.flutter_calling_package;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.*;
import android.os.AsyncTask;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.gson.Gson;
import io.flutter.embedding.android.FlutterActivity;
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//import io.flutter.plugins.



public class MainActivity extends FlutterActivity {
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final Executor executor = executorService;
    JWebSocketClient client;
    AudioRecord record;
    Map<String, AudioTrack> audios = new HashMap<>();
    boolean isRecording = false;
    int bufferSize = 1900;

    private static final String CHANNEL = "manishtalreja.github.io/connectWithCall";



    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            System.out.println(call.arguments);
                            String username=call.argument("username");
                            String groupname =call.argument("groupname");
                            System.out.println(username);

//                            System.out.println(call.argument("username"));
                            // Note: this method is invoked on the main thread.
                            if (call.method.equals("connectWithCall")) {
                                System.out.println("create connection is called");
                                createConnectionWithCall(username, groupname);
                                result.success(29);
                            } else {

                                if(call.method.equals("muteAudio")){
                                    stopAudioRecording();
                                } else{
                                    if(call.method.equals("unmuteAudio")){
                                        unmuteAudio();
                                    }
                                    else{
                                        if(call.method.equals("endCall")){
                                            endCall();
                                        } else{
                                            result.notImplemented();
                                        }
                                    }
                                }
                            }
                        }
                );
    }

    public void createConnectionWithCall(String username, String groupname){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    0);

        }else{
            URI uri = URI.create("ws://94.237.121.70:9090/call/" + groupname + "/" + username);
            System.out.println("connecting to:" + uri);
            client = new JWebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Websocket is opened successfully");
                }

                @Override
                public void onMessage(String string) {
                    onMessageFromWebsocket(string);
//                    class OnMessageAsyncTask extends AsyncTask<String, Void, String> {
//                        @Override
//                        protected String doInBackground(String... params) {
//                            onMessageFromWebsocket(string);
//                            return null;
//                        }
//                    }
//                    OnMessageAsyncTask onMessageAsyncTask = new OnMessageAsyncTask();
//                    onMessageAsyncTask.execute();
                }
            };
            client.connect();
            class StartRecordingAsyncTask extends AsyncTask<String, Void, String> {
                @Override
                protected String doInBackground(String... params) {
                    recordByAudioRecord();
                    return null;
                }
            }
            StartRecordingAsyncTask startRecordingAsyncTask = new StartRecordingAsyncTask();
            startRecordingAsyncTask.execute();
        }
    }

    public void unmuteAudio(){
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                t.run();
//            }
//        });
        class StartRecordingAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                recordByAudioRecord();
                return null;
            }
        }
        StartRecordingAsyncTask startRecordingAsyncTask = new StartRecordingAsyncTask();
        startRecordingAsyncTask.execute();
    }

    public void endCall(){
        stopAudioRecording();
        client.close();
    }

    public void onNewUserAdded(String username){
        System.out.println("Adding..." + username);
        int _rate = 16000;
        int buffersize =  20000;
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, _rate,
                AudioFormat.CHANNEL_OUT_DEFAULT, AudioFormat.CHANNEL_OUT_DEFAULT, buffersize, AudioTrack.WRITE_NON_BLOCKING);
        audios.put(username, audioTrack);
        System.out.println(audioTrack.getStreamType());
        System.out.println("new user is added:" + username);
    }
    public void onUserLeft(String username){
        audios.get(username).stop();
        audios.remove(username);
        System.out.println("user left:" + username);

    }
    public void writeAudio(String username, ByteBuffer byteBuffer){
        System.out.println("inside write audio");
        System.out.println(byteBuffer);
        audios.get(username).write(byteBuffer, bufferSize, AudioTrack.WRITE_NON_BLOCKING);
        audios.get(username).play();
        System.out.println("playing audio");
//        audios.get(username).write()
    }




    public void onMessageFromWebsocket(String string){
        Gson gson = new Gson();
        try {
            MessageFromWebSocket message = gson.fromJson(string, MessageFromWebSocket.class);
            if (message.getType().equals("noOfPersonsInTheGroup")) {
                message.getUsers().forEach(newUser -> {
                    onNewUserAdded(newUser);
                });
            }
            if (message.getAudio() != null) {
                if (audios.containsKey(message.getUsername())) {
                    System.out.println("the message is ");
                    System.out.println(message.getAudio());
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    writeAudio(message.getUsername(), ByteBuffer.wrap(Base64.getDecoder().decode(message.getAudio()), 0, bufferSize));
//                                }

                } else {
//TODO: do something here if list of audios doesn't contain the specific username.
                }

            } else {
                if (message.getType().equals("userJoinedNotification")) {
                    System.out.println("inside user joined notification");
                    onNewUserAdded(message.getUsername());
                } else {
                    if (message.getType().equals("userLeftNotification")) {
                        onUserLeft(message.getUsername());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("error is occured in try block");
            e.printStackTrace();
        }
    }

    public void recordByAudioRecord(){
        int rate = 16000;
        int channelConfig = AudioFormat.CHANNEL_OUT_DEFAULT;
        int audioFormat = AudioFormat.CHANNEL_OUT_DEFAULT;
        System.out.println("all variables initialized. about to initialize recorder");
        try{
            record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);
        }catch (SecurityException e){
            System.out.println(e);
            System.out.println("error in initializing audio recorder");
        }
        record.startRecording();
        isRecording = true;
        System.out.println("recording started");
        System.out.println(record.getRecordingState());
        System.out.println(AudioRecord.RECORDSTATE_RECORDING);
        while (record.getRecordingState()==AudioRecord.RECORDSTATE_RECORDING){
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());
                record.read(byteBuffer, bufferSize);
                System.out.println(byteBuffer);
                client.send(byteBuffer);
                byteBuffer.clear();
            }catch (Exception e)
            {}
        }

    }
    public void stopAudioRecording(){
        if(isRecording==true){
            record.stop();
            record.release();
            isRecording = false;
            System.out.println("Recording is stopped");
        } else{
            System.out.println("recorder is not recording");
        }
    }

}