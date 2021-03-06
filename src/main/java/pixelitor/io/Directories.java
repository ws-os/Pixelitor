/*
 * Copyright 2017 Laszlo Balazs-Csiki
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor. If not, see <http://www.gnu.org/licenses/>.
 */

package pixelitor.io;

import pixelitor.utils.AppPreferences;

import java.io.File;

public class Directories {
    private static File lastOpenDir = AppPreferences.loadLastOpenDir();
    private static File lastSaveDir = AppPreferences.loadLastSaveDir();

    public static File getLastOpenDir() {
        return lastOpenDir;
    }

    public static File getLastSaveDir() {
        return lastSaveDir;
    }

    public static String getLastOpenDirPath() {
        return lastOpenDir.getAbsolutePath();
    }

    public static String getLastSaveDirPath() {
        return lastSaveDir.getAbsolutePath();
    }

    public static void setLastOpenDir(File newOpenDir) {
        assert newOpenDir.exists() && newOpenDir.isDirectory();
        lastOpenDir = newOpenDir;
    }

    public static void setLastSaveDir(File newSaveDir) {
        assert newSaveDir.exists() && newSaveDir.isDirectory();
        lastSaveDir = newSaveDir;
    }

}

