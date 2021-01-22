package br.com.estudo.beberaguaff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

// Classe MainActivity extendendo a classe AppCompatActivity. A classe AppCompatActivity, é uma extensão
// Para fazer com que os celulares mais antigos sejam compativeis!
public class MainActivity extends AppCompatActivity {

    //Declarando as Constantes das chaves para o banco de dados
     private final String KEY_NOTIFY = "key_notify";
     private final String KEY_INTERVAL = "key_interval";
     private final String KEY_HOUR = "key_hour";
     private final String KEY_MINUTE = "key_minute";

    //Declarando as variáveis para ter acesso no findView
    private TimePicker timePicker;
    private Button btnNotify;
    private EditText editMinutes;

    //Declarando as variáveis de intervalo, hora, minuto e ativado
    private int interval;
    private int hour;
    private int minute;
    private boolean activated;

    // Uso SharedPreferences para guardar chaves e valores em um banco de dados local
    private SharedPreferences storage;

    //Metodo onCreate é um setUP de inicialização que contém tudo o que vou precisar na activity
    // aqui que abrimos uma conexão com banco de dados,
    // (NO EXEMPLO DESTE APP BEBER ÁGUA) Utilizado para "setar" e configurar os componentes que vou usar e escutar eventos de touch na tela
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Pegando as referências dos componentes que defini na Activity_layout: Horas, botão e editor de texto e
        //Passando para as variáveis que eu criei nesta MainAcitivity
        timePicker = findViewById(R.id.time_picker);
        btnNotify = findViewById(R.id.button_notify);
        editMinutes = findViewById(R.id.edit_number_interval);
        //Depois de pegar as referências eu consigo fazer as configurações de cada um


        //Gerando/Definindo um BANCO DE DADOS LOCAL no meu APP (Para salvar o estado da tela, pra que quando eu fechar o app, ele abra novamete de onde eu parei)
        //Este banco de dados é utilizado para salvar valores menores, como numeros, booleans etc...
        //Toda activity tem a referencia para o SharedPreferences          contexto de visibilidade privado (Para que nenhum outro app tenha acesso aos meus dados)
        storage = getSharedPreferences("storage", MODE_PRIVATE);
        //Buscando os dados                  marcando como desativado, pq se for a primeira vez que estou executando o app, eu ainda nao vou ter apertado nenhum botão
        activated = storage.getBoolean(KEY_NOTIFY, false);
        //Carregando os dados no seu último estado salvo
        setUpInterfaceGrafica(storage, activated);

        //Modificando o horario de americano para o formato de 24Horas
        timePicker.setIs24HourView(true);

