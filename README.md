# CoinCounter
**Goal:** The goal of this assignment is to explore the parallelization of a more challenging problem: The problem of computing how a vending machine should give change.

**Context:** Vending machines have a set of coins in their storage. They may have multiples of different coin sizes. For instance, you might have a very unbalanced scenarion with different quantities of 1, 2, 5, 10, 20, 50, 100 and 200 cents coins.

**Algorithm:** Attached is a sequential version of the recursive algorithm that computes the maximum amount of change using a given set of coins. It recursively considers the case where each position is included or not included in the final list.

**Task:** The goal is to parallelize the sequential algorithm provided. Note that you must respect the algorithm. Finding a better algorithm is out of the scope of this assignment.
