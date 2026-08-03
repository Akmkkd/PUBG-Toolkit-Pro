package com.hackerai.pubgtoolkit;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.DataOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private CheckBox checkScript1, checkScript2;
    private Button btnRunSelected, btnRunAll;
    private TextView logText, rootStatusText;
    private ProgressBar progressBar;
    private boolean hasRoot = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private StringBuilder logBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        checkRootAccess();
        setupClickListeners();
    }

    private void initViews() {
        checkScript1 = findViewById(R.id.checkScript1);
        checkScript2 = findViewById(R.id.checkScript2);
        btnRunSelected = findViewById(R.id.btnRunSelected);
        btnRunAll = findViewById(R.id.btnRunAll);
        logText = findViewById(R.id.logText);
        rootStatusText = findViewById(R.id.rootStatusText);
        progressBar = findViewById(R.id.progressBar);
    }

    private void checkRootAccess() {
        new Thread(() -> {
            hasRoot = canRunRootCommands();
            handler.post(() -> {
                if (hasRoot) {
                    rootStatusText.setText("✓ GRANTED");
                    rootStatusText.setTextColor(0xFF00FF88);
                    addLog("[Root] Root access granted successfully");
                } else {
                    rootStatusText.setText("✗ DENIED");
                    rootStatusText.setTextColor(0xFFE94560);
                    addLog("[Root] Root access denied - device not rooted or su not found");
                    showRootAlert();
                }
            });
        }).start();
    }

    private boolean canRunRootCommands() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("echo root_test\n");
            os.writeBytes("exit\n");
            os.flush();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        } finally {
            try { if (os != null) os.close(); } catch (IOException ignored) {}
            if (process != null) process.destroy();
        }
    }

    private void showRootAlert() {
        new AlertDialog.Builder(this)
                .setTitle("ROOT REQUIRED")
                .setMessage("This application requires root access to function properly.\n\nPlease ensure your device is rooted and SuperSU/Magisk is installed.")
                .setPositiveButton("OK", (d, w) -> {})
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setupClickListeners() {
        btnRunSelected.setOnClickListener(v -> runSelectedScripts());
        btnRunAll.setOnClickListener(v -> runAllScripts());
    }

    private void runSelectedScripts() {
        if (!hasRoot) {
            addLog("[Error] Root access is required!");
            Toast.makeText(this, "Root access required!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean s1 = checkScript1.isChecked();
        boolean s2 = checkScript2.isChecked();

        if (!s1 && !s2) {
            addLog("[Warning] No scripts selected");
            Toast.makeText(this, "Select at least one script", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            if (s1) executeScript1();
            if (s2) executeScript2();
            handler.post(() -> {
                progressBar.setVisibility(ProgressBar.GONE);
                addLog("[System] Selected scripts completed");
                Toast.makeText(MainActivity.this, "Completed!", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void runAllScripts() {
        if (!hasRoot) {
            addLog("[Error] Root access is required!");
            Toast.makeText(this, "Root access required!", Toast.LENGTH_SHORT).show();
            return;
        }

        checkScript1.setChecked(true);
        checkScript2.setChecked(true);

        new Thread(() -> {
            executeScript1();
            executeScript2();
            handler.post(() -> {
                progressBar.setVisibility(ProgressBar.GONE);
                addLog("[System] All scripts executed successfully");
                Toast.makeText(MainActivity.this, "All scripts completed!", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void executeScript1() {
        handler.post(() -> {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressBar.setProgress(10);
            addLog("[Script 1] Starting Full Clean...");
        });

        String[] commands = {
                "am force-stop com.tencent.ig",
                "am force-stop com.pubg.krmobile",
                "am force-stop com.rekoo.pubgm",
                "am force-stop com.vng.pubgmobile",
                "iptables -F",
                "iptables -X",
                "ip6tables --flush",
                "ip6tables -F",
                "rm -rf /cache/magisk.log*",
                "rm -rf /cache/mqsas",
                "rm -rf /cache/lost+found",
                "rm -rf /cache/backup*",
                "rm -rf /cache/recovery",
                "rm -rf /cache/p*",
                "rm -rf /cache/f*",
                "rm -rf /storage/emulated/legacy",
                "rm -rf /data/system/package_cache",
                "rm -rf /data/system/graphicsstats",
                "rm -rf /sbin/.magisk/mirror/data/system_ce/0/launch_params/com.pubg.krmobile_com.epicgames.ue4.SplashActivity.xml",
                "rm -rf /sbin/.magisk/mirror/data/system_ce/0/launch_params/com.rekoo.pubgm_com.epicgames.ue4.SplashActivity.xml",
                "rm -rf /sbin/.magisk/mirror/data/system_ce/0/launch_params/com.tencent.ig_com.epicgames.ue4.SplashActivity.xml",
                "rm -rf /sbin/.magisk/mirror/data/system_ce/0/launch_params/com.vng.pubgmobile_com.epicgames.ue4.SplashActivity.xml",
                "rm -f /storage/emulated/0/tencent",
                "touch /storage/emulated/0/tencent",
                "rm -rf /storage/emulated/0/.backups",
                "rm -rf /storage/emulated/0/TWRP",
                "rm -rf /storage/emulated/0/Android/data/com.pubg.krmobile",
                "rm -rf /storage/emulated/0/Android/data/com.rekoo.pubgm",
                "rm -rf /storage/emulated/0/Android/data/com.tencent.ig",
                "rm -rf /storage/emulated/0/Android/data/com.vng.pubgmobile",
                "rm -rf /data/data/com.pubg.krmobile",
                "rm -rf /data/data/com.rekoo.pubgm",
                "rm -rf /data/data/com.tencent.ig",
                "rm -rf /data/data/com.vng.pubgmobile",
                "rm -rf /data/user_de/0/com.pubg.krmobile",
                "rm -rf /data/user_de/0/com.rekoo.pubgm",
                "rm -rf /data/user_de/0/com.tencent.ig",
                "rm -rf /data/user_de/0/com.vng.pubgmobile",
                "rm -rf /data/misc/profiles/cur/0/com.pubg.krmobile",
                "rm -rf /data/misc/profiles/cur/0/com.rekoo.pubgm",
                "rm -rf /data/misc/profiles/cur/0/com.tencent.ig",
                "rm -rf /data/misc/profiles/cur/0/com.vng.pubgmobile",
                "rm -rf /data/misc/profiles/ref/com.pubg.krmobile",
                "rm -rf /data/misc/profiles/ref/com.rekoo.pubgm",
                "rm -rf /data/misc/profiles/ref/com.tencent.ig",
                "rm -rf /data/misc/profiles/ref/com.vng.pubgmobile",
                "rm -rf /data/app/preinstall_history",
                "rm -rf /data/system/users/0/settings_ssaid.xml*"
        };

        runRootCommands(commands, 80, "[Script 1]");

        handler.post(() -> {
            progressBar.setProgress(90);
            addLog("[Script 1] Full Clean completed successfully");
        });

        // Reboot prompt
        handler.post(() -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Reboot Required")
                    .setMessage("Script 1 completed. Reboot device now for full effect?")
                    .setPositiveButton("REBOOT", (d, w) -> runRootCommands(new String[]{"reboot"}, 100, "[Reboot]"))
                    .setNegativeButton("Later", (d, w) -> {})
                    .show();
        });
    }

    private void executeScript2() {
        handler.post(() -> {
            addLog("[Script 2] Starting Guest Account Reset...");
        });

        String[] commands = {
                "kill com.tencent.ig",
                "rm -rf /data/data/com.tencent.ig/shared_prefs",
                "mkdir /data/data/com.tencent.ig/shared_prefs",
                "chmod 777 /data/data/com.tencent.ig/shared_prefs",
                "rm -rf /data/data/com.tencent.ig/files",
                "rm -rf /data/data/com.tencent.ig/databases",
                "rm -f /data/media/0/Android/data/com.tencent.ig/files/login-identifier.txt",
                "rm -rf /data/media/0/Android/data/com.tencent.ig/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Intermediate",
                "touch /data/media/0/Android/data/com.tencent.ig/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Intermediate",
                "rm -rf /data/media/0/Android/data/com.tencent.ig/files/TGPA",
                "touch /data/media/0/Android/data/com.tencent.ig/files/TGPA",
                "rm -rf /data/media/0/Android/data/com.tencent.ig/files/ProgramBinaryCache",
                "touch /data/media/0/Android/data/com.tencent.ig/files/ProgramBinaryCache",
                "iptables -I OUTPUT -d cloud.vmp.onezapp.com -j REJECT",
                "iptables -I INPUT -s cloud.vmp.onezapp.com -j REJECT"
        };

        runRootCommands(commands, 70, "[Script 2]");

        // Create new device_id.xml with random UUID
        int r1 = (int)(Math.random() * 90000) + 10000;
        int r2 = (int)(Math.random() * 90000) + 10000;
        int r3 = (int)(Math.random() * 90000) + 10000;
        int r4 = (int)(Math.random() * 90000) + 10000;
        int r5 = (int)(Math.random() * 90000) + 10000;
        int r6 = (int)(Math.random() * 90000) + 10000;
        int r7 = (int)(Math.random() * 90000) + 10000;

        String uuid = r1 + "-" + r2 + "-" + r3 + "-" + r4 + "-" + r5 + r6 + r7;

        String[] uuidCommands = {
                "echo \"<?xml version='1.0' encoding='utf-8' standalone='yes' ?>\" > /data/data/com.tencent.ig/shared_prefs/device_id.xml",
                "echo \"<map>\" >> /data/data/com.tencent.ig/shared_prefs/device_id.xml",
                "echo \"    <string name=\\\"random\\\"></string>\" >> /data/data/com.tencent.ig/shared_prefs/device_id.xml",
                "echo \"    <string name=\\\"install\\\"></string>\" >> /data/data/com.tencent.ig/shared_prefs/device_id.xml",
                "echo \"    <string name=\\\"uuid\\\">" + uuid + "</string>\" >> /data/data/com.tencent.ig/shared_prefs/device_id.xml",
                "echo \"</map>\" >> /data/data/com.tencent.ig/shared_prefs/device_id.xml"
        };

        runRootCommands(uuidCommands, 95, "[Script 2]");

        handler.post(() -> {
            progressBar.setProgress(100);
            addLog("[Script 2] Guest account reset completed");
            addLog("[Script 2] New UUID generated: " + uuid);
        });
    }

    private void runRootCommands(String[] commands, int maxProgress, String tag) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());

            for (int i = 0; i < commands.length; i++) {
                final String cmd = commands[i];
                final int progress = (int) ((double) (i + 1) / commands.length * maxProgress);

                handler.post(() -> addLog(tag + " $ " + cmd));

                os.writeBytes(cmd + "\n");
                os.flush();

                final int finalProgress = progress;
                handler.post(() -> progressBar.setProgress(finalProgress));

                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {}
            }

            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();

            handler.post(() -> addLog(tag + " ✓ Commands executed (exit code: " + process.exitValue() + ")"));
        } catch (Exception e) {
            handler.post(() -> addLog(tag + " ✗ Error: " + e.getMessage()));
        } finally {
            try {
                if (os != null) os.close();
            } catch (IOException ignored) {}
            if (process != null) process.destroy();
        }
    }

    private void addLog(String message) {
        handler.post(() -> {
            if (logBuilder.length() > 5000) {
                logBuilder = new StringBuilder(logBuilder.substring(logBuilder.length() - 3000));
            }
            logBuilder.append("\n").append(message);
            logText.setText(logBuilder.toString());
            // Auto scroll to bottom
            final int scrollAmount = logText.getLayout().getLineTop(logText.getLineCount()) - logText.getHeight();
            if (scrollAmount > 0) {
                logText.scrollTo(0, scrollAmount + 50);
            } else {
                logText.scrollTo(0, 0);
            }
        });
    }
}
