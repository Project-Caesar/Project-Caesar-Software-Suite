import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;


public class CSV {

    //This function will create the data file in the event that the file does not already exist
    public static void createFile(String[] data) throws IOException{

        //bufferedwriter that will write the file
        BufferedWriter br = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/Test_Logs/" + data[0] + "_" + LocalDate.now() + ".csv"));

        //empty string array used for formatting only
        String[] empty = {""};

        //array used for the headers of the data file
        String[] header = {"Subject Name", "Stimulus Name", "Stimulus X Coordinate", "Stimulus Y Coordinate", "Attempt Timestamp"};

        //write the header and the blank line to separate the data
        writerFun(br,header);
        writerFun(br,empty);

        //close the writer
        br.close();
    }

    //this function is used to append a new line to an existing file instead of overwriting it
    public static void addLine(String[] data) throws IOException{

        //bufferedwriter variable used write to the existing file
        BufferedWriter br = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/Test_Logs/" + data[0] + "_" + LocalDate.now() + ".csv",true));

        //append the new line to the end of the file
        writerFun(br,data);

        //close the writer
        br.close();
    }

    //this function writes the row of information into the data file
    private static void writerFun(BufferedWriter br, String[] data) throws IOException{

        //stringbuilder that constructs the comma separated string
        StringBuilder sb = new StringBuilder();

        //loop that creates the comma separated string
        for (String element : data) {
            sb.append(element);
            sb.append(",");
        }

        //write the new string to the file
        br.write(sb.toString());

        //add a new line to go to the next row
        br.write("\n");

        //flush the buffer of the stringbuilder
        sb.setLength(0);
    }

}