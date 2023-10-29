import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv('results.csv')
plt.boxplot(df, labels=df.columns)
plt.xticks(fontsize=8)
plt.xticks(rotation=45)
plt.show()
