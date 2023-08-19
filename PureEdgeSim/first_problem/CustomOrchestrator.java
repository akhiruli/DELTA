package first_problem;

import com.mechalikh.pureedgesim.datacentersmanager.ComputingNode;
import com.mechalikh.pureedgesim.datacentersmanager.DataCenter;
import com.mechalikh.pureedgesim.scenariomanager.SimulationParameters;
import com.mechalikh.pureedgesim.simulationmanager.SimulationManager;
import com.mechalikh.pureedgesim.taskgenerator.Task;
import com.mechalikh.pureedgesim.taskorchestrator.Orchestrator;
import data_processor.DataProcessor;
import data_processor.Job;
import first_problem.dag.TaskNode;

import java.util.*;

import static com.mechalikh.pureedgesim.scenariomanager.SimulationParameters.TYPES.EDGE_DEVICE;

public class CustomOrchestrator extends Orchestrator {
    protected Map<Integer, Integer> historyMap = new HashMap<>();
    private SimulationManager simManager;
    private Integer currServerIndex;

    public CustomOrchestrator(SimulationManager simulationManager) {
        super(simulationManager);
        simManager = simulationManager;
        // Initialize the history map
        for (int i = 0; i < nodeList.size(); i++)
            historyMap.put(i, 0);
        currServerIndex = 0;
    }

    protected ComputingNode findComputingNode(String[] architecture, Task task) {
        if ("ROUND_ROBIN".equals(algorithm)) {
            return roundRobin(architecture, task);
        } else if ("TRADE_OFF".equals(algorithm)) {
            return tradeOff(architecture, task);
        } else if ("HIERARCHICAL_ALGO".equals(algorithm)) {
            //System.out.println("AKHIRUL-HIERARCHICAL_ALGO");
            return schedulingAlgo(task);
        } else if ("DEVICE_ONLY".equals(algorithm)) {
            return deviceOnlyScheduling(task);
        } else if ("EDGE_ONLY".equals(algorithm)) {
            return edgeOnlyScheduling(task);
        } else if ("MULTI_USER_SCHEDULING".equals(algorithm)){
            return multiUserScheduling(task);
        } else if("RANDOM_RR".equals(algorithm)) {
            return randomRRPolicy(task);
        } else if ("INTELIGENT_TO".equals(algorithm)) {
            return intelligentToPolicy(task);
        } else {
            throw new IllegalArgumentException(getClass().getSimpleName() + " - Unknown orchestration algorithm '"
                    + algorithm + "', please check the simulation parameters file...");
        }
    }
    private ComputingNode intelligentToPolicy(Task task){
        if(!task.getEdgeDevice().isSensor()) {
            if (((TaskNode) task).isDummyTask() ||
                    ((TaskNode) task).getTaskDecision() == TaskNode.TaskDecision.UE_ONLY){
                return task.getEdgeDevice();
            }
        }


        DataCenter dc = simManager.getDataCentersManager().getEdgeDatacenterList().get(0);
        Integer serverIndex = Helper.getRandomInteger(0, 8);
        return  dc.nodeList.get(serverIndex);
    }
    private ComputingNode randomRRPolicy(Task task){
        if(!task.getEdgeDevice().isSensor()) {
            if (((TaskNode) task).isDummyTask() ||
                    ((TaskNode) task).getTaskDecision() == TaskNode.TaskDecision.UE_ONLY){
                return task.getEdgeDevice();
            }
        }


        if(this.currServerIndex >= simManager.getDataCentersManager().getEdgeDatacenterList().size()){
            currServerIndex = 0; //RR selection DC
        }
        DataCenter dc = simManager.getDataCentersManager().getEdgeDatacenterList().get(currServerIndex);
        ++currServerIndex;
        Integer serverIndex = Helper.getRandomInteger(0, dc.nodeList.size()-1);
        return  dc.nodeList.get(serverIndex);
    }
    private ComputingNode multiUserScheduling(Task task){
        if(!task.getEdgeDevice().isSensor()) {
            if (((TaskNode) task).isDummyTask() ||
                    ((TaskNode) task).getTaskDecision() == TaskNode.TaskDecision.UE_ONLY){
                return task.getEdgeDevice();
            }
        }

        List<DataCenter> edgeDcs = simManager.getDataCentersManager().getEdgeDatacenterList();
        List<ComputingNode> serverList = edgeDcs.get(0).nodeList;
        if(this.currServerIndex >= serverList.size()){
            currServerIndex = 0;
        }

        ComputingNode node = serverList.get(currServerIndex);
        ++currServerIndex;
        return node;
    }

