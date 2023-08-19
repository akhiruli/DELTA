package data_processor;

public class Job {
    private boolean status;
    private String JobID_InDB;
    private String JobID;
    private String OwnerUserID;
    private String NodeID_SubmittedOn;
    private String TimeSubmission;
    private String TimeDeadLinePrefered;
    private String TimeDeadlineFinal;
    private String ListOfTasks;
    private String LastStatus;
    private String LastStatusTime;
    private String JobSizeWithoutAccountForParallelism;
    private String CountOfEdges;
    private String CountOfTasks;
    private String SumOfEdgeWeight;
    private String MaxEdgeWeight;
    private String MinEdgeWeight;
    private String MaxTaskSize;
    private String MinTaskSize;
    private String MaxParallelExecutableTasks;
    private String JobInitSourceTypeID;
    private String TasksWhichCanRunInParallel;
    private String MinStorageNeeded;
    private String TotalStorageNeeded;
    private String MaxRamNeededForParallelExec;
    private String MinRamNeeded;
    private String TotalRamNeeded;
    private String TaskIDsOfHighestsCPULoadPartOfTheDag;
    private String MaxRamNeededForParallelExecutionOfHighestCpuLoadPartOfTheDag;
    private String MinimumPassThrough_MI_withHighestParallelism;
    private String MinTimeForExecuteDagWithMaxParallelismOnCloud;
    private String MinTimeForExecuteDagWithMaxParallelismOnFog;
    private String MinTimeForExecuteDagWithMaxParallelismOnIoT;
    private String MaxTimeForExecuteDagSerialOnCloud;
    private String MinTimeForExecuteDagSerialOnFog;
    private String MinTimeForExecuteDagSerialOnIoT;
    private String EFT_DAG;
    private Integer taskAvgLength;

    public Integer getAppLatencyDeadline() {
        return appLatencyDeadline;
    }

    public void setAppLatencyDeadline(Integer appLatencyDeadline) {
        this.appLatencyDeadline = appLatencyDeadline;
    }

    private Integer appLatencyDeadline;
    public Job(){
        status = false;
    }
    public Integer getAvgTaskLength() {
        return taskAvgLength/100;
    }

    public void setAvgTaskLength() {
        this.taskAvgLength = (Integer.valueOf(getMinTaskSize()) + Integer.valueOf(getMaxTaskSize()))/2;
    }


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    public String getJobID_InDB() {
        return JobID_InDB;
    }

    public void setJobID_InDB(String jobID_InDB) {
        JobID_InDB = jobID_InDB;
    }

    public String getJobID() {
        return JobID;
    }

    public void setJobID(String jobID) {
        JobID = jobID;
    }

    public String getOwnerUserID() {
        return OwnerUserID;
    }

    public void setOwnerUserID(String ownerUserID) {
        OwnerUserID = ownerUserID;
    }

    public String getNodeID_SubmittedOn() {
        return NodeID_SubmittedOn;
    }

    public void setNodeID_SubmittedOn(String nodeID_SubmittedOn) {
        NodeID_SubmittedOn = nodeID_SubmittedOn;
    }

    public String getTimeSubmission() {
        return TimeSubmission;
    }

    public void setTimeSubmission(String timeSubmission) {
        TimeSubmission = timeSubmission;
    }

    public String getTimeDeadLinePrefered() {
        return TimeDeadLinePrefered;
    }

    public void setTimeDeadLinePrefered(String timeDeadLinePrefered) {
        TimeDeadLinePrefered = timeDeadLinePrefered;
    }

    public String getTimeDeadlineFinal() {
        return TimeDeadlineFinal;
    }

    public void setTimeDeadlineFinal(String timeDeadlineFinal) {
        TimeDeadlineFinal = timeDeadlineFinal;
    }

    public String getListOfTasks() {
        return ListOfTasks;
    }

    public void setListOfTasks(String listOfTasks) {
        ListOfTasks = listOfTasks;
    }

    public String getLastStatus() {
        return LastStatus;
    }

