import matplotlib.pyplot as plt
import numpy as np

barWidth = 0.2
fig = plt.subplots(figsize =(12, 8))

hierarchical = [6.45, 1.37, 19.88]
multi_scheduling = [12.10, 1.4171, 23.33]
rr = [7.68, 1.39, 22.47]
intelligent = [12.4918, 1.6314, 11.8805]

br1 = np.arange(len(hierarchical))
br2 = [x + barWidth for x in br1]
br3 = [x + barWidth for x in br2]
br4 = [x + barWidth for x in br3]

plt.bar(br1, hierarchical, color ='c', width = barWidth,
        edgecolor ='grey', label ='DELTA')
plt.bar(br2, multi_scheduling, color ='g', width = barWidth,
        edgecolor ='grey', label ='Multi-user-scheduling')
plt.bar(br3, rr, color ='b', width = barWidth,
        edgecolor ='grey', label ='Selective-random-RR')
plt.bar(br4, intelligent, color ='orange', width = barWidth,
        edgecolor ='grey', label ='Intelligent-TO')

plt.xlabel('Task Mix', fontweight ='bold', fontsize = 15)
plt.ylabel('Average Energy Consumption at UE(In joules)', fontweight ='bold', fontsize = 15)
plt.xticks([r + barWidth for r in range(len(hierarchical))],
        ['50% CPU-intensive tasks', '50% IO-intensive tasks', '50% Memory-intensive tasks'])
 
plt.legend()
plt.savefig('energy_bar.png', bbox_inches='tight')
#plt.show()
