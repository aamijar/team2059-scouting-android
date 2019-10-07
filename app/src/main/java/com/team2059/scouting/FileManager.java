package com.team2059.scouting;
import java.util.Scanner;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.util.ArrayList;

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

}
