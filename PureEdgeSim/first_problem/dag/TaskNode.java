package first_problem.dag;

import com.mechalikh.pureedgesim.taskgenerator.Task;

import java.util.ArrayList;
import java.util.List;


public class TaskNode extends Task{
    public enum TaskType {
        NONE,
        NORMAL,
        IO_INTENSIVE,
        CPU_INTENSIVE,
        MEM_INTENSIVE
    };

    public enum TaskDecision{
        UE_ONLY,
        MEC_ONLY,
        OPEN
    };
    public List<TaskNode>   predecessors;
    public List<TaskNode>   successors;
    List<Integer> predecessorsId;
    List<Integer> successorsId;
    private boolean taskDone;
    private Integer level;
    private boolean startTask;
    private boolean endTask;
    private TaskType taskType;
    private boolean isDummyTask;

    public Double getSubDeadline() {
        return subDeadline;
    }

    public void setSubDeadline(Double subDeadline) {
        this.subDeadline = subDeadline;
    }

    private Double subDeadline;

    public double getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(double executionTime) {
        this.executionTime = executionTime;
    }

    private double executionTime;

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    private Integer rank;
    private TaskDecision taskDecision;

    public TaskDecision getTaskDecision() {
        return taskDecision;
    }

    public void setTaskDecision(TaskDecision taskDecision) {
        this.taskDecision = taskDecision;
    }

    public boolean isDummyTask() {
        return isDummyTask;
    }

    public void setDummyTask(boolean dummyTask) {
        isDummyTask = dummyTask;
    }


    public TaskNode(int id, long length){
        super(id, length);
        predecessors = new ArrayList<>();
        successors = new ArrayList<>();
        successorsId = new ArrayList<>();
        predecessorsId = new ArrayList<>();
        taskDone = false;
        level = 0;
        startTask = false;
        endTask = false;
        taskType = TaskType.NORMAL;
        isDummyTask = false;
        taskDecision = TaskDecision.OPEN;
    }
    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public boolean isStartTask() {
        return startTask;
    }

    public void setStartTask(boolean startTask) {
        this.startTask = startTask;
    }

    public boolean isEndTask() {
        return endTask;
    }

    public void setEndTask(boolean endTask) {
        this.endTask = endTask;
    }

    public boolean isTaskDone() {
        return taskDone;
    }

    public void setTaskDone(boolean taskDone) {
        this.taskDone = taskDone;
    }
    public List<Integer> getPredecessorsId() {
        return predecessorsId;
    }

    public void setPredecessorsId(List<Integer> predecessorsId) {
        this.predecessorsId = predecessorsId;
    }

    public List<Integer> getSuccessorsId() {
        return successorsId;
    }

    public void setSuccessorsId(List<Integer> successorsId) {
        this.successorsId = successorsId;
    }

}