    public void setLastStatus(String lastStatus) {
        LastStatus = lastStatus;
    }

    public String getLastStatusTime() {
        return LastStatusTime;
    }

    public void setLastStatusTime(String lastStatusTime) {
        LastStatusTime = lastStatusTime;
    }

    public String getJobSizeWithoutAccountForParallelism() {
        return JobSizeWithoutAccountForParallelism;
    }

    public void setJobSizeWithoutAccountForParallelism(String jobSizeWithoutAccountForParallelism) {
        JobSizeWithoutAccountForParallelism = jobSizeWithoutAccountForParallelism;
    }

    public String getCountOfEdges() {
        return CountOfEdges;
    }

    public void setCountOfEdges(String countOfEdges) {
        CountOfEdges = countOfEdges;
    }

    public String getCountOfTasks() {
        return CountOfTasks;
    }

    public void setCountOfTasks(String countOfTasks) {
        CountOfTasks = countOfTasks;
    }

    public String getSumOfEdgeWeight() {
        return SumOfEdgeWeight;
    }

    public void setSumOfEdgeWeight(String sumOfEdgeWeight) {
        SumOfEdgeWeight = sumOfEdgeWeight;
    }

    public String getMaxEdgeWeight() {
        return MaxEdgeWeight;
    }

    public void setMaxEdgeWeight(String maxEdgeWeight) {
        MaxEdgeWeight = maxEdgeWeight;
    }

    public String getMinEdgeWeight() {
        return MinEdgeWeight;
    }

    public void setMinEdgeWeight(String minEdgeWeight) {
        MinEdgeWeight = minEdgeWeight;
    }

    public String getMaxTaskSize() {
        return MaxTaskSize;
    }

    public void setMaxTaskSize(String maxTaskSize) {
        MaxTaskSize = maxTaskSize;
    }

    public String getMinTaskSize() {
        return MinTaskSize;
    }

    public void setMinTaskSize(String minTaskSize) {
        MinTaskSize = minTaskSize;
    }

    public String getMaxParallelExecutableTasks() {
        return MaxParallelExecutableTasks;
    }

    public void setMaxParallelExecutableTasks(String maxParallelExecutableTasks) {
        MaxParallelExecutableTasks = maxParallelExecutableTasks;
    }

    public String getJobInitSourceTypeID() {
        return JobInitSourceTypeID;
    }

    public void setJobInitSourceTypeID(String jobInitSourceTypeID) {
        JobInitSourceTypeID = jobInitSourceTypeID;
    }

    public String getTasksWhichCanRunInParallel() {
        return TasksWhichCanRunInParallel;
    }

    public void setTasksWhichCanRunInParallel(String tasksWhichCanRunInParallel) {
        TasksWhichCanRunInParallel = tasksWhichCanRunInParallel;
    }

    public String getMinStorageNeeded() {
        return MinStorageNeeded;
    }

    public void setMinStorageNeeded(String minStorageNeeded) {
        MinStorageNeeded = minStorageNeeded;
    }

    public String getTotalStorageNeeded() {
        return TotalStorageNeeded;
    }

    public void setTotalStorageNeeded(String totalStorageNeeded) {
        TotalStorageNeeded = totalStorageNeeded;
    }

    public String getMaxRamNeededForParallelExec() {
        return MaxRamNeededForParallelExec;
    }

    public void setMaxRamNeededForParallelExec(String maxRamNeededForParallelExec) {
        MaxRamNeededForParallelExec = maxRamNeededForParallelExec;
    }

    public String getMinRamNeeded() {
        return MinRamNeeded;
    }

    public void setMinRamNeeded(String minRamNeeded) {
        MinRamNeeded = minRamNeeded;
    }

    public String getTotalRamNeeded() {
        return TotalRamNeeded;
    }

    public void setTotalRamNeeded(String totalRamNeeded) {
        TotalRamNeeded = totalRamNeeded;
    }

    public String getTaskIDsOfHighestsCPULoadPartOfTheDag() {
        return TaskIDsOfHighestsCPULoadPartOfTheDag;
    }

