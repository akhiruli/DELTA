/**
 *     PureEdgeSim:  A Simulation Framework for Performance Evaluation of Cloud, Edge and Mist Computing Environments 
 *
 *     This file is part of PureEdgeSim Project.
 *
 *     PureEdgeSim is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     PureEdgeSim is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with PureEdgeSim. If not, see <http://www.gnu.org/licenses/>.
 *     
 *     @author Charafeddine Mechalikh
 **/
package com.mechalikh.pureedgesim.simulationmanager;

import java.util.ArrayList;
import java.util.List;

import com.mechalikh.pureedgesim.datacentersmanager.ComputingNode;
import com.mechalikh.pureedgesim.datacentersmanager.DataCentersManager;
import com.mechalikh.pureedgesim.network.NetworkModel;
import com.mechalikh.pureedgesim.scenariomanager.Scenario;
import com.mechalikh.pureedgesim.simulationengine.PureEdgeSim;
import com.mechalikh.pureedgesim.simulationengine.SimEntity;
import com.mechalikh.pureedgesim.simulationvisualizer.SimulationVisualizer;
import com.mechalikh.pureedgesim.taskgenerator.Task;
import com.mechalikh.pureedgesim.taskorchestrator.Orchestrator;

/**
 * The abstract class that is extended by the simulation manager.
 * 
 * @see com.mechalikh.pureedgesim.simulationmanager.SimulationManager
 * 
 * @author Charafeddine Mechalikh
 */
public abstract class SimulationManagerAbstract extends SimEntity {

	protected List<Task> tasksList;
	protected Orchestrator edgeOrchestrator;
	protected DataCentersManager dataCentersManager;
	protected SimulationVisualizer simulationVisualizer;
	protected PureEdgeSim simulation;
	protected int simulationId;
	protected int iteration;
	protected SimLog simLog;
	protected NetworkModel networkModel;
	protected List<? extends ComputingNode> orchestratorsList;
	protected List<Task> finishedTasks = new ArrayList<>();
	protected Scenario scenario;

	/**
	 * Initializes the simulation manager.
	 * 
	 * @see com.mechalikh.pureedgesim.simulationmanager.SimulationThread#startSimulation()
	 * 
	 * @param simLog       The simulation logger
	 * @param pureEdgeSim  The CloudSim simulation engine.
	 * @param simulationId The simulation ID
	 * @param iteration    Which simulation run
	 * @param scenario     The scenario is composed of the algorithm and
	 *                     architecture that are being used, and the number of edge
	 *                     devices.
	 */
	public SimulationManagerAbstract(SimLog simLog, PureEdgeSim pureEdgeSim, int simulationId, int iteration,
			Scenario scenario) {
		super(pureEdgeSim);
		this.simulation = pureEdgeSim;
		this.simLog = simLog;
		this.scenario = scenario;
		this.simulationId = simulationId;
		this.iteration = iteration;

	}

	/**
	 * Sets the data centers manager.
	 * 
	 * @see com.mechalikh.pureedgesim.simulationmanager.SimulationThread#loadModels(SimulationManager simulationManager)
	 * 
	 * @param dataCentersManager The data centers manager that is used in this
	 *                           simulation
	 */
	public void setDataCentersManager(DataCentersManager dataCentersManager) {
		// Get orchestrators list from the server manager
		orchestratorsList = dataCentersManager.getOrchestratorsList();
		this.dataCentersManager = dataCentersManager;
		// Submit vm list to the broker
		simLog.deepLog("SimulationManager- Submitting VM list to the broker");
		// broker.submitVmList(dataCentersManager.getVmList());
	}

	/**
	 * Sets the list of tasks to be offloaded
	 * 
	 * @see com.mechalikh.pureedgesim.simulationmanager.SimulationThread#loadModels(SimulationManager simulationManager)
	 * 
	 * @param tasksList the tasks generated by the tasks generator module.
	 */
	public void setTasksList(List<Task> tasksList) {
		this.tasksList = tasksList;
	}

	/**
	 * Returns the list of generated tasks
	 * 
	 * @see com.mechalikh.pureedgesim.simulationmanager.SimulationThread#loadModels(SimulationManager simulationManager)
	 * 
	 * @return the list of tasks generated by the tasks generator module.
	 */
	public List<Task> getTasksList() {
		return this.tasksList;
	}

	/**
	 * Sets the orchestrator that is used in this simulation. Used when offloading
	 * the tasks.
	 * 
	 * @see com.mechalikh.pureedgesim.simulationmanager.SimulationThread#loadModels(SimulationManager simulationManager)
	 * 
	 * @param edgeOrchestrator the orchestrator.
	 */
	public void setOrchestrator(Orchestrator edgeOrchestrator) {
		this.edgeOrchestrator = edgeOrchestrator;

	}

	/**
	 * Sets the network model that is used in this simulation.
	 * 
	 * @see com.mechalikh.pureedgesim.simulationmanager.SimulationThread#loadModels(SimulationManager simulationManager)
	 * 
	 * @param networkModel the network model.
	 */
	public void setNetworkModel(NetworkModel networkModel) {
		this.networkModel = networkModel;
	}

	/**
	 * Returns the iteration number.
	 * 
	 * @see com.mechalikh.pureedgesim.simulationmanager.SimulationThread#startSimulation()
	 * 
	 * @return The iteration number.
	 */
	public int getIteration() {
		return iteration;
	}

	/**
	 * Returns the network model.
	 * 
	 * @see com.mechalikh.pureedgesim.simulationmanager.SimulationThread#loadModels(SimulationManager simulationManager)
	 * 
	 * @return The network model.
	 */
	public NetworkModel getNetworkModel() {
		return networkModel;
	}

	/**
	 * Returns the simulation ID.
	 * 
	 * @see com.mechalikh.pureedgesim.simulationmanager.SimulationThread#startSimulation()
	 * 
	 * @return The simulation ID.
	 */
	public int getSimulationId() {
		return simulationId;
	}

	/**
	 * Returns the simulation logger.
	 * 
	 * @see com.mechalikh.pureedgesim.simulationmanager.SimulationThread#startSimulation()
	 * @see com.mechalikh.pureedgesim.simulationmanager.SimLog
	 * 
	 * @return The simulation logger.
	 */
	public SimLog getSimulationLogger() {
		return simLog;
	}

	/**
	 * Returns the data centers manager that is used in this simulation.
	 * 
	 * @see com.mechalikh.pureedgesim.simulationmanager.SimulationThread#loadModels(SimulationManager simulationManager)
	 * 
	 * @return The data centers manager.
	 */
	public DataCentersManager getDataCentersManager() {
		return this.dataCentersManager;
	}

	/**
	 * Returns the name of orchestration algorithm, the architecture in use, as well
	 * as the number of devices.
	 * 
	 * @return The simulation scenario.
	 */
	public Scenario getScenario() {
		return scenario;
	}
	
}
