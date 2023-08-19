import matplotlib.pyplot as plt
import numpy as np

barWidth = 0.2
fig = plt.subplots(figsize =(12, 8))

hierarchical = [91.28, 27.46, 299.55]
multi_scheduling = [350.31, 38.93, 1197.84]
rr = [693.59, 81.28, 825.39]
intelligent = [561.5216, 49.3127, 811.9625]

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
plt.ylabel('Average Latency (In secs)', fontweight ='bold', fontsize = 15)
plt.xticks([r + barWidth for r in range(len(hierarchical))],
        ['50% CPU-intensive tasks', '50% IO-intensive tasks', '50% Memory-intensive tasks'])
 
plt.legend()
plt.savefig('latency_bar.png', bbox_inches='tight')
#plt.show()
