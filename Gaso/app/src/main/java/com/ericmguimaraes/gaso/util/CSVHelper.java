/*
 *     Gaso
 *
 *     Copyright (C) 2016  Eric Guimarães
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ericmguimaraes.gaso.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import au.com.bytecode.opencsv.ResultSetHelper;

/**
 * Created by eric on 04/09/16.
 */
public abstract class CSVHelper {

    public static final String GASO_DIR = "gaso";

    public static void createCSV(String fileName, List<String[]> data) throws IOException {
        if(!isExternalStorageWritable())
            throw new IOException("External Storage Is Not Writable");

        File baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

//        String filePath = baseDir + File.separator + GASO_DIR + File.separator + fileName;

        Log.d("FILE_PATH",baseDir+fileName);

        File f = new File(baseDir,fileName);
        CSVWriter writer=null;
        try {
            if (f.exists() && !f.isDirectory()) {
                FileWriter mFileWriter = new FileWriter(f, false);
                writer = new CSVWriter(mFileWriter);
            } else {
                FileWriter fw = new FileWriter(f);
                writer = new CSVWriter(fw);
            }
            writer.writeAll(data);
        } finally {
            if(writer!=null)
                writer.close();
        }
    }


    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


}
