import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv('results.csv')

# surplus_strategy = df.loc[:, df.columns.str.contains('Surplus')]
# print(surplus_strategy)

# plt.boxplot(surplus_strategy, labels=surplus_strategy.columns)
# plt.xticks(fontsize=6)
# plt.xticks(rotation=35)
# plt.show()

# Exibir o boxplot com todas as colunas
plt.boxplot(df, labels=df.columns)
plt.xticks(fontsize=6)
plt.xticks(rotation=35)
plt.show()