    public void setTaskIDsOfHighestsCPULoadPartOfTheDag(String taskIDsOfHighestsCPULoadPartOfTheDag) {
        TaskIDsOfHighestsCPULoadPartOfTheDag = taskIDsOfHighestsCPULoadPartOfTheDag;
    }

    public String getMaxRamNeededForParallelExecutionOfHighestCpuLoadPartOfTheDag() {
        return MaxRamNeededForParallelExecutionOfHighestCpuLoadPartOfTheDag;
    }

    public void setMaxRamNeededForParallelExecutionOfHighestCpuLoadPartOfTheDag(String maxRamNeededForParallelExecutionOfHighestCpuLoadPartOfTheDag) {
        MaxRamNeededForParallelExecutionOfHighestCpuLoadPartOfTheDag = maxRamNeededForParallelExecutionOfHighestCpuLoadPartOfTheDag;
    }

    public String getMinimumPassThrough_MI_withHighestParallelism() {
        return MinimumPassThrough_MI_withHighestParallelism;
    }

    public void setMinimumPassThrough_MI_withHighestParallelism(String minimumPassThrough_MI_withHighestParallelism) {
        MinimumPassThrough_MI_withHighestParallelism = minimumPassThrough_MI_withHighestParallelism;
    }

    public String getMinTimeForExecuteDagWithMaxParallelismOnCloud() {
        return MinTimeForExecuteDagWithMaxParallelismOnCloud;
    }

    public void setMinTimeForExecuteDagWithMaxParallelismOnCloud(String minTimeForExecuteDagWithMaxParallelismOnCloud) {
        MinTimeForExecuteDagWithMaxParallelismOnCloud = minTimeForExecuteDagWithMaxParallelismOnCloud;
    }

    public String getMinTimeForExecuteDagWithMaxParallelismOnFog() {
        return MinTimeForExecuteDagWithMaxParallelismOnFog;
    }

    public void setMinTimeForExecuteDagWithMaxParallelismOnFog(String minTimeForExecuteDagWithMaxParallelismOnFog) {
        MinTimeForExecuteDagWithMaxParallelismOnFog = minTimeForExecuteDagWithMaxParallelismOnFog;
    }

    public String getMinTimeForExecuteDagWithMaxParallelismOnIoT() {
        return MinTimeForExecuteDagWithMaxParallelismOnIoT;
    }

    public void setMinTimeForExecuteDagWithMaxParallelismOnIoT(String minTimeForExecuteDagWithMaxParallelismOnIoT) {
        MinTimeForExecuteDagWithMaxParallelismOnIoT = minTimeForExecuteDagWithMaxParallelismOnIoT;
    }

    public String getMaxTimeForExecuteDagSerialOnCloud() {
        return MaxTimeForExecuteDagSerialOnCloud;
    }

    public void setMaxTimeForExecuteDagSerialOnCloud(String maxTimeForExecuteDagSerialOnCloud) {
        MaxTimeForExecuteDagSerialOnCloud = maxTimeForExecuteDagSerialOnCloud;
    }

    public String getMinTimeForExecuteDagSerialOnFog() {
        return MinTimeForExecuteDagSerialOnFog;
    }

    public void setMinTimeForExecuteDagSerialOnFog(String minTimeForExecuteDagSerialOnFog) {
        MinTimeForExecuteDagSerialOnFog = minTimeForExecuteDagSerialOnFog;
    }

    public String getMinTimeForExecuteDagSerialOnIoT() {
        return MinTimeForExecuteDagSerialOnIoT;
    }

    public void setMinTimeForExecuteDagSerialOnIoT(String minTimeForExecuteDagSerialOnIoT) {
        MinTimeForExecuteDagSerialOnIoT = minTimeForExecuteDagSerialOnIoT;
    }

    public String getEFT_DAG() {
        return EFT_DAG;
    }

    public void setEFT_DAG(String EFT_DAG) {
        this.EFT_DAG = EFT_DAG;
    }

}
