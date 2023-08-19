We have used PureEdgeSim to simulate our experiment and the details of PureEdgeSIm is given below.
# PureEdgeSim

PureEdgeSim: A simulation framework for performance evaluation of cloud, edge and mist computing environments

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0) [![Build Status](https://travis-ci.com/CharafeddineMechalikh/PureEdgeSim.svg?branch=master)](https://travis-ci.com/CharafeddineMechalikh/PureEdgeSim) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/7bcee5c75c3741b5923e0158c6e79b37)](https://www.codacy.com/gh/CharafeddineMechalikh/PureEdgeSim/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=CharafeddineMechalikh/PureEdgeSim&amp;utm_campaign=Badge_Grade) [![Maven Central](https://img.shields.io/maven-central/v/com.mechalikh/pureedgesim.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.mechalikh%22%20AND%20a:%22pureedgesim%22) [![Codacy Badge](https://app.codacy.com/project/badge/Coverage/7bcee5c75c3741b5923e0158c6e79b37)](https://www.codacy.com/gh/CharafeddineMechalikh/PureEdgeSim/dashboard?utm_source=github.com&utm_medium=referral&utm_content=CharafeddineMechalikh/PureEdgeSim&utm_campaign=Badge_Coverage)

## 📝 Please Cite It As (Kindly do not use the github link, othewise, your citation will not be counted):

Mechalikh, C., Taktak, H., & Moussa, F. (2021). PureEdgeSim: A simulation framework for performance evaluation of cloud, edge and mist computing environments. Computer Science and Information Systems, 18(1), 43-66.

Bibtex:

```groovy
@article{mechalikh2021pureedgesim,
  title={PureEdgeSim: A simulation framework for performance evaluation of cloud, edge and mist computing environments},
  author={Mechalikh, Charafeddine and Taktak, Hajer and Moussa, Faouzi},
  journal={Computer Science and Information Systems},
  volume={18},
  number={1},
  pages={43--66},
  year={2021}
}
```

To set up the project we can use Intellij and need Java as mandatory.
Steps to run the experiment:
1. Need to download the dataset from https://zenodo.org/record/4667690.
2. Provide the dataset directory location in the file DataProcessor.java like below.
   	static final String task_dataset_path = "task_dataset/TaskDetails.txt";
    	static final String job_dataset_path = "task_dataset/JobDetails.json";
3. To run the simulation we have to run the main class FirstProblem.java.
4. To run the different scenarios uncomment one of the following lines below:
    //private static String settingsPath = "PureEdgeSim/first_problem/settings/";
    //private static String settingsPath = "PureEdgeSim/first_problem/random_roundrobin/";
    //private static String settingsPath = "PureEdgeSim/first_problem/multi_user_scheduling/";
    //private static String settingsPath = "PureEdgeSim/first_problem/intelligent_to/";
