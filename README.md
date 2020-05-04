# vns-ainet-UPMSDSP
Project destinated to the paper "A robust multi-response VNS-aiNET approach for solving scheduling problems under unrelated parallel machines environments"


#EXPERIMENTS CONFIGURATIONS

Every configuration of the experiments, e.g. the directory of instances, number of executions without improvement, etc., must be done in the file ExperimentConfig.java;


#COMPILE AND EXECUTE

To compile goes to ./src directory and execute in command line javac -cp lib/tools.jar -encoding ISO-8859-1  @sources.txt

To execute the algorithm goes to ./src directory and execute in command line java -cp lib/tools.jar:. ExperimentRunner
The path and name of the result files must be configurated in ExperimentConfig.java


#RESULTS

The analytical results showed in the paper to the benchmark instances are avaliable in the directory ./results.


#INSTANCES

The instances used in the paper are not property of the authors. In reason to that and to make possible tests in the algorithm only one instance of each studied case is avaliable in the directory ./instances.

To get the complete set of instances, contact the authors of the original papers.


The Makespan instances are avaliable in http://schedulingresearch.com/ 
Papers: Arnaout, J-P., Rabadi, G.* and Musa, R. (2010) “A Two-stage Ant Colony 
Optimization to Minimize the Makespan on Unrelated Parallel Machines with 
Sequence-Dependent Setup Times”, Journal of Intelligent Manufacturing, 
Vol. 21, No. 6, P. 693 – 701


The Total Weighted Tardiness contact Yang-Kuei Lin - yklin@mail.fcu.edu.tw
Paper: Lin, Y.-K., Hsieh, F.-Y., 2014. Unrelated parallel machine scheduling with
setup times and ready times. International Journal of Production Research
Vol. 52 (4),  P. 1200 – 1214. 


The Total Tardiness and Total Weighted Completion Time contact Jeng-Fung Chen - jfchen@fcu.edu.tw

Papers:
Chen, J.-F., 2009. Scheduling on unrelated parallel machines with sequence-
and machine-dependent setup times and due-date constraints. The Internati-
onal Journal of Advanced Manufacturing Technology Vol. 44 (11), 1204 – 1212.

Chen, J.-F., Dec 2015. Unrelated parallel-machine scheduling to minimize to-
tal weighted completion time. Journal of Intelligent Manufacturing Vol. 26 (6),
1099–1112.


#CONTACT

Any doubt may be emailed to rodneyoliveira@dppg.cefetmg.br or sergio@dppg.cefetmg.br