    private ComputingNode deviceOnlyScheduling(Task task) {
        return task.getEdgeDevice();
    }

    private ComputingNode edgeOnlyScheduling(Task task) {
        DataCenter nearestDc = findNearestEdgeDC(task);
        ComputingNode node = this.getBestServer(nearestDc.nodeList, (TaskNode) task);
        double distance = task.getEdgeDevice().getMobilityModel().distanceTo(nearestDc.nodeList.get(0));
        this.updateTransmissionLatency(task, 0, distance);
        return node;
    }

    protected ComputingNode tradeOff(String[] architecture, Task task) {
        int selected = -1;
        double min = -1;
        double new_min;// the computing node with minimum weight;
        ComputingNode node;
        // get best computing node for this task
        for (int i = 0; i < nodeList.size(); i++) {
            node = nodeList.get(i);
            if(task.getId() == 0 && task.getEdgeDevice() == node){
                selected = i;
                break;
            }
            if (offloadingIsPossible(task, node, architecture)) {
                // the weight below represent the priority, the less it is, the more it is
                // suitable for offlaoding, you can change it as you want
                double weight = 1.2; // this is an edge server 'cloudlet', the latency is slightly high then edge
                // devices
                if (node.getType() == SimulationParameters.TYPES.CLOUD) {
                    weight = 1.8; // this is the cloud, it consumes more energy and results in high latency, so
                    // better to avoid it
                } else if (node.getType() == EDGE_DEVICE) {
                    weight = 1.3;// this is an edge device, it results in an extremely low latency, but may
                    // consume more energy.
                }
                new_min = (historyMap.get(i) + 1) * weight * task.getLength() / node.getMipsPerCore();
                if (min == -1 || min > new_min) { // if it is the first iteration, or if this computing node has more
                    // cpu mips and
                    // less waiting tasks
                    min = new_min;
                    // set the first computing node as the best one
                    selected = i;
                }
            }
        }
        if (selected != -1) {
            historyMap.put(selected, historyMap.get(selected) + 1);
            node = nodeList.get(selected);
        } else{
            node = null;
        }
        // assign the tasks to the selected computing node

        return node;
    }

    protected ComputingNode roundRobin(String[] architecture, Task task) {
        int selected = -1;
        int minTasksCount = -1; // Computing node with minimum assigned tasks.
        ComputingNode node = null;
        for (int i = 0; i < nodeList.size(); i++) {
            if (offloadingIsPossible(task, nodeList.get(i), architecture)
                    && (minTasksCount == -1 || minTasksCount > historyMap.get(i))) {
                minTasksCount = historyMap.get(i);
                // if this is the first time,
                // or new min found, so we choose it as the best computing node.
                selected = i;
            }
        }
        // Assign the tasks to the obtained computing node.
        historyMap.put(selected, minTasksCount + 1);
        if(selected != -1){
            node = nodeList.get(selected);
        }
        return node;
    }

