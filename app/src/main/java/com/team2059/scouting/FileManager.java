/**
 * class to organize read and write
 * functions of the app to internal storage
 * in dir data/data/app_name
 *
 * @author Anupam
 */

package com.team2059.scouting;


import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

import java.util.ArrayList;
import java.util.Scanner;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


public class FileManager
{
    //not in use, works on desktop os
    public static void writeFile(ArrayList<String> stats) throws IOException
    {
        PrintWriter writer = new PrintWriter("sample.txt", "UTF-8");

        for(int i = 0; i < stats.size(); i ++)
        {
            writer.println(stats.get(i));
        }

        writer.close();
    }
    //not in use, works on desktop os
    public static ArrayList<String> readFile(String path) throws IOException
    {
        File sampleFile = new File(path);

        Scanner sc = new Scanner(sampleFile);
        ArrayList<String> strList = new ArrayList<String>();
        while(sc.hasNextLine())
        {
            strList.add(sc.nextLine());
        }
        sc.close();
        return strList;
    }

    /**
     *
     * @param fileName saved name of the file
     * @param stats retrieved scout sheet entries
     * @param context context of application
     */

    public static void writeToFile(String fileName, ArrayList<String> stats, Context context)
    {
        try
        {

            OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            for(String s : stats)
            {
                writer.write(s);
            }
            Toast.makeText(context, "Save Successful", Toast.LENGTH_LONG).show();
            writer.close();

        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
            Toast.makeText(context, "Unable to Save", Toast.LENGTH_LONG).show();
        }
    }

}