        //Atribuindo o método de Escutar Eventos (Listener) de click, para a variável que eu criei "btnNotify"
        btnNotify.setOnClickListener(notifyListener);
    }

    private void setUpInterfaceGrafica(SharedPreferences storage, boolean activated){

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if(activated){
            btnNotify.setText(R.string.pause);
            btnNotify.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_dark));
            //String.valueOf é para pegar o número da hora que defini no app e transformar em String de volta | Usando storage.getInt para pegar o valor através da chave!!!!!
            editMinutes.setText(String.valueOf(storage.getInt(KEY_INTERVAL, interval)));
            timePicker.setCurrentHour(storage.getInt(KEY_HOUR,timePicker.getCurrentHour()));
            timePicker.setCurrentMinute(storage.getInt(KEY_MINUTE,timePicker.getCurrentMinute()));
        } else {
            btnNotify.setText(R.string.notify);
            btnNotify.setBackgroundResource(R.drawable.bg_button_background);
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
            editMinutes.setText("");
        }
    }

    private void updateStorage(boolean activated, int interval, int hour, int minute){
        //Este método vai adicionar um editor que serve para adicionar os métodos put
        SharedPreferences.Editor edit = storage.edit();

        if(activated) {

            //O metodo put é responsável por atribuir em uma determinada chave, algum valor! ou para remover os estados salvos tmb
            // Salvando a notificação / preciso definir a KEY_NOTIFY como uma CONSTANTE (com valor inalteravel) Para fazer isso selecione com ALT+ENTER. Fazer isso para os demais também!
            edit.putBoolean(KEY_NOTIFY, true);
            edit.putInt(KEY_INTERVAL, interval);
            edit.putInt(KEY_HOUR, hour);
            edit.putInt(KEY_MINUTE, minute);
            //Agora para realmente efetivar a gravação dos dados eu preciso dar um edit.apply();
        } else {
            // Removendo a notificação!
            edit.putBoolean(KEY_NOTIFY, false);
            edit.remove(KEY_INTERVAL);
            edit.remove(KEY_HOUR);
            edit.remove(KEY_MINUTE);
        }
        edit.apply();
    }

    private void alert(int resId){
        Toast.makeText(MainActivity.this, resId, Toast.LENGTH_LONG).show();
    }

    private boolean intervalIsValid(){
        // Pegando o intervalo digitado no celular e armazenando na variável sInterval
        String sInterval = editMinutes.getText().toString();

        //Verificando se o usuário digitou algo
        if (sInterval.isEmpty()) {
            //Utilizo o Toast, para exibir uma mensagem na tela
            alert(R.string.validation);
            return false;
        }

        //Verificando se o usuário digitou algo
        if (sInterval.equals("0")) {
            //Utilizo o Toast, para exibir uma mensagem na tela
            alert(R.string.zero_value);
            return false;
        }

        return true;
    }

    private View.OnClickListener notifyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Se o botão não estiver ativado... (activated = false)
            if(!activated) {

                if(!intervalIsValid()) return;

                // Convertendo a string interval para receber um valor int.
                // Variavel hour recebendo o valor das horas que o usuário definiu no timepicker
                // Variavel minute recebendo o valor dos minutos
                interval = Integer.parseInt(editMinutes.getText().toString());
                hour = timePicker.getCurrentHour();
                minute = timePicker.getCurrentMinute();

                //Pegar o intervalo e imprimir no 'Log.d' (Log.Debug), só pra ver se a ação de clicar no botão esta funcionando (para ver se esta funcionando, abra o logcat e pesquise por "Teste")
                //Usando String.format para para pegar o valor que o usuário definiu no app, no caso no app, o usuário coloca
                // numeros, mais o app converte automaticamente para uma string, pois trata-se de um campo de texto, por isso é necessário a convesão dos dados
                // então eu uso o String.format, para transformar a string de volta um numero Double.
                Log.d("Teste", String.format("%d, %d, %d", interval, hour, minute));

                // .
                // .
                // .

                //Configurando o botão para que ele mude para 'Pausar', quando o usuário clicar
                //Assim eu consigo informar o usuário de que o botão está funcionando!
                setUpInterfaceGrafica(storage, true);
                updateStorage(true, interval, hour, minute);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                //Pegando o Alarme
                Intent notificationIntent = new Intent(MainActivity.this, NotificationPublisher.class);
                notificationIntent.putExtra(NotificationPublisher.KEY_NOTIFICATION_ID, 1);
                notificationIntent.putExtra(NotificationPublisher.KEY_NOTIFICATION, "Hora de beber água!");

                alert(R.string.app_name);

                PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                // Tem que fazer um cast, pois pode vir qualquer tipo de sistema
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if(alarmManager == null) return;
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval * 1000, broadcast);

                //Agora vou marcar que as alterações foram feitas indicando que o botão foi ativado! (activated = true)
                activated = true;
            }

            //Senão se o botão já estiver ativado (activated = true)
            else {

                //Inverto as configurações do botão, para deixar ele como era antes!!
                setUpInterfaceGrafica(storage, false);
                updateStorage(false, 0, 0, 0);

                Intent notificationIntent = new Intent(MainActivity.this, NotificationPublisher.class);
                PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this, 0, notificationIntent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if(alarmManager == null) return;
                alarmManager.cancel(broadcast);

                activated = false;
            }
        }
    };
}