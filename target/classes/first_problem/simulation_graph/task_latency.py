import matplotlib.pyplot as plt

x_axis = [0, 15, 30, 45, 60, 75, 90, 105, 120, 135, 150]
y_axis_hierarchical = [0, 35.5437, 56.532, 148.985, 172.1745, 210.669, 230.3322, 297.8338, 368.3758, 349.7493, 400.4118]
y_axis_multi_user_sche = [0, 263.5119, 542.8664, 797.8658, 1092.4709, 1090.1861, 1657.8044, 1917.2329, 2184.9578, 2493.9401, 2809.5154]
y_axis_selective_rand_rr = [0, 125.5311, 167.9996, 336.5144, 489.7127, 602.2997, 668.0331, 722.5413, 702.6767, 735.4565, 832.5837]
y_axis_intelligent = [0, 76.4853, 150.6696, 199.4903, 246.4698, 330.7941, 355.5306, 402.7573, 499.991, 518.714, 621.1739]

plt.plot(x_axis, y_axis_hierarchical, marker='x', label='DELTA', linewidth=1, color='blue')
plt.plot(x_axis, y_axis_multi_user_sche, marker='o', label='Multi-user-scheduling', linewidth=1, color='red')
plt.plot(x_axis, y_axis_selective_rand_rr, marker='^', label='Selective-random-RR', linewidth=1, color='green')
plt.plot(x_axis, y_axis_intelligent, marker='v', label='Intelligent-TO', linewidth=1, color='orange')
plt.title('Average Latency')

plt.ylabel('Average Latency of a Task(In secs)')
plt.xlabel('No. of Appliacations')
plt.legend(loc='upper center')
plt.savefig('task_latency.png', bbox_inches='tight')
#plt.show()
