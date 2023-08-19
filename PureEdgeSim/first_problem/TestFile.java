package first_problem;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TestFile {
    static final String task_dataset_path = "/Users/akhirul.islam/task_dataset/TaskDetails.txt";
    //static final String processed_file = "/Users/akhirul.islam/task_dataset/processed_latency_file.txt";
    static final String processed_file = "/Users/akhirul.islam/task_dataset/test_file.txt";
    public static void main(String args[]){
        FileInputStream inputStream = null;
        Scanner sc = null;
        long lineCount = 0;
        final Double MIPS = 400000.0;
        final Double IO_SPEED = 17000.0; //kbps
        final Double DATA_BUS_SPEED =  300000.0; //kbps
        try{
            inputStream = new FileInputStream(task_dataset_path);
            sc = new Scanner(inputStream, "unicode");
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(processed_file, true));
            String header = "jobid,taskid,cpu_need,memory_need,stirage_need,input_data_size,output_data_size,cpu_time,io_time,mem_time,type\n";
            out.write(header);
            int cpu_count = 0;
            int mem_count = 0;
            int io_count = 0;
            int normal_count = 0;

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                lineCount++;
                if(lineCount > 1) {
                    int type = 0;
                    String fields[] = line.split(",");
                    String taskId = fields[1];
                    String jobid = fields[2];
                    Double cpu = Double.valueOf(fields[3]);
                    Double memory = Double.valueOf(fields[4])*Helper.getRandomInteger(1000, 1500);
                    //Double storage = Double.valueOf(fields[5]);
                    Double storage = Double.valueOf(fields[5])*Helper.getRandomInteger(100, 500);
                    Integer inputsize = Helper.getRandomInteger(8000000, 400000000);
                    Integer outputsize = Helper.getRandomInteger(8000000, 400000000);

                    Double cpuTime = cpu / MIPS;
                    Double ioTime = storage * 60 / (100 * 1000) //READ operation, 60% read
                            + storage * 40 / (100 * 500); //WRITE operation, 40% write;;
                    Double memoryTransferTime = memory/DATA_BUS_SPEED;

                    Double totalTime = cpuTime + ioTime + memoryTransferTime;
                    if(cpuTime/totalTime > 0.5){
                        cpu_count++;
                        type = 1;
                    } else if(ioTime/totalTime > 0.5){
                        type = 2;
                        io_count++;
                    } else if(memoryTransferTime/totalTime > 0.5){
                        type = 3;
                        mem_count++;
                    } else{
                        normal_count++;
                    }
                    String newline = jobid + "," + taskId + "," + cpu + "," + memory + "," + storage + "," + inputsize + "," + outputsize + "," + cpuTime + "," + ioTime + "," + memoryTransferTime + "," + type + "\n";
                    out.write(newline);
                }
            }

            System.out.println("cpu: " + cpu_count + " io: " + io_count + " mem: " + mem_count + " normal: " + normal_count);
            out.close();
            sc.close();
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }
}
