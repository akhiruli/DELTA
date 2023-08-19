import matplotlib.pyplot as plt

x_axis = [0, 15, 30, 45, 60, 75, 90, 105, 120, 135, 150]
y_axis_hierarchical = [0, 0, 0.3044, 1.8302, 1.9779, 2.7302,  1.8936, 3.0644, 3.1392, 2.795, 2.811]
y_axis_multi_user_sche = [0, 4.6043, 8.6758, 9.4052, 8.8246, 10.7302, 12.607, 13.7125, 15.4501, 17.523, 19.0341]
y_axis_selective_rand_rr = [0, 2.3022, 2.3592, 2.6436, 3.043, 3.2698, 3.8132, 4.4092, 5.5787, 5.2552, 5.9659]
y_axis_intelligent = [0, 1.295, 2.1309, 2.2302, 3.1571, 2.5397, 2.8534, 3.1101, 3.6687, 3.8661, 4.3511]

plt.plot(x_axis, y_axis_hierarchical, marker='x', label='DELTA', linewidth=1, color='blue')
plt.plot(x_axis, y_axis_multi_user_sche, marker='o', label='Multi-user-scheduling', linewidth=1, color='red')
plt.plot(x_axis, y_axis_selective_rand_rr, marker='^', label='Selective-random-RR', linewidth=1, color='green')
plt.plot(x_axis, y_axis_intelligent, marker='v', label='Intelligent-TO', linewidth=1, color='orange')
plt.title('Task Failure Rate')

plt.ylabel('Task Failure rate (%)')
plt.xlabel('No. of Applications')
plt.legend(loc='upper center')
plt.savefig('task_failure_rate.png', bbox_inches='tight')
#plt.show()
