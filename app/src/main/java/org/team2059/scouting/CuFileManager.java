/**
 * class to organize read and write
 * functions of the app to internal storage
 * in dir data/data/app_name
 *
 * @author Anupam
 *
 */

package org.team2059.scouting;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.team2059.scouting.core.Match;
import org.team2059.scouting.core.Team;
import org.team2059.scouting.core.frc2023.CuMatch;
import org.team2059.scouting.core.frc2023.CuTeam;


public class CuFileManager extends FileManager
{
    private static final String TAG = "FileManager";


    /**
     *
     * @param fileName name of file
     * @param match match object of attributes
     * @param context context of application
     * @throws IOException file not found
     */


    public static void writeToJsonFile(String fileName, CuMatch match, Context context) throws IOException
    {

        //saves to external storage (public, visible in device files on phone)
        File jsonFile = new File(context.getExternalFilesDir(null), fileName);


        if(!jsonFile.exists()) //make new file
        {
            FileWriter writer = new FileWriter(jsonFile);

            ArrayList<CuMatch> gsonArr = new ArrayList<>();
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

                Type irMatchType = new TypeToken<ArrayList<CuMatch>>(){}.getType();
                ArrayList<CuMatch> irMatchArr = gson.fromJson(updateJsonArr.toJSONString(), irMatchType);

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


    /**
     *
     * @param filepath path of file excluding root
     * @param context context of application
     * @return json formatted string, default null
     */


    // this function needs to be able to read from a competition directory as well,
    // while omitting checkpoints directory

    public static String readFile(String filepath, Context context){

        ArrayList<CuMatch> cuMatchArr = new ArrayList<>();

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

                    Type cuMatchType = new TypeToken<ArrayList<CuMatch>>(){}.getType();
                    if(updateJsonArr != null){
                        ArrayList<CuMatch> tmp = gson.fromJson(updateJsonArr.toJSONString(), cuMatchType);
                        cuMatchArr.addAll(tmp);
                    }


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

                    Type cuMatchType = new TypeToken<ArrayList<CuMatch>>(){}.getType();
                    if(updateJsonArr != null){
                        ArrayList<CuMatch> tmp = gson.fromJson(updateJsonArr.toJSONString(), cuMatchType);
                        cuMatchArr.addAll(tmp);
                    }
                }
                catch (ParseException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else return null;
        }
        return gson.toJson(cuMatchArr);
    }

    private static ArrayList<File> getFileArray(File [] files, ArrayList<File> filesList){
        for(File f : files){
            if(f.isDirectory() && !f.getName().equals("checkpoints")) {
                getFileArray(f.listFiles(), filesList);
            } else if (!f.isDirectory()){
                filesList.add(f);
            }
        }
        return filesList;
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

                Log.e("createteam", teamNameSplit[0]);
                teamList.add(new CuTeam(teamNameSplit[0], teamNameSplit[1], defHomeDistrict, currentMatchList));
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

        Type cuMatchType = new TypeToken<ArrayList<CuMatch>>(){}.getType();
        ArrayList<CuMatch> cuMatchArr = gson.fromJson(updateJsonArr.toJSONString(), cuMatchType);


        if(cuMatchArr.size() == 0){
            Toast.makeText(context, "No matches have been recorded", Toast.LENGTH_LONG).show();
            return;
        }
        //remove last match object from arr
        cuMatchArr.remove(cuMatchArr.size() - 1);

        String gsonStr = gson.toJson(cuMatchArr);

        FileWriter writer = new FileWriter(jsonFile);
        writer.write(gsonStr); //updated JSON String written


        writer.close();
        Toast.makeText(context, fileName + " updated successfully!", Toast.LENGTH_LONG).show();
    }
}

