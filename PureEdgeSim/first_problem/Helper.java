package first_problem;

import com.mechalikh.pureedgesim.datacentersmanager.ComputingNode;
import com.mechalikh.pureedgesim.datacentersmanager.DataCenter;
import com.mechalikh.pureedgesim.scenariomanager.SimulationParameters;
import com.mechalikh.pureedgesim.taskgenerator.Task;
import first_problem.dag.TaskNode;

import java.util.Random;

public class Helper {
    public static double calculateExecutionTime(ComputingNode computingNode, Task task){
        double mem_tr_time = 0.0;
        double io_time = 0;
        double cpu_time = 0;
        if(task.getCpuType().equals("GPU") &&computingNode.isGpuEnabled()){
            double time  = task.getLength() / (computingNode.getGpuMipsPerCore()* computingNode.getNumberOfGPUCores());
            cpu_time = time / getAmdahlsSpeedup(task, computingNode);
        } else if(task.getCpuType().equals("PIM") && computingNode.isPimEnabled()){
            cpu_time  = task.getLength() / computingNode.getPimMips();
        }else{
            cpu_time  = task.getLength() / computingNode.getMipsPerCore();
        }
        if(computingNode.getDataBusBandwidth() > 0) {
            mem_tr_time = task.getMemoryNeed() / computingNode.getDataBusBandwidth();
        }
        if(task.getStorageType().equals("SSD") && computingNode.isSsdEnabled()){
            io_time = task.getStorageNeed() * 60 / (100 * computingNode.getSsdReadBw()) //READ operation, 60% read
                    + task.getStorageNeed() * 40 / (100 * computingNode.getSsdWriteBw()); //WRITE operation, 40% write;;
        } else {
            if(computingNode.getReadBandwidth() > 0 && computingNode.getWriteBandwidth() > 0) {
                io_time = task.getStorageNeed() * 60 / (100 * computingNode.getReadBandwidth()) //READ operation, 60% read
                        + task.getStorageNeed() * 40 / (100 * computingNode.getWriteBandwidth()); //WRITE operation, 40% write;
            }
        }

        double total_latency = 0;
        if(task.getCpuType().equals("PIM") && computingNode.isPimEnabled()){
            total_latency = cpu_time + io_time;
        } else{
            total_latency = cpu_time + io_time + mem_tr_time;
        }

        return total_latency;
    }

    public static float getAmdahlsSpeedup(Task task, ComputingNode computingNode){
        float p = (float)task.getParallelTaskPct()/(float)100;
        float speedup = (float) (1.0/((1.0-p) + p/(float)computingNode.getNumberOfGPUCores()));
        return speedup;
    }

    public static Integer getRandomInteger(Integer min, Integer max){
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static Double calculateDistance(DataCenter dc1, DataCenter dc2){
        double x1 = dc1.getLocation().getXPos();
        double y1 = dc1.getLocation().getYPos();
        double x2 = dc2.getLocation().getXPos();
        double y2 = dc2.getLocation().getYPos();
        double distance = Math.sqrt(Math.pow((x2-x1), 2) + Math.pow((y2-y1), 2));
        return distance;
    }

    public static double getDataRate(double bw){
        //SimulationParameters.MAN_BANDWIDTH_BITS_PER_SECOND
        float leftLimit = 0.2F;
        float rightLimit = 0.7F;
        float generatedFloat = leftLimit + new Random().nextFloat() * (rightLimit - leftLimit);
        return 2*bw*generatedFloat;
    }

    public static double calculatePropagationDelay(double distance){
        return distance*10/300000000;
    }

    public static double getManEdgeTransmissionLatency(double bits){
        double dataRate = Helper.getDataRate(SimulationParameters.MAN_BANDWIDTH_BITS_PER_SECOND);
        return bits/dataRate;
    }

    public static double getManCloudTransmissionLatency(double bits){
        double dataRate = Helper.getDataRate(SimulationParameters.WAN_BANDWIDTH_BITS_PER_SECOND);
        return bits/dataRate;
    }

    public static double getWirelessTransmissionLatency(double bits){
        double dataRate = Helper.getDataRate(SimulationParameters.WIFI_BANDWIDTH_BITS_PER_SECOND);
        return bits/dataRate;
    }

    public static double calculateExecutionTime_new(ComputingNode computingNode, Task task){
        double io_time = 0;
        double cpu_time = 0;

        cpu_time  = task.getLength() / computingNode.getMipsPerCore();

        io_time = task.getStorageNeed() * 60 / (100 * computingNode.getReadBandwidth()) //READ operation, 60% read
                + task.getStorageNeed() * 40 / (100 * computingNode.getWriteBandwidth()); //WRITE operation, 40% write;

        double total_latency = cpu_time + io_time;
        //System.out.println(cpu_time + " : " +  io_time + " : " + total_latency);
        return total_latency;
    }
}
