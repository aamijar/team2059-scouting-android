package com.team2059.scouting;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.util.ArrayList;
import android.content.Context;
import android.util.Log;

public class FileManager
{
    public static void writeFile(ArrayList<String> stats) throws IOException
    {
        PrintWriter writer = new PrintWriter("sample.txt", "UTF-8");

        for(int i = 0; i < stats.size(); i ++)
        {
            writer.println(stats.get(i));
        }

        writer.close();
    }

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
    public static void writeToFile(ArrayList<String> stats, Context context)
    {
        try
        {
            OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            for(String s : stats)
            {
                writer.write(s);
            }
            writer.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
