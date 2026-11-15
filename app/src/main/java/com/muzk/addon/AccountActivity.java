package com.muzk.addon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.File;

/* JADX INFO: loaded from: classes13.dex */
public class AccountActivity extends Activity {
    private Button backup;
    private Button login;
    private Button logout;
    private Button restore;
    private TextView status;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(build());
        refresh();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private View build() {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout linearLayoutColumn = Ui.column(this);
        linearLayoutColumn.setGravity(1);
        scrollView.addView(linearLayoutColumn);
        TextView textViewText = Ui.text(this, "Conta e backup", 22.0f, true);
        textViewText.setGravity(17);
        linearLayoutColumn.addView(textViewText);
        TextView textViewText2 = Ui.text(this, "", 14.0f, false);
        this.status = textViewText2;
        textViewText2.setGravity(17);
        this.status.setPadding(0, Ui.dp(this, 12.0f), 0, Ui.dp(this, 20.0f));
        linearLayoutColumn.addView(this.status);
        Button button = Ui.button(this, "Entrar com Google");
        this.login = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.muzk.addon.AccountActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AccountActivity.this.startActivity(new Intent(AccountActivity.this, (Class<?>) CloudAuthActivity.class));
            }
        });
        linearLayoutColumn.addView(this.login, wide());
        Button button2 = Ui.button(this, "Fazer backup no Drive");
        this.backup = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.muzk.addon.AccountActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AccountActivity.this.doBackup();
            }
        });
        linearLayoutColumn.addView(this.backup, wide());
        Button button3 = Ui.button(this, "Restaurar do Drive");
        this.restore = button3;
        button3.setOnClickListener(new View.OnClickListener() { // from class: com.muzk.addon.AccountActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AccountActivity.this.confirmRestore();
            }
        });
        linearLayoutColumn.addView(this.restore, wide());
        Button button4 = Ui.button(this, "Sair");
        this.logout = button4;
        button4.setOnClickListener(new View.OnClickListener() { // from class: com.muzk.addon.AccountActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) throws Throwable {
                Cloud.logout(AccountActivity.this);
                AccountActivity.this.refresh();
                Ui.toast(AccountActivity.this, "Sessão encerrada.");
            }
        });
        linearLayoutColumn.addView(this.logout, wide());
        TextView textViewText3 = Ui.text(this, "O backup guarda playlists, favoritos e ajustes em uma pasta privada do seu Drive.", 12.0f, false);
        textViewText3.setGravity(17);
        textViewText3.setPadding(0, Ui.dp(this, 24.0f), 0, 0);
        linearLayoutColumn.addView(textViewText3);
        Ui.applyInsets(scrollView);
        return scrollView;
    }

    private LinearLayout.LayoutParams wide() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.topMargin = Ui.dp(this, 8.0f);
        return layoutParams;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refresh() {
        String string;
        String str;
        boolean zHasRefresh = new TokenStore(this).hasRefresh();
        String strEmail = new TokenStore(this).email();
        TextView textView = this.status;
        if (zHasRefresh) {
            StringBuilder sb = new StringBuilder("Conectado");
            if (strEmail != null) {
                str = " como " + strEmail;
            } else {
                str = "";
            }
            sb.append(str);
            string = sb.toString();
        } else {
            string = "Não conectado";
        }
        textView.setText(string);
        this.login.setEnabled(!zHasRefresh);
        this.logout.setEnabled(zHasRefresh);
        this.backup.setEnabled(zHasRefresh);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doBackup() {
        this.status.setText("Preparando backup...");
        new Thread(new Runnable() { // from class: com.muzk.addon.AccountActivity.5
            @Override // java.lang.Runnable
            public void run() {
                try {
                    File fileCreate = Backup.create(AccountActivity.this);
                    long length = fileCreate.length() / 1024;
                    String str = Cloud.token(AccountActivity.this);
                    if (str == null) {
                        throw new Exception("sessão expirada, entre novamente");
                    }
                    DriveClient.upload(AccountActivity.this, str, fileCreate, DriveClient.find(AccountActivity.this, str, Config.BACKUP_NAME));
                    AccountActivity.this.ok("Backup enviado (" + length + " KB).");
                } catch (Exception e) {
                    AccountActivity.this.fail("Falha no backup: " + e.getMessage());
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void confirmRestore() {
        new AlertDialog.Builder(this).setTitle("Restaurar do Drive").setMessage("Os dados atuais de playlists e ajustes serão substituídos. Continuar?").setNegativeButton("Cancelar", (DialogInterface.OnClickListener) null).setPositiveButton("Restaurar", new DialogInterface.OnClickListener() { // from class: com.muzk.addon.AccountActivity.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                AccountActivity.this.doRestore();
            }
        }).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doRestore() {
        this.status.setText("Baixando backup...");
        new Thread(new Runnable() { // from class: com.muzk.addon.AccountActivity.7
            @Override // java.lang.Runnable
            public void run() {
                try {
                    String str = Cloud.token(AccountActivity.this);
                    if (str == null) {
                        throw new Exception("sessão expirada, entre novamente");
                    }
                    String strFind = DriveClient.find(AccountActivity.this, str, Config.BACKUP_NAME);
                    if (strFind == null) {
                        throw new Exception("nenhum backup encontrado");
                    }
                    int iRestore = Backup.restore(AccountActivity.this, DriveClient.download(str, strFind));
                    AccountActivity.this.ok(iRestore + " arquivos restaurados. Feche e abra o app.");
                } catch (Exception e) {
                    AccountActivity.this.fail("Falha ao restaurar: " + e.getMessage());
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ok(final String str) {
        runOnUiThread(new Runnable() { // from class: com.muzk.addon.AccountActivity.8
            @Override // java.lang.Runnable
            public void run() {
                AccountActivity.this.status.setText(str);
                Ui.toast(AccountActivity.this, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fail(final String str) {
        runOnUiThread(new Runnable() { // from class: com.muzk.addon.AccountActivity.9
            @Override // java.lang.Runnable
            public void run() {
                AccountActivity.this.status.setText(str);
                Ui.toast(AccountActivity.this, str);
            }
        });
    }
}
