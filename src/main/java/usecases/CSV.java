package usecases;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSV {

    private int numOfColumns;
    private String directoy;
    private String fileName;

    // creates CSV object that sets up the file to write too
    public CSV (String[] headers, String fileName, String directory) throws IOException {
        this.numOfColumns = headers.length;
        this.directoy = directory;
        this.fileName = this.directoy + '/' + fileName;

        new File(this.directoy).mkdirs();

        // bufferedwriter that will write the file
        BufferedWriter br = new BufferedWriter(new FileWriter(this.fileName));

        // write headers to the file
        writerFun(br, headers);
        // add empty line between headers and future data
        writerFun(br, new String[] {""});

        br.close();
    }

    // used if no new directory is required to be made
    public CSV (String[] headers, String fileName) throws IOException {
        this(headers, fileName, "");
    }

    //this function is used to append a new line to an existing file instead of overwriting it
    public void addLine(String[] data) throws IOException{

        if (numOfColumns != data.length) {
            throw new IllegalArgumentException
                    ("New Line must contain the same amount of entries as the number of headers");

        } else {
            //bufferedwriter variable used write to the existing file
            BufferedWriter br = new BufferedWriter(new FileWriter(fileName,true));
            //append the new line to the end of the file
            writerFun(br,data);

            //close the writer
            br.close();
        }
    }

    //this function writes the row of information into the data file
    private void writerFun(BufferedWriter br, String[] data) throws IOException{

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