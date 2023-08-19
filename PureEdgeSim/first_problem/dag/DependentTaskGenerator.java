package first_problem.dag;

import com.mechalikh.pureedgesim.datacentersmanager.ComputingNode;
import com.mechalikh.pureedgesim.scenariomanager.SimulationParameters;
import com.mechalikh.pureedgesim.simulationmanager.SimulationManager;
import com.mechalikh.pureedgesim.taskgenerator.Task;
import com.mechalikh.pureedgesim.taskgenerator.TaskGenerator;
import data_processor.DataProcessor;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class DependentTaskGenerator extends TaskGenerator {
    private double simulationTime;
    public DependentTaskGenerator(SimulationManager simulationManager) {
        super(simulationManager);
    }
    @Override
    public List<Task> generate() {
//        LoadTasks loadTasks = new LoadTasks("PureEdgeSim/first_problem/dag_data", getSimulationManager(), devicesList);
//        Map<Integer, List<TaskNode>> dags = loadTasks.load();
//        for(Map.Entry<Integer, List<TaskNode>> entry : dags.entrySet()){
//            taskList.addAll(entry.getValue());
//        }
        DataProcessor dataProcessor = new DataProcessor(getSimulationManager(), devicesList);
        return dataProcessor.getTaskList();
    }
}
