import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.opencsv.*;

public class FindAndReplaceUtil {
	
	//Should have used instance variables...

	public static void main(String[] args) throws IOException {
		String path[] = new String[1];
		List<String[]> mapList;
		String outputPath;
		ArrayList<String> pathList;
		
		try{
			mapList = readCSV(args[args.length-2]); 	    //Read the CSV of the map
			outputPath = args[args.length-1];				//Read the input for output path
			pathList = createPathList((path = Arrays.copyOfRange(args,0,(args.length)-2)));  //Create list of strings for all possible output CSVs	
		}
		catch(ArrayIndexOutOfBoundsException e){			//Ask for paths if none provided
			Scanner scan = new Scanner(System.in);			//TODO Check for just CSV files, catch invalid inputs
			System.out.println("Enter the path of a CSV or a directory containing only CSV files.");
			path[0] = scan.nextLine();
			System.out.println("Enter the path of the map");
			String map = scan.nextLine();
			System.out.println("Enter the path of the directory to output into.");
			outputPath = scan.nextLine();
			mapList = readCSV(map);
			pathList = createPathList(path);
		}
		
		for(int i=0; i<pathList.size(); i++){					//Run for every CSV file in pathList
			findAndReplace(outputPath,mapList,readCSV(pathList.get(i)),pathList.get(i));
		}
	}
	
	/* Returns an ArrayList of strings for the output path for each CSV modified. If a CSV file is inputed, it will create one path
	 * string for each file inputed. If it is not a CSV it will assume it is a directory, creating a path for each file inside the
	 * directory.  */
	//TODO Add exception if it isn't a .CSV or Directory
	private static ArrayList<String> createPathList(String[] paths){
		ArrayList<String> pathList = new ArrayList<String>();
		for(int i=0; i<(paths.length); i++){ 					//All inputs apart excluding the map and target directory
			if(paths[i].contains(".csv")){						//Reading all .CSV files
				pathList.add(paths[i]);
			}
			else{												//Reading directory
				File[] files = new File(paths[i]).listFiles();
				for (File file : files) {
				    if (file.isFile()) {
				        pathList.add(file.getAbsolutePath());
				    }
				}
			}
		}
		return pathList;
	}
	
	//Reads CSV file, returning as a list of strings 
	private static List<String[]> readCSV(String fileName) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(fileName));
		List<String[]> myEntries = reader.readAll();
		reader.close();
		return myEntries;
	}
	
	/*Writes a CSV file using a string for the path, a string for the filename (which is then modified), and a list of strings for 
	the content of the CSV*/
	private static void writeCSV(List<String[]> list, String path, String fileName) throws IOException {
		CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(path + fileName + "_Modified.csv"), "UTF-8"));
		writer.writeAll(list);
		writer.close();
	}
	
	//Finds the final substring of the filename
	private static String findString(String input){
		int lastIndex = input.lastIndexOf("\\");
		int csvIndex = input.indexOf(".csv");
		return input.substring(lastIndex,csvIndex);
	}
	
	//Modifies CSV values by checking from a map
	private static void findAndReplace(String outputPath, List<String[]> mapList, List<String[]> csvList, String fileName) throws IOException{
		for(int i=0;i<csvList.size();i++){								//Loop through each line of CSV
			for(int j=0;j<csvList.get(i).length;j++){					//Loop through each string of line
				for(int k=0;k<mapList.size();k++){						//Loop through each row of map
					if(mapList.get(k)[0].equals(csvList.get(i)[j])){ 	//Check map key vs CSV value
						if(mapList.get(k).length == 3){					//Check for boolean flag to delete
							if(mapList.get(k)[2].toLowerCase().equals("false")){
								csvList.remove(i);						//Empty current row
							}
							else{
								String[] tempArray = csvList.get(i);	//Create a temp. row from CSV values
								tempArray[j] = mapList.get(k)[1];		//Modify the temp. row with the map value
								csvList.set(i,tempArray);				//Replace initial row from CSV with modified row
							}
						}
					}													//above can be improved
				}
			}
		}
		writeCSV(csvList, outputPath, findString(fileName));
	}
	
}