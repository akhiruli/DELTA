package first_problem;

import com.mechalikh.pureedgesim.simulationmanager.Simulation;
import first_problem.dag.DependentTaskGenerator;

public class FirstProblem {
    //private static String settingsPath = "PureEdgeSim/first_problem/settings/";
//    private static String outputPath = "PureEdgeSim/first_problem/output/";
    //private static String settingsPath = "PureEdgeSim/first_problem/random_roundrobin/";
    //private static String settingsPath = "PureEdgeSim/first_problem/multi_user_scheduling/";
    private static String settingsPath = "PureEdgeSim/first_problem/intelligent_to/";
    private static String outputPath = "PureEdgeSim/first_problem/output/";
    public FirstProblem(){
        Simulation sim = new Simulation();
        sim.setCustomOutputFolder(outputPath);
        sim.setCustomSettingsFolder(settingsPath);
        sim.setCustomTaskGenerator(DependentTaskGenerator.class);
        sim.setCustomEdgeOrchestrator(CustomOrchestrator.class);
        sim.launchSimulation();;
    }

    public static void main(String args[]){
        new FirstProblem();
    }
}
