/**
 * class to organize read and write
 * functions of the app to internal storage
 * in dir data/data/app_name
 *
 * @author Anupam
 *
 */

package com.team2059.scouting;

import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import android.content.SharedPreferences;
import android.media.MediaScannerConnection;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.team2059.scouting.core.Match;
import org.team2059.scouting.core.Team;
import org.team2059.scouting.core.frc2020.IrMatch;
import org.team2059.scouting.core.frc2020.IrTeam;

public class FileManager
{
    private static final String TAG = "FileManager";
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
//    public static ArrayList<String> readFile(String path) throws IOException
//    {
//        File sampleFile = new File(path);
//
//        Scanner sc = new Scanner(sampleFile);
//        ArrayList<String> strList = new ArrayList<String>();
//        while(sc.hasNextLine())
//        {
//            strList.add(sc.nextLine());
//        }
//        sc.close();
//        return strList;
//    }

    /**
     *
     * @param fileName saved name of the file
     * @param stats retrieved scout sheet entries
     * @param context context of application
     */

    //txt method
    public static void writeToFile(String fileName, ArrayList<String> stats, Context context)
    {
        try
        {
            fileName = fileName + ".txt";
            OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));

            for(String s : stats)
            {
                writer.write(s + "\n");
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


    //txt method
    public static ArrayList<String> readFromFile(Context context)
    {
        ArrayList<String> data = new ArrayList<String>();

        try
        {
            String [] fileNameArr = context.getFilesDir().list();
            Toast.makeText(context, fileNameArr[3], Toast.LENGTH_LONG).show();
            InputStream inputStream = context.openFileInput("config.txt");

            if(inputStream != null)
            {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String recieveString = "";
                while((recieveString = bufferedReader.readLine()) != null)
                {
                    data.add(recieveString);
                }

                inputStream.close();

            }
        }
        catch(FileNotFoundException e)
        {
            Log.e("file reading" , "File not found: " + e.toString());
            Toast.makeText(context, "File not found!", Toast.LENGTH_LONG).show();
        }
        catch(IOException e)
        {
            Log.e("file reading", "File not read" + e.toString());
            Toast.makeText(context, "File cannot be read", Toast.LENGTH_LONG).show();
        }


        return data;


    }

    /**
     *
     * @param fileName name of file
     * @param match match object of attributes
     * @param context context of application
     * @throws IOException file not found
     */


    public static void writeToJsonFile(String fileName, IrMatch match, Context context) throws IOException
    {
//        JSONArray jsonArr = new JSONArray();
//        JSONObject matchObject = new JSONObject();
//        JSONObject matchDetails = new JSONObject();
//
//
//
//        try
//        {
//            matchDetails.put("teamName", match.getTeamName());
//            matchDetails.put("matchNumber", match.getMatchNumber());
//            matchDetails.put("teamPoints", match.getTeamPoints());
//            matchDetails.put("alliancePoints", match.getAlliancePoints());
//            matchDetails.put("rankPoints", match.getRankPoints());
//            matchDetails.put("matchResult", match.getMatchResult());
//
//            matchDetails.put("initLine", match.getAuto().getInitLine());
//            matchDetails.put("autoLowerAttempt", match.getAuto().getLowerAttempt());
//            matchDetails.put("autoLowerPort", match.getAuto().getLowerPort());
//            matchDetails.put("autoUpperAttempt", match.getAuto().getUpperAttempt());
//            matchDetails.put("autoOuterPort", match.getAuto().getOuterPort());
//            matchDetails.put("autoInnerPort", match.getAuto().getInnerPort());
//
//            matchDetails.put("lowerAttempt", match.getTeleop().getLowerAttempt());
//            matchDetails.put("lowerPort", match.getTeleop().getLowerPort());
//            matchDetails.put("upperAttempt", match.getTeleop().getUpperAttempt());
//            matchDetails.put("outerPort", match.getTeleop().getOuterPort());
//            matchDetails.put("innerPort", match.getTeleop().getInnerPort());
//            matchDetails.put("rotation", match.getTeleop().getControlPanel().getRotation());
//            matchDetails.put("position", match.getTeleop().getControlPanel().getPosition());
//
//            matchDetails.put("park", match.getEndgame().getPark());
//            matchDetails.put("climbAttempt", match.getEndgame().getClimbAttempt());
//            matchDetails.put("climb", match.getEndgame().getClimb());
//            matchDetails.put("level", match.getEndgame().getLevel());
//            matchDetails.put("notes", match.getNotes());
//
//            matchObject.put("matchObject", matchDetails);
//            jsonArr.put(matchObject);
//        }
//        catch(JSONException e)
//        {
//            Log.e("JSON write error", "failed to write desc: " + e.toString());
//            Toast.makeText(context, "JSON write failed!", Toast.LENGTH_LONG).show();
//        }

        //saves on internal storage (only visible in virtual device manager in Android Studio)
        //File jsonFile = new File(context.getFilesDir(), fileName);

        //saves to external storage (public, visible in device files on phone)
        File jsonFile = new File(context.getExternalFilesDir(null), fileName);



        if(!jsonFile.exists()) //make new file
        {
            FileWriter writer = new FileWriter(jsonFile);

            ArrayList<IrMatch> gsonArr = new ArrayList<IrMatch>();
            gsonArr.add(match);

            Gson gson = new Gson();
            String gsonStr = gson.toJson(gsonArr);

            writer.write(gsonStr); //JSON STRING ADDED

            Toast.makeText(context, "Successful Write JSON: " + fileName, Toast.LENGTH_LONG).show();

            writer.close();
        }
        else //update existing file
        {
            try
            {

                //Read from internal storage (private, only visible in android studio virtual explorer)
                //FileReader reader = new FileReader(context.getFilesDir().getAbsoluteFile() + "/" + fileName);

                //Read from external storage (public, visible in phone file explorer)
                FileReader reader = new FileReader(context.getExternalFilesDir(fileName));

                JSONParser jsonParser = new JSONParser();
                org.json.simple.JSONArray updateJsonArr = (org.json.simple.JSONArray) jsonParser.parse(reader);

                Gson gson = new Gson();

                Type irMatchType = new TypeToken<ArrayList<IrMatch>>(){}.getType();
                ArrayList<IrMatch> irMatchArr = gson.fromJson(updateJsonArr.toJSONString(), irMatchType);

                irMatchArr.add(match);
                String gsonStr = gson.toJson(irMatchArr);

                FileWriter writer = new FileWriter(jsonFile);
                writer.write(gsonStr); //updated JSON String written


                MediaScannerConnection.scanFile(context, new String[] {jsonFile.toString()}, null, null);

                writer.close();
                Toast.makeText(context, fileName + " updated successfully!", Toast.LENGTH_LONG).show();

            }
            catch(ParseException e)
            {
                Log.e("JSON parser error","parser desc: " + e.toString());
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();

            }

        }

    }

    public static boolean makeDir(String dirName, Context context){

        String root = context.getExternalFilesDir(null).getAbsolutePath();
        File compDir = new File(root + "/" + dirName);

        if(!compDir.exists()){
            return compDir.mkdirs();
        }
        return false;
    }

    public static ArrayList<String> getDirs(Context context){
        String root = context.getExternalFilesDir(null).getAbsolutePath();

        File [] files = new File(root).listFiles();

        ArrayList<String> names = new ArrayList<>();

        for(File file : files){


            Date date = new Date(file.lastModified());


            names.add(file.getName() + ", " + date.toString());
        }

        return names;
    }


    /**
     *
     * @param filepath path of file excluding root
     * @param context context of application
     * @return json formatted string, default null
     */


    // this function needs to be able to read from a competition directory as well,
    // while omitting checkpoints directory

    public static String readFile(String filepath, Context context){

        ArrayList<IrMatch> irMatchArr = new ArrayList<>();

        File file = new File(context.getExternalFilesDir(null), filepath);
        File [] files = file.listFiles();

        Gson gson = new Gson();

        if(files != null){

            ArrayList<File> filesList = getFileArray(files, new ArrayList<File>());
            for (File f : filesList){
                try{
                    Log.e(TAG, f.getName());
                    FileReader reader = new FileReader(f);

                    JSONParser jsonParser = new JSONParser();
                    org.json.simple.JSONArray updateJsonArr = (org.json.simple.JSONArray) jsonParser.parse(reader);

                    Type irMatchType = new TypeToken<ArrayList<IrMatch>>(){}.getType();
                    ArrayList<IrMatch> tmp = gson.fromJson(updateJsonArr.toJSONString(), irMatchType);
                    irMatchArr.addAll(tmp);
                }
                catch (ParseException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            Log.e(TAG, "list files null");
            if(file.exists()){
                try{
                    FileReader reader = new FileReader(file);

                    JSONParser jsonParser = new JSONParser();
                    org.json.simple.JSONArray updateJsonArr = (org.json.simple.JSONArray) jsonParser.parse(reader);

                    Type irMatchType = new TypeToken<ArrayList<IrMatch>>(){}.getType();
                    ArrayList<IrMatch> tmp = gson.fromJson(updateJsonArr.toJSONString(), irMatchType);
                    irMatchArr.addAll(tmp);
                }
                catch (ParseException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                return null;
            }

        }
        return gson.toJson(irMatchArr);
    }

    private static ArrayList<File> getFileArray(File [] files, ArrayList<File> filesList){
        for(File f : files){
            if(f.isDirectory() && !f.getName().equals("checkpoints")) {
                getFileArray(f.listFiles(), filesList);
            } else{
                filesList.add(f);
            }
        }
        return filesList;
    }



    /**
     * will overwrite any existing file
     *
     * @param filePath path of file excluding root
     * @param jsonString json formatted string
     * @param context context of application
     * @throws IOException ex: filePath does not exist, or file cannot be opened
     */
    public static void writeToJsonFile(String filePath, String jsonString, Context context) throws IOException{
        File jsonFile = new File(context.getExternalFilesDir(null), filePath);
        FileWriter writer = new FileWriter(jsonFile);
        writer.write(jsonString);
        writer.close();
    }

    /**
     *
     * @param incomingMessage json string shared from device
     * @param bluetoothDevice device that sent the data
     * @param context context of application
     */

    public static void writeFromBluetoothResponse(String incomingMessage, final BluetoothDevice bluetoothDevice, final Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        final Activity activity = (Activity) context;
        final String root = incomingMessage.split(",", 2)[0];
        String jsonString = incomingMessage.split(",", 2)[1];
        String dirName = sharedPreferences.getString(bluetoothDevice.getAddress(), null);
        if(!new File(context.getExternalFilesDir(null), root).exists()){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, root + " does not exist on you device and data received could not be saved.", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        if(dirName == null){
            boolean dirCreated = false;
            int count = 0;


            while(!dirCreated){

                if(count == 0){
                    dirName = bluetoothDevice.getName() + "-data";
                }
                else {
                    dirName = bluetoothDevice.getName() + "(" + count + ")-data";
                }
                Log.e(TAG, dirName);
                dirCreated = FileManager.makeDir(root + "/" + dirName, context);
                count ++;
            }
            editor.putString(bluetoothDevice.getAddress(), dirName);
            editor.apply();
        }
        else{
            FileManager.makeDir(root + "/" + dirName, context);
        }
        try {
            FileManager.writeToJsonFile(root + "/" + dirName + "/shared-copy.json", jsonString, context);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Data received from " + bluetoothDevice.getName() + " and placed in > " + root, Toast.LENGTH_LONG).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Could not generate file from received data", Toast.LENGTH_LONG).show();
                }
            });
        }

        //----
//        Gson gson = new Gson();
//        String gsonStr = readFile(root  + "/" + dirName + "/shared-copy.json", context);
//        Type irMatchType = new TypeToken<ArrayList<IrMatch>>(){}.getType();
//        ArrayList<IrMatch> irMatchArr = gson.fromJson(gsonStr, irMatchType);
//        ArrayList<Team> teamsList = createTeamsArr(irMatchArr);
//
//        for(Team team : teamsList){
//            Log.e("TEAM", "team name: " + team.getTeamName());
//            Log.e("TEAM", "team number: " + team.getTeamNumber());
//            Log.e("TEAM", "total points: " + team.getTotalPoints());
//            for (IrMatch irMatch : team.getIrMatches()){
//                Log.e("MATCH", "match number: " + irMatch.getMatchNumber());
//            }
//        }


    }

    /**
     *
     * @param mlist list of match objects
     * @return list of team objects containing match objects
     */

    public static ArrayList<Team> createTeamsArr(ArrayList<? extends Match> mlist)
    {

        ArrayList<Team> teamList = new ArrayList<>();
        ArrayList<String> usedTeams = new ArrayList<>();
        boolean teamDone;
        for(int i = 0; i < mlist.size(); i ++)
        {
            ArrayList<Match> currentMatchList = new ArrayList<>();

            String defHomeDistrict = "NC";

            teamDone = false;
            for(String t : usedTeams)
            {
                if(mlist.get(i).getTeamName().equals(t))
                {
                    teamDone = true;
                }
            }

            if(!teamDone)
            {

                for(int j = i; j < mlist.size(); j ++)
                {
                    if(mlist.get(i).getTeamName().equals(mlist.get(j).getTeamName()))
                    {
                        currentMatchList.add(mlist.get(j));
                    }
                }
                usedTeams.add(mlist.get(i).getTeamName());
                String [] teamNameSplit = mlist.get(i).getTeamName().split("\\s*,\\s*");

                teamList.add(new IrTeam(teamNameSplit[0], teamNameSplit[1], defHomeDistrict, currentMatchList));
            }
        }

        return teamList;
    }


    /**
     *
     * @param fileName name of file
     * @param context context of application
     * @throws IOException file not found
     * @throws ParseException cannot parse json file
     */
    public static void undoLastMatchSheet(String fileName, Context context) throws IOException, ParseException {

        File jsonFile = new File(context.getExternalFilesDir(null), fileName);
        FileReader reader = new FileReader(context.getExternalFilesDir(fileName));


        JSONParser jsonParser = new JSONParser();
        org.json.simple.JSONArray updateJsonArr = (org.json.simple.JSONArray) jsonParser.parse(reader);

        Gson gson = new Gson();

        Type irMatchType = new TypeToken<ArrayList<IrMatch>>(){}.getType();
        ArrayList<IrMatch> irMatchArr = gson.fromJson(updateJsonArr.toJSONString(), irMatchType);

        //remove last match object from arr
        irMatchArr.remove(irMatchArr.size() - 1);

        String gsonStr = gson.toJson(irMatchArr);

        FileWriter writer = new FileWriter(jsonFile);
        writer.write(gsonStr); //updated JSON String written


        writer.close();
        Toast.makeText(context, fileName + " updated successfully!", Toast.LENGTH_LONG).show();

    }


}

