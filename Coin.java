import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class Coin extends RecursiveTask<Integer> implements Runnable {

	private int[] coins;
	private int index;
	private int accumulator;
	private int depth;
	private int strategy;	
	public static final int LIMIT = 999;

	public Coin(int[] coins, int index, int accumulator,  int depth, int strategy) {

		this.coins = coins;
		this.index = index;
		this.accumulator = accumulator;
		this.depth = depth;
		this.strategy = strategy;
	}
	
	public static int[] createRandomCoinSet(int N) {
		int[] r = new int[N];
		for (int i = 0; i < N ; i++) {
			if (i % 10 == 0) {
				r[i] = 400;
			} else {
				r[i] = 4;
			}
		}
		return r;
	}

	private static int seq(int[] coins, int index, int accumulator) {
		
		if (index >= coins.length) {
			if (accumulator < LIMIT) {
				return accumulator;
			}
			return -1;
		}
		if (accumulator + coins[index] > LIMIT) {
			return -1;
		}
		int a = seq(coins, index+1, accumulator);
		int b = seq(coins, index+1, accumulator + coins[index]);
		return Math.max(a,  b);
	}
	
	private static int par(int[] coins, int index, int accumulator, int depth, int strategy) {
		Coin f2 = new Coin(coins, index, accumulator, depth, strategy);
		return f2.compute();
	}

	@Override
	protected Integer compute() {
		//stop condition
		if (index >= coins.length) {
			if (accumulator < LIMIT) {
				return accumulator;
			}
			return -1;
		}

		if (accumulator + coins[index] > LIMIT) {
			return -1;
		}
		
		// CoinQueuedTaskCount f1;
		// CoinQueuedTaskCount f2;
		switch (strategy) {
			case 0:
				// Surplus: if the current queue has more than 2 tasks than the average
				//System.out.println(RecursiveTask.getSurplusQueuedTaskCount());
				if (RecursiveTask.getSurplusQueuedTaskCount() > 2 ) return seq(coins, index, accumulator);
				break;

			case 1:
				if (depth >= 20) return seq(coins, index, accumulator);
				break;

			case 2:
				// Max tasks: if the total number of tasks >= T * #cores.
				// System.out.println("\n task count = " + Coin.getQueuedTaskCount());
				// System.out.println("\n parallelism: " + Coin.getPool().getParallelism());
				if (Coin.getQueuedTaskCount() > 3 * Coin.getPool().getParallelism() ) return seq(coins, index, accumulator);
				break;
			default:
				return seq(coins, index, accumulator);
		}

		Coin f1 = new Coin(coins, index+1, accumulator, depth+1, strategy);
		f1.fork();
		Coin f2 = new Coin(coins, index+1, accumulator + coins[index], depth+1, strategy);
		f2.fork();

		int a = f1.join();
		int b = f2.join();
		return Math.max(a,  b);
	}

	@Override
	public void run() {
		int nCores = Runtime.getRuntime().availableProcessors();

		int[] coins = createRandomCoinSet(30);

		try {
			FileWriter csvWriter = new FileWriter("results.csv");

			int repeats = 1;
			
			List<Integer> threads = new ArrayList<>();
			int j = 2;
			while (j <= nCores) {
				threads.add(j);
				j *= 2;
			}
			
			StringBuilder header = new StringBuilder("Sequential");
			for (int t : threads) {
				header.append(", Surplus:Thread " + t);
				header.append(", Depth:Thread " + t);
				header.append(", MaxTasks:Thread " + t);
			}
			csvWriter.write(header.toString() + "\n");

			HashMap<Integer, String> strategies = new HashMap<Integer, String>();
			strategies.put(0, "Surplus");
			strategies.put(1, "Depth");
			strategies.put(2, "MaxTasks");

			for (int i=0; i<repeats; i++) {

				StringBuilder line = new StringBuilder();
				System.out.println("\nIteration: " + i);
				long seqInitialTime = System.nanoTime();
				int rs = seq(coins, 0, 0);
				long seqEndTime = System.nanoTime() - seqInitialTime;
				System.out.println("Sequential;" + seqEndTime);
				line.append(seqEndTime);

				for (int currentThread = 2; currentThread <= nCores; currentThread *= 2) {
            		ForkJoinPool pool = new ForkJoinPool(currentThread);
					for (int s : strategies.keySet()) {				
						long parInitialTime = System.nanoTime();
						Future<Integer> future = pool.submit(() -> par(coins, 0, 0, 0, s));
						int rp = future.get();
						long parEndTime = System.nanoTime() - parInitialTime;
						System.out.println(currentThread + ";Strategy:" + strategies.get(s) + ";" + parEndTime);
						line.append("," + parEndTime);
						if (rp != rs) {
							System.out.println("Wrong Result!");
							System.exit(-1);
						}
					}
				}
				csvWriter.write(line.toString() + "\n");
			}
			csvWriter.close();
			System.out.println("Results saved to results.csv");
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