    @Override
    public void resultsReturned(Task task) {
        TaskNode taskNode = (TaskNode) task;
        taskNode.setTaskDone(true);
        CustomSimMananger customSimMananger = (CustomSimMananger) simManager;
        if(task.getStatus() == Task.Status.FAILED) {
            System.out.println("Task " + task.getId() + " failed for job " + task.getApplicationID() + " CPU: " + task.getLength() + " node type: " + task.getComputingNode().getType() + " ID:" + task.getComputingNode().getId() + " Reason : " + task.getFailureReason());
        }

        if(taskNode.isEndTask()) {
            Job app = DataProcessor.scheduledJob.get(taskNode.getApplicationID());
            app.setStatus(true);

            if (customSimMananger.isAllDagCompleted()){
                customSimMananger.genReport();
            }
        } else if(!taskNode.isEndTask()) {
            customSimMananger.scheduleSuccessors(taskNode.successors);
        }

    }

    public ComputingNode schedulingAlgo(Task task){
        ComputingNode node = null;
        if(!task.getEdgeDevice().isSensor()) {
            if (((TaskNode) task).isDummyTask() ||
                ((TaskNode) task).getTaskDecision() == TaskNode.TaskDecision.UE_ONLY){
                return task.getEdgeDevice();
            }
        }

        DataCenter nearestDc = findNearestEdgeDC(task);

        if(nearestDc == null){
            return task.getEdgeDevice();
        }

        List<DataCenter> dcList = new ArrayList<>();
        dcList.add(nearestDc);
        List<ComputingNode> serverList = getServerList(dcList, ((TaskNode)task).getTaskType(), task);
        node = this.getBestServer(serverList, (TaskNode) task);

        if(node == null){ //no suitable server in nearest DC
            List<DcDistanceIndexPair> dcDistanceIndexPairs = sortDcWithDistance(nearestDc);
            dcList.clear();
            for(DcDistanceIndexPair dcDistanceIndexPair : dcDistanceIndexPairs){
                DataCenter nextNearestDc = simManager.getDataCentersManager().getEdgeDatacenterList().get(dcDistanceIndexPair.getIndex());
                if(nextNearestDc.equals(nearestDc)) //skipping the nearest one it is already evaluated
                    continue;
                dcList.add(nextNearestDc);
            }

            serverList = getServerList(dcList, ((TaskNode)task).getTaskType(), task);
            node = this.getBestServer(serverList, (TaskNode) task);
            dcDistanceIndexPairs.clear();
        }

        if(node == null){ //at last, we go for cloud
            node = simManager.getDataCentersManager().getCloudDatacentersList().get(0).nodeList.get(0);
        }

        return node;
    }

    List<ComputingNode> getServerList(List<DataCenter> dataCenterList, TaskNode.TaskType taskType, Task task){
        List<ComputingNode> nodeList = new ArrayList<>();
        for(DataCenter dc : dataCenterList){
            for(ComputingNode computingNode : dc.nodeList) {
                if(task.getCpuType().equals("GPU") && computingNode.isGpuEnabled()){
                    nodeList.add(computingNode);
                }
                
                if(task.getCpuType().equals("PIM") && computingNode.isPimEnabled()){
                    nodeList.add(computingNode);
                }
                
                if (taskType == TaskNode.TaskType.IO_INTENSIVE && computingNode.isSsdEnabled()) {
                    nodeList.add(computingNode);
                }

                if(taskType == TaskNode.TaskType.NORMAL
                        && !computingNode.isGpuEnabled()
                        && !computingNode.isPimEnabled()
                        && !computingNode.isGpuEnabled()){
                    nodeList.add(computingNode);
                }
            }
        }

        if(nodeList.size() == 0){
            for(DataCenter dc : dataCenterList){
                for(ComputingNode computingNode : dc.nodeList) {
                    nodeList.add(computingNode);
                }
            }
        }
        return nodeList;
    };

    private DataCenter findNearestEdgeDC(Task task){
        DataCenter dataCenter = null;
        List<DataCenter> edgeDcs = simManager.getDataCentersManager().getEdgeDatacenterList();
        for(DataCenter dc : edgeDcs){
            if(sameLocation(dc.nodeList.get(0), task.getEdgeDevice(), SimulationParameters.EDGE_DEVICES_RANGE)
            || (SimulationParameters.ENABLE_ORCHESTRATORS && sameLocation(dc.nodeList.get(0),
                    task.getOrchestrator(), SimulationParameters.EDGE_DEVICES_RANGE))){
                dataCenter = dc;
            }
        }

        return dataCenter;
    }

