package step.learning.android_spd_111;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import step.learning.android_spd_111.orm.ChatMessage;
import step.learning.android_spd_111.orm.ChatResponse;

public class ChatActivity extends AppCompatActivity {

    private static final String CHAT_URL = "https://chat.momentfor.fun/";
    private final byte[] buffer = new byte[8096];
    // паралельні запити до кількох ресурсів не працюють, виконується лише один
    // це обмежує вибір виконавчого сервісу
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final List<ChatMessage> chatMessages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CompletableFuture
                .supplyAsync(this::loadChat, executorService)
                .thenApply(this::processChatResponse)
                .thenAcceptAsync(this::displayChatMessages);
    }

    private String loadChat() {
        try (InputStream chatStream = new URL(CHAT_URL).openStream()) {
            String response = readString(chatStream);
            // runOnUiThread( () ->
            //         ((TextView)findViewById(R.id.chat_tv_title)).setText(response)
            // );
            return response;
        }
        catch(Exception ex) {
            Log.e("ChatActivity::loadChat()",
                    ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
        }
        return null;
    }

    private boolean processChatResponse(String response) {
        boolean wasNewMessage = false;
        try {
            ChatResponse chatResponse = ChatResponse.fromJsonString(response);
            for(ChatMessage message : chatResponse.getData()) {
                if(this.chatMessages.stream().noneMatch(
                        m -> m.getId().equals(message.getId()))) {
                    // немає жодного повідомлення з таким id, як у message -- це нове повідомлення
                    this.chatMessages.add(message);
                    wasNewMessage = true;
                }
            }
        }
        catch(IllegalArgumentException ex) {
            Log.e("ChatActivity::processChatResponse",
                    ex.getMessage() == null ? ex.getClass().getName() : ex.getMessage());
        }
        return wasNewMessage;
    }

    private void displayChatMessages(boolean wasNewMessage) {
        if(!wasNewMessage) return;
        Drawable myBackground = AppCompatResources.getDrawable(
                getApplicationContext(),
                R.drawable.chat_msg_my);
        LinearLayout.LayoutParams msgParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        msgParams.setMargins(0, 10, 8, 10);
        msgParams.gravity = Gravity.END;
        runOnUiThread( () -> {
            LinearLayout container = findViewById(R.id.chat_container);
            for(ChatMessage message : this.chatMessages) {
                TextView tv = new TextView(this);
                tv.setText(message.getAuthor() + ": " + message.getText() + "\n");
                tv.setBackground(myBackground);
                tv.setGravity(Gravity.END);
                tv.setPadding(15,5,15,5);
                tv.setLayoutParams(msgParams);
                container.addView(tv);
            }
        });
    }

    private String readString(InputStream stream) throws IOException {
        ByteArrayOutputStream byteBuilder = new ByteArrayOutputStream();
        int len;
        while ((len = stream.read(buffer)) != -1) {
            byteBuilder.write(buffer, 0, len);
        }
        String res = byteBuilder.toString();
        byteBuilder.close();
        return res;
    }
    @Override
    protected void onDestroy() {
        executorService.shutdownNow();
        super.onDestroy();
    }
}

/*
Робота з мережею Інтернет
Основу складає клас java.net.URL
традиційно для Java створення об'єкту не призводить до якоїсь активності,
лише створюється програмний об'єкт.
Підключення та передача даних здійснюється при певних командах,
зокрема, відкриття потоку.
Читання даних з потоку має особливості
- мультибайтове кодування: різні символи мають різну байтову довжину. Це
    формує вимогу спочатку одержати всі дані у бінарному вигляді і потім
    декодувати як рядок (замість одержання фрагментів рядків і їх перетворення)
- запити до мережі не можуть виконуватись з основного (UI) потоку. Це спричинює
    виняток (android.os.NetworkOnMainThreadException)
    Варіанти рішень
    = запустити в окремому потоці
        + простіше і наочніше
        - складність завершення різних потоків, особливо, якщо їх багато
    = запустити у фоновому виконавці
        + централізоване завершення
        - не забути завершення
- Для того щоб застосунок міг звертатись до мережі, йому потрібні відповідні
    дозволи. Без них виняток (Permission denied (missing INTERNET permission?))
    Дозволи зазначаються в маніфесті
    <uses-permission android:name="android.permission.INTERNET"/>
- Необхідність запуску мережних запитів у окремих потоках часто призводить до
    того, що з них обмежено доступ до елементів UI
    (Only the original thread that created a view hierarchy can touch its views. Expected: main Calling: pool-3-thread-1)
    Перехід до UI потоку здійснюється або викликом runOnUiThread, або переходом
    до синхронного режиму
 */