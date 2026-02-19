package com.github.arthurdeka.cedromoderndock.util;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;
import com.sun.jna.ptr.IntByReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NativeWindowUtils {

    public record WindowInfo(HWND hwnd, String title) {}

    public static List<WindowInfo> getAppWindows(String targetExePath) {
        List<WindowInfo> result = new ArrayList<>();

        // Safety check for non-Windows environments (development)
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            return result;
        }

        if (targetExePath == null || targetExePath.isEmpty()) {
            return result;
        }

        final User32 user32 = User32.INSTANCE;
        user32.EnumWindows(new WNDENUMPROC() {
            @Override
            public boolean callback(HWND hWnd, Pointer arg1) {
                if (user32.IsWindowVisible(hWnd)) {
                    char[] buffer = new char[1024];
                    user32.GetWindowText(hWnd, buffer, 1024);
                    String title = Native.toString(buffer);

                    if (title.isEmpty()) {
                        return true;
                    }

                    IntByReference processId = new IntByReference();
                    user32.GetWindowThreadProcessId(hWnd, processId);

                    Optional<ProcessHandle> handle = ProcessHandle.of(processId.getValue());
                    if (handle.isPresent()) {
                         ProcessHandle.Info info = handle.get().info();
                         if (info.command().isPresent()) {
                             String command = info.command().get();
                             if (pathsMatch(command, targetExePath)) {
                                 result.add(new WindowInfo(hWnd, title));
                             }
                         }
                    }
                }
                return true;
            }
        }, null);
        return result;
    }

    private static boolean pathsMatch(String path1, String path2) {
        if (path1 == null || path2 == null) return false;
        // Normalize separators
        String p1 = path1.replace("\\", "/");
        String p2 = path2.replace("\\", "/");
        return p1.equalsIgnoreCase(p2);
    }

    public static void activateWindow(HWND hwnd) {
        // Safety check for non-Windows environments
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            return;
        }

        User32 user32 = User32.INSTANCE;
        // Restore if minimized, otherwise just show/activate
        user32.ShowWindow(hwnd, WinUser.SW_RESTORE);
        user32.SetForegroundWindow(hwnd);
    }
}