    private List<DcDistanceIndexPair> sortDcWithDistance(DataCenter nearestDc){
        List<DataCenter> edgeDcs = simManager.getDataCentersManager().getEdgeDatacenterList();
        List<DcDistanceIndexPair> indexDistList = new ArrayList<>();
        for(Integer i = 0; i < edgeDcs.size(); i++){
            Double distance = Helper.calculateDistance(nearestDc, edgeDcs.get(i));
            DcDistanceIndexPair indexDist = new DcDistanceIndexPair();
            indexDist.setDistance(distance);
            indexDist.setIndex(i);
            indexDistList.add(indexDist);
        }

        Collections.sort(indexDistList, new DcDistanceIndexComparator());

        return indexDistList;
    }

    private Integer nearestDC(List<DataCenter> edgeDcs, DataCenter nearestDc){
        double min_dist = Double.MAX_VALUE;
        Integer index = -1;
        for (int i=0; i < edgeDcs.size(); i++) {
            Double distance = Helper.calculateDistance(nearestDc, edgeDcs.get(i));
            if(distance < min_dist){
                min_dist = distance;
                index = i;
            }
        }
        return index;
    }

    private ComputingNode getBestServer(List<ComputingNode> computingNodes, TaskNode task){ //null return means not possible
        ComputingNode schedule_node = null;
        Double min_latency = Double.MAX_VALUE;
        for(ComputingNode computingNode : computingNodes){
            Double computationTime = Helper.calculateExecutionTime(computingNode, task);
            Double transmissionTime = this.calculateTransmissionLatency(task, computingNode);
            Double latency = computationTime + transmissionTime + 0.012*transmissionTime;
            if(latency < task.getMaxLatency() && latency < min_latency) {
                min_latency = latency;
                schedule_node = computingNode;
            }
        }

        return schedule_node;
    }

    private double calculateTransmissionLatency(Task task, ComputingNode computingNode){
        double distance = task.getEdgeDevice().getMobilityModel().distanceTo(computingNode);
        double upload_latency = Helper.getWirelessTransmissionLatency(task.getFileSize()) + Helper.calculatePropagationDelay(distance);
        double download_latency = Helper.getWirelessTransmissionLatency(task.getOutputSize()) + Helper.calculatePropagationDelay(distance);
        return upload_latency + download_latency;
    }

    private void updateTransmissionLatency(Task task, Integer type, double distance){ //0-nearest edge, 1-neighbour edge, 2- cloud
        double upload_latency = 0;
        double download_latency = 0;
        double ul_wir = Helper.getWirelessTransmissionLatency(task.getFileSize());
        double dl_wir = Helper.getWirelessTransmissionLatency(task.getOutputSize());
        if(type == 0){
            upload_latency = ul_wir + Helper.calculatePropagationDelay(distance);
            download_latency = dl_wir + Helper.calculatePropagationDelay(distance);
        } else if(type == 1){
            upload_latency = ul_wir + Helper.getManEdgeTransmissionLatency(task.getFileSize()) + Helper.calculatePropagationDelay(distance);
            download_latency = dl_wir + Helper.getManEdgeTransmissionLatency(task.getOutputSize()) + Helper.calculatePropagationDelay(distance);
        } else if(type == 2){
            upload_latency = ul_wir + Helper.getManCloudTransmissionLatency(task.getFileSize()) + Helper.calculatePropagationDelay(distance);
            download_latency = dl_wir + Helper.getManCloudTransmissionLatency(task.getOutputSize()) + Helper.calculatePropagationDelay(distance);
        } else{
            System.out.println("Invalid Type");
        }

        task.setDownloadTransmissionLatency(download_latency);
        task.setUploadTransmissionLatency(upload_latency);
    }

}
