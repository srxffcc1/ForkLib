package veg.mediaplayer.sdk;

import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

class SystemUtils {
    private static final String TAG = "LibLoader";
    private static PlayerHardwareInfoCPU cpu = PlayerHardwareInfoCPU.ARM;
    private static PlayerHardwareInfoCPUCapabilities cpucaps = PlayerHardwareInfoCPUCapabilities.None;
    private static boolean isLoaded = false;

    private enum PlayerHardwareInfoCPU {
        ARM,
        ARMV7,
        x86
    }

    private enum PlayerHardwareInfoCPUCapabilities {
        None,
        Neon
    }

    public static class WaitNotify {
        private Object obj = new Object();
        private boolean wasSignalled = false;

        public Object getObject() {
            return this.obj;
        }

        public boolean wait(String text) {
            boolean ret;
            synchronized (this.obj) {
                ret = true;
                while (!this.wasSignalled) {
                    try {
                        this.obj.wait();
                        ret = true;
                    } catch (InterruptedException e) {
                    }
                }
                this.wasSignalled = false;
            }
            return ret;
        }

        protected void notify(String text) {
            synchronized (this.obj) {
                this.wasSignalled = true;
                this.obj.notifyAll();
            }
        }
    }

    SystemUtils() {
    }

    public static int getNumCores() {
        try {
            return new File("/sys/devices/system/cpu/").listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    if (Pattern.matches("cpu[0-9]+", pathname.getName())) {
                        return true;
                    }
                    return false;
                }
            }).length;
        } catch (Exception e) {
            return 1;
        }
    }

    public static float getCPUBogoMIPS() {
        String temp = "";
        float bogoMIPS_ = 0.0f;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/cpuinfo"), 8192);
            while (true) {
                temp = bufferedReader.readLine();
                if (temp == null) {
                    break;
                }
                String[] parts = temp.toLowerCase().split(":");
                if (!(parts == null || parts.length != 2 || parts[0] == null || parts[1] == null || !parts[0].contains("bogomips"))) {
                    try {
                        bogoMIPS_ += Float.parseFloat(parts[1].toString());
                    } catch (NumberFormatException e) {
                        bogoMIPS_ = 0.0f;
                    }
                }
            }
            bufferedReader.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return bogoMIPS_;
    }

    public static synchronized void loadLibs() {
        synchronized (SystemUtils.class) {
            if (!isLoaded) {
                getCPUInfo();
                Log.v(TAG, "Load library CPU1: " + getNumCores() + " Build.CPU_ABI:" + Build.CPU_ABI + " Build.CPU_ABI:" + false);
                if (Build.CPU_ABI.startsWith("x86")) {
                    try {
                        System.loadLibrary("SDL2-x86");
                        System.loadLibrary("rtstm-x86");
                        System.loadLibrary("yuv_shared-x86");
                        System.loadLibrary("rtspplr-x86");
                        Log.v(TAG, "1. Load library for x86");
                    } catch (UnsatisfiedLinkError e) {
                        System.err.println("Native code library failed to load: " + e + "\n");
                    }
                } else if (Build.CPU_ABI.startsWith("armeabi-v7") && cpucaps == PlayerHardwareInfoCPUCapabilities.Neon) {
                    boolean load = false;
                    try {
                        System.loadLibrary("SDL2-armeabi-v7a");
                        System.loadLibrary("rtstm-armeabi-v7a");
                        System.loadLibrary("yuv_shared-armeabi-v7a");
                        System.loadLibrary("rtspplr-armeabi-v7a");
                        Log.v(TAG, "2. Load library for armeabi-v7a");
                        load = true;
                    } catch (UnsatisfiedLinkError e2) {
                        System.err.println("Native code library failed to load: " + e2 + "\n");
                    }
                    if (!load) {
                        System.loadLibrary("SDL2-armeabi");
                        System.loadLibrary("rtstm-armeabi");
                        System.loadLibrary("yuv_shared-armeabi");
                        System.loadLibrary("rtspplr-armeabi");
                        Log.v(TAG, "4. Load library for armeabi");
                    }
                } else if (Build.CPU_ABI.startsWith("armeabi-v7") && cpucaps == PlayerHardwareInfoCPUCapabilities.None) {
                    boolean load = false;
                    try {
                        System.loadLibrary("SDL2-armeabi-v7a-noneon");
                        System.loadLibrary("yuv_shared-armeabi-v7a-noneon");
                        System.loadLibrary("rtspplr-armeabi-v7a-noneon");
                        Log.v(TAG, "3. Load library for armeabi-v7a-noneon");
                        load = true;
                    } catch (UnsatisfiedLinkError e22) {
                        System.err.println("Native code library failed to load: " + e22 + "\n");
                    }
                    try {
                        System.loadLibrary("SDL2-armeabi-v7a");
                        System.loadLibrary("rtstm-armeabi-v7a");
                        System.loadLibrary("yuv_shared-armeabi-v7a");
                        System.loadLibrary("rtspplr-armeabi-v7a");
                        Log.v(TAG, "3. Load library for armeabi-v7a");
                        load = true;
                    } catch (UnsatisfiedLinkError e22) {
                        System.err.println("Native code library failed to load: " + e22 + "\n");
                    }
                    if (!load) {
                        System.loadLibrary("SDL2-armeabi");
                        System.loadLibrary("rtstm-armeabi");
                        System.loadLibrary("yuv_shared-armeabi");
                        System.loadLibrary("rtspplr-armeabi");
                        Log.v(TAG, "4. Load library for armeabi");
                    }
                } else {
                    if (Build.CPU_ABI.startsWith("arm64-v8a")) {
                        Log.v(TAG, "Load library for armeabi");
                    } else {
                        Log.v(TAG, "Load library for armeabi");
                    }
                    try {
                        System.loadLibrary("SDL2-armeabi");
                        System.loadLibrary("rtstm-armeabi");
                        System.loadLibrary("yuv_shared-armeabi");
                        System.loadLibrary("rtspplr-armeabi");
                        Log.v(TAG, "5. Load library for armeabi");
                    } catch (UnsatisfiedLinkError e222) {
                        System.err.println("Native code library failed to load: " + e222 + "\n");
                    }
                }
                isLoaded = true;
            }
        }
    }

    private static void getCPUInfo() {
        String temp = "";
        String info = "";
        try {
            FileReader fileReader = new FileReader("/proc/cpuinfo");
            BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);
            while (true) {
                temp = bufferedReader.readLine();
                if (temp == null) {
                    break;
                }
                info = info + temp.toLowerCase();
            }
            if (info.isEmpty()) {
                fileReader.close();
                return;
            }
            cpu = PlayerHardwareInfoCPU.ARM;
            if (info.contains("armv7")) {
                cpu = PlayerHardwareInfoCPU.ARMV7;
            } else if (info.contains("intel") || info.contains("x86")) {
                cpu = PlayerHardwareInfoCPU.x86;
            }
            cpucaps = PlayerHardwareInfoCPUCapabilities.None;
            if (info.contains("neon")) {
                cpucaps = PlayerHardwareInfoCPUCapabilities.Neon;
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
