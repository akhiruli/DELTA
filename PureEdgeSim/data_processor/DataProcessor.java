package data_processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mechalikh.pureedgesim.datacentersmanager.ComputingNode;
import com.mechalikh.pureedgesim.datacentersmanager.DataCenter;
import com.mechalikh.pureedgesim.energy.EnergyModelComputingNode;
import com.mechalikh.pureedgesim.scenariomanager.SimulationParameters;
import com.mechalikh.pureedgesim.simulationmanager.SimulationManager;
import com.mechalikh.pureedgesim.taskgenerator.Task;
import first_problem.Helper;
import first_problem.dag.TaskNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DataProcessor {
    public static Map<Integer, Job> jobsMap;
    public static Map<Integer, Job> scheduledJob;
    public static Map<Integer, Map<Integer, TaskNode>> tasksMap;
    private List<? extends ComputingNode> devices;
    SimulationManager simManager;
    private Integer ueDeviceIndex;
    static final String task_dataset_path = "/Users/akhirul.islam/task_dataset/TaskDetails.txt";
    static final String job_dataset_path = "/Users/akhirul.islam/task_dataset/JobDetails.json";
    static Integer totalCpuIntensiveTask = 0;
    public DataProcessor(SimulationManager simulationManager,
                         List<? extends ComputingNode> devicesList){
        jobsMap = new HashMap();
        tasksMap = new HashMap<>();
        scheduledJob = new HashMap<>();
        simManager = simulationManager;
        devices = devicesList;
        ueDeviceIndex = 0;

        this.loadJobs();
        this.loadTasks();
        //System.out.println("DataProcessor: " + jobsMap.size() + "-" + tasksMap.size());
    }

    public Map<Integer, Job> getJobsMap() {
        return jobsMap;
    }

    public Map<Integer, Map<Integer, TaskNode>> getTasksMap() {
        return tasksMap;
    }
    public List<Task> getTaskList(){
        List<Task> taskList = new ArrayList<>();
        Integer cpu_intensive = 0;
        Integer memory_intensive = 0;
        Integer io_intensive = 0;
        Integer normal = 0;
        this.assignDependencies();
        this.assignUEDevice();
        this.assignStartAndEndTask(); //if required
        this.updateData(3);
        this.assignTaskType();

        Integer job_count = 0;
        for(Map.Entry<Integer, Map<Integer, TaskNode>> entry : tasksMap.entrySet()){
            Map<Integer, TaskNode> job = entry.getValue();
            if(!isValidDag(job)){
                System.out.println("DAG with ap_id: " + entry.getKey() + " is not valid");
                continue;
            }

            job_count++;
            List<TaskNode> tempTaskList = new ArrayList<>();
            for (Map.Entry<Integer, TaskNode> task : job.entrySet()) {
                task.getValue().setContainerSize(jobsMap.get(entry.getKey()).getAvgTaskLength());
                if(task.getValue().getTaskType() == TaskNode.TaskType.CPU_INTENSIVE){
                    ++cpu_intensive;
                } else if(task.getValue().getTaskType() == TaskNode.TaskType.MEM_INTENSIVE){
                    ++memory_intensive;
                } else if(task.getValue().getTaskType() == TaskNode.TaskType.IO_INTENSIVE){
                    ++io_intensive;
                } else{
                    ++normal;
                }
                taskList.add(task.getValue());
                tempTaskList.add(task.getValue());
            }

            if(simManager.getScenario().getStringOrchAlgorithm().equals("HIERARCHICAL_ALGO")) {
                assignDecision(tempTaskList);
                assignTaskRanking(tempTaskList);
            } else if (simManager.getScenario().getStringOrchAlgorithm().equals("MULTI_USER_SCHEDULING")) {
                multiUserScheduleDecision(tempTaskList);
            } else if (simManager.getScenario().getStringOrchAlgorithm().equals("RANDOM_RR")) {
                randomRRScheduleDecision(tempTaskList);
            } else if(simManager.getScenario().getStringOrchAlgorithm().equals("INTELIGENT_TO")) {
                intelligentSchedulingDecision(tempTaskList);
            }
            scheduledJob.put(entry.getKey(), jobsMap.get(entry.getKey()));

//            if(entry.getKey() == 4){
//                printDag(job);
//            }

            if(job_count >= 1)
               break;
        }

        return taskList;
    }

    public static Comparator<TaskNode> getCompByCPUNeed()
    {
        Comparator comp = new Comparator<TaskNode>(){
            @Override
            public int compare(TaskNode t1, TaskNode t2)
            {
                if (t1.getLength() == t2.getLength())
                    return 0;
                else if(t1.getLength() < t2.getLength())
                    return -1;
                else
                    return 1;
            }
        };
        return comp;
    }
    void assignDecision(List<TaskNode> tempTaskList){
        Collections.sort(tempTaskList, getCompByCPUNeed());
        int numNormalTask = 0;
        for(TaskNode taskNode : tempTaskList){
            if(taskNode.getTaskType() == TaskNode.TaskType.NORMAL){
                ++numNormalTask;
            }
        }

        int no_uetask = (int) Math.ceil(50*numNormalTask/100);
        int index =0;
        while(no_uetask > 0 && index < tempTaskList.size()){
            if(tempTaskList.get(index).getTaskType() == TaskNode.TaskType.NORMAL){
                tempTaskList.get(index).setTaskDecision(TaskNode.TaskDecision.UE_ONLY);
                tempTaskList.get(index).setLength(Helper.getRandomInteger(5, 50));
                tempTaskList.get(index).setMaxLatency(Helper.getRandomInteger(60, 100));
                --no_uetask;
            }

            ++index;
        }
    }

    void printDag(Map<Integer, TaskNode> job){
        for(Map.Entry<Integer, TaskNode> task :  job.entrySet()) {
            TaskNode taskNode = task.getValue();
            String pred_str = "";
            String succ_str = "";
            for(TaskNode t : taskNode.successors){
                if(succ_str.isEmpty()){
                    succ_str += t.getId();
                } else{
                    succ_str += "->" + t.getId();
                }
            }

            for(TaskNode t : taskNode.predecessors){
                if(pred_str.isEmpty()){
                    pred_str += t.getId();
                } else{
                    pred_str += "->" + t.getId();
                }
            }

            System.out.println("Task: " + taskNode.getId() + " predecessors: " + pred_str + " Successors: " + succ_str);
        }
    }

    boolean isValidDag(Map<Integer, TaskNode> job){
        boolean result = true;
        for(Map.Entry<Integer, TaskNode> task :  job.entrySet()) {
            TaskNode taskNode = task.getValue();
            if (taskNode.isStartTask()) {
                if (taskNode.predecessors.size() != 0 || taskNode.successors.size() == 0) {
                    result = false;
                    break;
                }
            } else if (taskNode.isEndTask()) {
                if (taskNode.predecessors.size() == 0 || taskNode.successors.size() != 0) {
                    result = false;
                    break;
                }
            } else {
                if (taskNode.predecessors.size() == 0 || taskNode.successors.size() == 0) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
    void assignUEDevice(){
        if(devices.size() == 0) {
            System.out.println("No UE devices available");
            return;
        }
        for(Map.Entry<Integer, Map<Integer, TaskNode>> entry : tasksMap.entrySet()){
            Map<Integer, TaskNode> job = entry.getValue();
            for(Map.Entry<Integer, TaskNode> task :  job.entrySet()){
                TaskNode taskNode = task.getValue();
                taskNode.setEdgeDevice(devices.get(ueDeviceIndex));
            }

            ueDeviceIndex++;
            if(ueDeviceIndex >= devices.size()){
                ueDeviceIndex = 0;
            }
        }
    }
    void assignDependencies(){
        for(Map.Entry<Integer, Map<Integer, TaskNode>> entry : tasksMap.entrySet()){
            Map<Integer, TaskNode> job = entry.getValue();
            for(Map.Entry<Integer, TaskNode> task :  job.entrySet()){
                TaskNode taskNode = task.getValue();
                for(Integer taskId : taskNode.getSuccessorsId()){
                    TaskNode dTask = job.get(taskId);
                    if(dTask != null)
                        taskNode.successors.add(dTask);
                }

                for(Integer taskId : taskNode.getPredecessorsId()){
                    TaskNode dTask = job.get(taskId);
                    if(dTask != null)
                        taskNode.predecessors.add(dTask);
                }

                if(taskNode.successors.size() == 0){
                    taskNode.setEndTask(true);
                }

                if(taskNode.predecessors.size() == 0){
                    taskNode.setStartTask(true);
                }

                //rectifying the dependency
                List<TaskNode> successors = taskNode.successors;
                for(TaskNode tNode : successors){
                    boolean pred_check = false;
                    for(TaskNode succNode: tNode.predecessors){
                        if(succNode.getId() == taskNode.getId()){
                            pred_check = true;
                            break;
                        }
                    }
                    if(!pred_check) {
                        tNode.predecessors.add(taskNode);
                    }
                }

                List<TaskNode> predecessors = taskNode.predecessors;
                for(TaskNode tNode : predecessors){
                    boolean succ_check = false;
                    for(TaskNode succNode: tNode.successors){
                        if(succNode.getId() == taskNode.getId()){
                            succ_check = true;
                            break;
                        }
                    }
                    if(!succ_check) {
                        tNode.successors.add(taskNode);
                    }
                }
            }

        }
    }

    void assignStartAndEndTask(){
        for(Map.Entry<Integer, Map<Integer, TaskNode>> entry : tasksMap.entrySet()){
            Map<Integer, TaskNode> job = entry.getValue();
            List<TaskNode> startTasks = new ArrayList<>();
            List<TaskNode> endTasks = new ArrayList<>();
            ComputingNode ueDevice = null;
            for(Map.Entry<Integer, TaskNode> task :  job.entrySet()) {
                TaskNode taskNode = task.getValue();
                ueDevice = taskNode.getEdgeDevice();
                if(taskNode.predecessors.size() == 0){
                    startTasks.add(taskNode);
                }

                if(taskNode.successors.size() == 0){
                    endTasks.add(taskNode);
                }
            }

            if(startTasks.size() > 1){
                Integer dummy_task_id = this.getDummyTaskId(job);
                TaskNode taskNode = this.createDummyTask(dummy_task_id, Integer.valueOf(entry.getKey()), ueDevice);
                taskNode.setStartTask(true);
                taskNode.successors.addAll(startTasks);
                for(TaskNode task : startTasks){
                    task.setStartTask(false);
                    task.predecessors.add(taskNode);
                }
                job.put(dummy_task_id, taskNode);
            }

            if(endTasks.size() > 1){
                Integer dummy_task_id = this.getDummyTaskId(job);
                TaskNode taskNode = this.createDummyTask(dummy_task_id, Integer.valueOf(entry.getKey()), ueDevice);
                taskNode.predecessors.addAll(endTasks);
                taskNode.setEndTask(true);
                for(TaskNode task : endTasks){
                    task.setEndTask(false);
                    task.successors.add(taskNode);
                }
                job.put(dummy_task_id, taskNode);
            }
        }

    }

    Integer getDummyTaskId(Map<Integer, TaskNode> job){
       Integer task_id = Integer.MIN_VALUE;
       for(Map.Entry<Integer, TaskNode> task :  job.entrySet()){
           if(task.getKey() > task_id){
               task_id = task.getKey();
           }
       }

       return task_id + 1;
    }

    TaskNode createDummyTask(Integer id, Integer app_id, ComputingNode ueDevice){
        TaskNode taskNode = new TaskNode(0, 0);
        taskNode.setApplicationID(app_id);
        taskNode.setFileSize(0).setOutputSize(0);
        taskNode.setContainerSize(0);
        taskNode.setRegistry(simManager.getDataCentersManager().getCloudDatacentersList().get(0).nodeList.get(0));
        taskNode.setId(id);
        taskNode.setMaxLatency(0);
        taskNode.setMemoryNeed(0.0);
        taskNode.setStorageNeed(0.0);
        taskNode.setContainerSize(0);
        taskNode.setEdgeDevice(ueDevice);
        taskNode.setDummyTask(true);
        return taskNode;
    }

    public void loadJobs(){
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get(job_dataset_path));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNodeRoot = objectMapper.readTree(jsonData);
            if(jsonNodeRoot.isObject()) {
                ArrayNode jobs = (ArrayNode) jsonNodeRoot.get("Jobs");
                if(jobs.isArray()){
                    for(JsonNode objNode : jobs){
                        Job job = new Job();
                        job.setJobID_InDB(objNode.get("JobID_InDB").asText());
                        job.setJobID(objNode.get("JobID").asText());
                        job.setTimeDeadLinePrefered(objNode.get("TimeDeadLinePrefered").asText());
                        job.setListOfTasks(objNode.get("ListOfTasks").asText());
                        job.setMinStorageNeeded(objNode.get("MinStorageNeeded").asText());
                        job.setTasksWhichCanRunInParallel(objNode.get("TasksWhichCanRunInParallel").asText());
                        job.setMinTaskSize(objNode.get("MinTaskSize").asText());
                        job.setMaxTaskSize(objNode.get("MaxTaskSize").asText());
                        job.setAvgTaskLength();
                        jobsMap.put(Integer.valueOf(job.getJobID()), job);
                    }
                }
            }
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    public void loadTasks(){
        BufferedReader reader;
        long lineCount = 0;
        FileInputStream inputStream = null;
        Scanner sc = null;
        Integer taskCount = 0;
        Integer dupTaskCount = 0;
        Long max_cpu = Long.MIN_VALUE;
        Long min_cpu = Long.MAX_VALUE;
        try {
            inputStream = new FileInputStream(task_dataset_path);
            sc = new Scanner(inputStream, "unicode");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                lineCount++;
                if(lineCount > 1) {
                    String fields[] = line.split("\\[");

                    String other_fields[] = fields[0].split(",");
                    String taskId = other_fields[1];
                    if(!taskId.isEmpty()) {
                        Integer jobId = Integer.valueOf(other_fields[2]);
                        Long cpu = Long.parseLong(other_fields[3]);
                        Double memory = Double.parseDouble(other_fields[4]);
                        Double storage = Double.parseDouble(other_fields[5]);
                        Double deadline = Double.parseDouble(other_fields[8]);
                        Integer task_id = Integer.parseInt(taskId);

                        if(cpu < min_cpu){
                            min_cpu = cpu;
                        }

                        if(cpu > max_cpu){
                            max_cpu = cpu;
                        }
//24
                        TaskNode taskNode = new TaskNode(task_id, cpu);
                        taskNode.setApplicationID(Integer.valueOf(jobId));
                        taskNode.setFileSize(Helper.getRandomInteger(8000000, 400000000)).setOutputSize(Helper.getRandomInteger(8000000, 400000000));
                        taskNode.setContainerSize(25000);
                        taskNode.setRegistry(simManager.getDataCentersManager().getCloudDatacentersList().get(0).nodeList.get(0));
                        taskNode.setId(task_id);
                        taskNode.setMaxLatency(deadline);
                        taskNode.setMemoryNeed(memory*Helper.getRandomInteger(1000, 1500));
                        taskNode.setStorageNeed(storage*Helper.getRandomInteger(500, 1000));
                        taskNode.setParallelTaskPct(Helper.getRandomInteger(70, 90));

                        String successors_str = fields[1];
                        if(successors_str.length() > 1) {
                            String successors = fields[1].split("\\]")[0];
                            String[] succList = successors.split(",");
                            for(String succ : succList){
                                if(!succ.isEmpty()) {
                                    taskNode.getSuccessorsId().add(Integer.parseInt(succ));
                                }
                            }
                        }
                        String predecessors_str = fields[3];

                        if(predecessors_str.length() > 1){
                            String predecessors = predecessors_str.split("\\]")[0];
                            String[] predList = predecessors.split(",");
                            for(String pred : predList){
                                if(!pred.isEmpty()) {
                                    taskNode.getPredecessorsId().add(Integer.parseInt(pred));
                                }
                            }
                        }

                        if(tasksMap.containsKey(jobId)) {
                            if(tasksMap.get(jobId).containsKey(task_id)){
                                dupTaskCount++;
                            } else {
                                tasksMap.get(jobId).put(task_id, taskNode);
                            }
                        } else{
                            Map<Integer, TaskNode> tMap = new HashMap<>();
                            tMap.put(task_id, taskNode);
                            tasksMap.put(jobId, tMap);
                        }
                        taskCount++;
                    }
                }
            }
            sc.close();
        } catch (IOException ioException){
            ioException.printStackTrace();
        }

//        System.out.println("Total tasks loaded: " + taskCount + " duplicate: " + dupTaskCount);
//        System.out.println("MAX CPU: " + max_cpu + " MIN CPU: " + min_cpu);
    }

    private void assignTaskType(){
        for(Map.Entry<Integer, Map<Integer, TaskNode>> entry : tasksMap.entrySet()){
            Map<Integer, TaskNode> job = entry.getValue();
            int numCPUIntensiveTask = 0;
            for(Map.Entry<Integer, TaskNode> task :  job.entrySet()) {
                task.getValue().setTaskType(this.getTaskType(task.getValue()));
                if(task.getValue().getTaskType() == TaskNode.TaskType.CPU_INTENSIVE)
                    ++numCPUIntensiveTask;
            }

            Integer parallelizableTaskPct = Helper.getRandomInteger(55, 80); //55% to 80% of CPU intensive tasks are marking as GPU suitable tasks
            Integer totalGpuTask = parallelizableTaskPct*numCPUIntensiveTask/100;
            for(Map.Entry<Integer, TaskNode> task :  job.entrySet()) {
                if(task.getValue().getTaskType() == TaskNode.TaskType.CPU_INTENSIVE){
                    if(totalGpuTask>0) {
                        task.getValue().setCpuType("GPU");
                        --totalGpuTask;
                    }
                }
            }
        }
    }

    private void updateData(int type){ //1-cpu, 2-io, 3-memory
        int job_no = 0;
        for(Map.Entry<Integer, Map<Integer, TaskNode>> entry : tasksMap.entrySet()) {
            Map<Integer, TaskNode> job = entry.getValue();
            job_no++;
            int intensive_task = (int) Math.ceil(job.size()/2) + Helper.getRandomInteger(2, 5);
            int task_assigned = 0;
            List<DataCenter> dclist = simManager.getDataCentersManager().getEdgeDatacenterList();
            ComputingNode node = dclist.get(0).nodeList.get(0);
            final Double MIPS = node.getMipsPerCore();
            for (Map.Entry<Integer, TaskNode> task : job.entrySet()) {
                TaskNode taskNode = task.getValue();
                if(taskNode.isDummyTask())
                    continue;
                TaskNode.TaskType taskType = TaskNode.TaskType.NORMAL;

                Double cpuTime = taskNode.getLength() / MIPS;
                Double ioTime = taskNode.getStorageNeed() * 60 / (100 * node.getReadBandwidth()) //READ operation, 60% read
                        + taskNode.getStorageNeed() * 40 / (100 * node.getWriteBandwidth()); //WRITE operation, 40% write;;
                Double memoryTransferTime = taskNode.getMemoryNeed()/node.getDataBusBandwidth();

                Double totalTime = cpuTime + ioTime + memoryTransferTime;

                if(type == 1) {
                    if(cpuTime ==0)
                        continue;
                    if (cpuTime / totalTime > 0.5) {
                        task_assigned++;
                        continue;
                    }

                    Double required_cpu_time = memoryTransferTime + ioTime + Helper.getRandomInteger(10, 50);
                    int task_length = (int) Math.ceil((taskNode.getLength() * required_cpu_time) / (cpuTime));
                    taskNode.setLength(task_length);
                } else if(type == 2){
                    if(ioTime <= 0)
                        continue;
                    if(ioTime/totalTime > 0.5){
                        task_assigned++;
                        continue;
                    }

                    Double required_io_time = memoryTransferTime + cpuTime + Helper.getRandomInteger(10, 50);
                    int task_length = (int) Math.ceil((taskNode.getLength() * required_io_time) / (ioTime));
                    taskNode.setLength(task_length);
                } else { //type=3
                    if(memoryTransferTime <= 0)
                        continue;
                    if(memoryTransferTime/totalTime > 0.5){
                        task_assigned++;
                        continue;
                    }

                    Double required_mem_time = ioTime + cpuTime + Helper.getRandomInteger(10, 50);
                    int task_length = (int) Math.ceil((taskNode.getLength() * required_mem_time) / (memoryTransferTime));
                    taskNode.setLength(task_length);
                }

                if (task_assigned >= intensive_task)
                    break;
            }

            if(job_no >= 300)
                break;

        }
    }

    private TaskNode.TaskType getTaskType(TaskNode taskNode){
        /*
        * SSD: w: 540MB/s, r: 560MB/s
        * HD: 140-190 MB/s
        * MIPS- 400000
        * data bus: 300 MB/s (SATA 2.0 https://en.wikipedia.org/wiki/Serial_ATA)
        * */
        List<DataCenter> dclist = simManager.getDataCentersManager().getEdgeDatacenterList();
        ComputingNode node = dclist.get(0).nodeList.get(0);
        final Double MIPS = node.getMipsPerCore();
        TaskNode.TaskType taskType = TaskNode.TaskType.NORMAL;
        Double cpuTime = taskNode.getLength() / MIPS;
        Double ioTime = taskNode.getStorageNeed() * 60 / (100 * node.getReadBandwidth()) //READ operation, 60% read
                + taskNode.getStorageNeed() * 40 / (100 * node.getWriteBandwidth()); //WRITE operation, 40% write;;
        Double memoryTransferTime = taskNode.getMemoryNeed()/node.getDataBusBandwidth();

        Double totalTime = cpuTime + ioTime + memoryTransferTime;
        if(cpuTime/totalTime > 0.5) {
            taskType = TaskNode.TaskType.CPU_INTENSIVE;
        } else if(ioTime/totalTime > 0.5){
            taskType = TaskNode.TaskType.IO_INTENSIVE;
        } else if(memoryTransferTime/totalTime > 0.5){
            taskType = TaskNode.TaskType.MEM_INTENSIVE;
            taskNode.setCpuType("PIM");
        }

        return taskType;
    }

    private void randomRRScheduleDecision(List<TaskNode> tempTaskList){
        for(TaskNode taskNode : tempTaskList){
            if(taskNode.getTaskType() == TaskNode.TaskType.NORMAL){
                Integer decision = Helper.getRandomInteger(0, 1);
                if(decision == 0)
                    taskNode.setTaskDecision(TaskNode.TaskDecision.UE_ONLY);
                else
                    taskNode.setTaskDecision(TaskNode.TaskDecision.MEC_ONLY);
            } else{
                taskNode.setTaskDecision(TaskNode.TaskDecision.MEC_ONLY);
            }
        }
    }

    private void multiUserScheduleDecision(List<TaskNode> tempTaskList){
        Collections.sort(tempTaskList, getCompByCPUNeed());
        int no_uetask = (int) Math.ceil(5*tempTaskList.size()/100);

        for(int i=0; i<no_uetask; i++){
            tempTaskList.get(i).setLength(Helper.getRandomInteger(5, 50));
            tempTaskList.get(i).setMaxLatency(Helper.getRandomInteger(60, 100));
        }

        for(TaskNode taskNode : tempTaskList){
            if(taskNode.getEdgeDevice().getMipsPerCore() == 0) {
                System.out.println("UE MIPS zero");
            }
            double localComputingTime = taskNode.getLength()/taskNode.getEdgeDevice().getMipsPerCore();
            double mecComputingTime = getMECComputingTime(taskNode);

            if(localComputingTime < mecComputingTime){
                taskNode.setTaskDecision(TaskNode.TaskDecision.UE_ONLY);
            } else{
                taskNode.setTaskDecision(TaskNode.TaskDecision.MEC_ONLY);
            }
        }
    }

    private double getMECComputingTime(TaskNode taskNode){ //only for multi-user scheduling algo
        List<DataCenter> dclist = simManager.getDataCentersManager().getEdgeDatacenterList();
        ComputingNode node = dclist.get(0).nodeList.get(0);
        Double mips = node.getMipsPerCore();

        //System.out.println(mips + " " + node.getWriteBandwidth() + " " + " " + node.getReadBandwidth());
        double cpuTime = taskNode.getLength()/mips;

        double io_time = taskNode.getStorageNeed() * 60 / (100 * node.getReadBandwidth()) //READ operation, 60% read
                + taskNode.getStorageNeed() * 40 / (100 * node.getWriteBandwidth()); //WRITE operation, 40% write;
        double mem_tr_time = taskNode.getMemoryNeed() / node.getDataBusBandwidth();

        return cpuTime + io_time + mem_tr_time;
    }

    //Asigning rank to each of the task of an application represented by DAG
    private void assignTaskRanking(List<TaskNode> tempTaskList){
        DataCenter nearestDc = simManager.getDataCentersManager().getEdgeDatacenterList().get(0);
        DataCenter neighBorDc = simManager.getDataCentersManager().getEdgeDatacenterList().get(1);
        DataCenter cloudDc = simManager.getDataCentersManager().getCloudDatacentersList().get(0);

        ComputingNode nearestEdgeNode = nearestDc.nodeList.get(0);
        Double nearestEdgeMips = nearestEdgeNode.getMipsPerCore();
        ComputingNode cloudNode = cloudDc.nodeList.get(0);
        Double cloudMips = cloudNode.getMipsPerCore();
        ComputingNode neighborEdgeNode = neighBorDc.nodeList.get(0);
        Double neighborEdgeMips = neighborEdgeNode.getMipsPerCore();
        Double distBetweenNearestToCoud = nearestEdgeNode.getMobilityModel().distanceTo(cloudNode);
        Double distBetweenNearestToNeighbor = nearestEdgeNode.getMobilityModel().distanceTo(neighborEdgeNode);

        TaskNode rootNode = getRootTask(tempTaskList);
        Double ueTonearestDCDist = rootNode.getEdgeDevice().getMobilityModel().distanceTo(nearestEdgeNode);
        this.setRank(rootNode, nearestEdgeMips, neighborEdgeMips, cloudMips, ueTonearestDCDist,
                distBetweenNearestToNeighbor, distBetweenNearestToCoud);

//        for(TaskNode taskNode : tempTaskList){
//            System.out.println("Task: " + taskNode.getId() + " Rank: " + taskNode.getRank());
//        }
    }

    private TaskNode getRootTask(List<TaskNode> tempTaskList){
        TaskNode rootNode = null;
        for(TaskNode taskNode : tempTaskList) {
            if(taskNode.isStartTask()){
                rootNode = taskNode;
                break;
            }
        }

        return  rootNode;
    }

    private void setRank(TaskNode taskNode, Double nearestEdgeMips, Double neighborEdgeMips, Double cloudMips,
                 Double ueTonearestDCDist, Double distBetweenNearestToNeighbor, Double distBetweenNearestToCoud){
        Double nearestET = taskNode.getLength() / nearestEdgeMips;
        Double neighborET = taskNode.getLength() / neighborEdgeMips;
        Double cloudET = taskNode.getLength() / cloudMips;
        Double ueMips = taskNode.getEdgeDevice().getMipsPerCore();
        Double ueET = taskNode.getLength() / ueMips;

        Double upload_latency = Helper.getWirelessTransmissionLatency(taskNode.getFileSize()) +
                Helper.calculatePropagationDelay(ueTonearestDCDist);
        Double nearToNeighborLatency = Helper.getManEdgeTransmissionLatency(taskNode.getFileSize()) +
                Helper.calculatePropagationDelay(distBetweenNearestToNeighbor);
        Double nearToCloudLatency = Helper.getManCloudTransmissionLatency(taskNode.getFileSize()) +
                Helper.calculatePropagationDelay(distBetweenNearestToCoud);

        Double ET_near = nearestET + upload_latency;
        Double ET_neighbor = neighborET + nearToNeighborLatency + upload_latency;
        Double ET_cloud = cloudET + nearToCloudLatency + upload_latency;

        Double S_avg = (ET_near + ET_neighbor + ET_cloud)/3;
        Double T_avg = (S_avg + ueET)/2;

        if(taskNode.isStartTask()){
            taskNode.setRank((int) Math.ceil(T_avg));
        } else{
            Integer pred_max_rank = findMaxPredecRank(taskNode.predecessors);
            taskNode.setRank((int) Math.round(T_avg) + pred_max_rank);
        }

        taskNode.setExecutionTime(T_avg);
        if(taskNode.isEndTask()){
            taskNode.setExecutionTime(T_avg);
            return;
        }

        for(TaskNode successor : taskNode.successors){
            setRank(successor, nearestEdgeMips, neighborEdgeMips, cloudMips, ueTonearestDCDist,
                    distBetweenNearestToNeighbor, distBetweenNearestToCoud);
        }
    }

    private Integer findMaxPredecRank(List<TaskNode>   predecessors){
        Integer max_rank = Integer.MIN_VALUE;
        for(TaskNode pred : predecessors){
            if(pred.getRank() == null){
                continue;
            }
            if(max_rank < pred.getRank()){
                max_rank = pred.getRank();
            }
        }

        return max_rank;
    }

    private void intelligentSchedulingDecision(List<TaskNode> tempTaskList){
        DataCenter dc = simManager.getDataCentersManager().getEdgeDatacenterList().get(0);
        ComputingNode computingNode = dc.nodeList.get(1);

        for(TaskNode taskNode : tempTaskList) {
            double localComputingTime = taskNode.getLength() / taskNode.getEdgeDevice().getMipsPerCore();
            EnergyModelComputingNode energyModelComputingNode = taskNode.getEdgeDevice().getEnergyModel();
            double local_dynaEnergy = ((energyModelComputingNode.getMaxActiveConsumption() - energyModelComputingNode.getIdleConsumption()) / 3600 * taskNode.getLength() / taskNode.getEdgeDevice().getMipsPerCore());
            double local_staticEnergy = energyModelComputingNode.getIdleConsumption() / 3600 * SimulationParameters.UPDATE_INTERVAL;
            double local_energyConsumption = local_dynaEnergy + local_staticEnergy;


            double transmission_latency = calculateTransmissionLatency(taskNode, computingNode);
            double mecComputingTime = Helper.calculateExecutionTime_new(computingNode, taskNode) + transmission_latency;

            double remote_energy = SimulationParameters.CELLULAR_DEVICE_TRANSMISSION_WATTHOUR_PER_BIT*taskNode.getFileSize() + SimulationParameters.CELLULAR_DEVICE_RECEPTION_WATTHOUR_PER_BIT*taskNode.getOutputSize();

            double totalMecCost = 0.9*mecComputingTime + 0.1*remote_energy;
            double totalLocalCost = 0.9*localComputingTime + 0.1*local_energyConsumption;

            if(totalLocalCost < totalMecCost){
                taskNode.setTaskDecision(TaskNode.TaskDecision.UE_ONLY);
                //ue_counter++;
            } else{
                taskNode.setTaskDecision(TaskNode.TaskDecision.MEC_ONLY);
            }
        }
    }

    private double calculateTransmissionLatency(Task task, ComputingNode computingNode){
        double distance = task.getEdgeDevice().getMobilityModel().distanceTo(computingNode);
        double upload_latency = Helper.getWirelessTransmissionLatency(task.getFileSize()) + Helper.calculatePropagationDelay(distance);;
        double download_latency = Helper.getWirelessTransmissionLatency(task.getOutputSize()) + Helper.calculatePropagationDelay(distance);
        return upload_latency + download_latency;
    }

    //Calculating sub deadline for each of the task in an application represented by DAG
    private void calculateAndAssignSubDeadline(List<TaskNode> tempTaskList, Double app_deadline){
        TaskNode rootNode = getRootTask(tempTaskList);
        List<Set<TaskNode>> labelList = new ArrayList<Set<TaskNode>>();
        Set<TaskNode> label = new HashSet<>();
        label.add(rootNode);
        labelList.add(label);
        Queue<TaskNode> queue = new LinkedList<>();
        queue.add(rootNode);

        int curr_label_idx = 0;
        //Finding nodes on all the labels
        while(!queue.isEmpty()){
            int currSize = queue.size();
            Set<TaskNode> new_label = new HashSet<>();
            for(int i=0; i<currSize;i++){
                TaskNode curTask = queue.poll();
                if(isAllpredInPrevLabel(curTask, labelList.get(curr_label_idx))){
                    new_label.add(curTask);
                }
                for(TaskNode taskNode : curTask.successors){
                    queue.add(taskNode);
                }
            }
            labelList.add(new_label);
            curr_label_idx++;
        }

        //Calculating lable ratio for each label
        Map<Integer, Integer> label_ratio_map = new HashMap<>();
        for(int i = 0; i<labelList.size();i++){
            Set<TaskNode> labelNodes = labelList.get(i);
            Double max_et = Double.MIN_VALUE;
            for(TaskNode labelTask : labelNodes){
                if(labelTask.getExecutionTime() > max_et){
                    max_et = labelTask.getExecutionTime();
                }
            }

            if(max_et == Double.MIN_VALUE || max_et == 0){
                max_et = 1.0;
            }

            label_ratio_map.put(i, (int) Math.ceil(max_et));
        }

        Integer total_label_ratio = 0;
        for(Map.Entry<Integer, Integer> label_ratio :  label_ratio_map.entrySet()){
            total_label_ratio += label_ratio.getValue();
        }

        for(int i = 0; i < labelList.size();i++){
            Set<TaskNode> label_nodes = labelList.get(i);
            Integer curr_lable_ratio = label_ratio_map.get(i);
            Double sub_deadline = (curr_lable_ratio*app_deadline)/total_label_ratio;
            for(TaskNode taskNode : label_nodes){
                taskNode.setSubDeadline(sub_deadline);
            }
        }
    }

    private boolean isAllpredInPrevLabel(TaskNode taskNode,Set<TaskNode> prevLabel){
        boolean ret = true;
        for(TaskNode task : taskNode.predecessors){
            boolean flag = false;
            for(TaskNode prevLabelTask : prevLabel){
                if(task.getId() == prevLabelTask.getId()){
                    flag = true;
                }
            }
            if(!flag){
                ret = false;
                break;
            }
        }
        return ret;
    }
}

