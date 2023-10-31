import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv('results.csv')

surplusStrategy = df.loc[:, df.columns.str.contains('Surplus')]
plt.boxplot(surplusStrategy, labels=surplusStrategy.columns)
plt.xticks(fontsize=6)
plt.xticks(rotation=35)
plt.show()

bestSurplusTuning = surplusStrategy.mean().idxmin()
print("Best tuning for Surplus strategy:", bestSurplusTuning)

indexStrategy = df.loc[:, df.columns.str.contains('Index')]
plt.boxplot(indexStrategy, labels=indexStrategy.columns)
plt.xticks(fontsize=6)
plt.xticks(rotation=35)
plt.show()

bestIndexTuning = indexStrategy.mean().idxmin()
print("Best tuning for Index strategy:", bestIndexTuning)

maxTasksStrategy = df.loc[:, df.columns.str.contains('MaxTasks')]
plt.boxplot(maxTasksStrategy, labels=maxTasksStrategy.columns)
plt.xticks(fontsize=6)
plt.xticks(rotation=35)
plt.show()

bestMaxTasksTuning = maxTasksStrategy.mean().idxmin()
print("Best tuning for Index strategy:", bestMaxTasksTuning)

new_df = pd.DataFrame({'Sequential': df['Sequential'], 
                       bestSurplusTuning: df[bestSurplusTuning], 
                       bestIndexTuning: df[bestIndexTuning], 
                       bestMaxTasksTuning: df[bestMaxTasksTuning]})

plt.boxplot(new_df, labels=new_df.columns)
plt.xticks(fontsize=6)
plt.xticks(rotation=35)
plt.show()