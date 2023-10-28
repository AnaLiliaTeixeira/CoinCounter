import java.util.concurrent.RecursiveTask;

public class CoinQueuedTaskCount extends RecursiveTask<Integer> implements Runnable{

	private int[] coins;
	private int index;
	private int accumulator;
	private int strategy;
	private int depth;
	
	public static final int LIMIT = 999;

	public CoinQueuedTaskCount(int[] coins, int index, int accumulator,  int depth, int strategy) {

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
		CoinQueuedTaskCount f2 = new CoinQueuedTaskCount(coins, index, accumulator, depth, strategy);
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
		
		CoinQueuedTaskCount f1 = null;
		CoinQueuedTaskCount f2 = null;
		switch (strategy) {
			case 0:
				// Surplus: if the current queue has more than 2 tasks than the average
				//System.out.println(RecursiveTask.getSurplusQueuedTaskCount());
				if (RecursiveTask.getSurplusQueuedTaskCount() > 2 ) return seq(coins, index, accumulator);
		
				f1 = new CoinQueuedTaskCount(coins, index+1, accumulator, depth, strategy);
				f1.fork();
				f2 = new CoinQueuedTaskCount(coins, index+1, accumulator + coins[index], depth, strategy);
				f2.fork();
				break;
			case 1:

				if (depth >= 20) return seq(coins, index, accumulator);

				f1 = new CoinQueuedTaskCount(coins, index+1, accumulator, depth+1, strategy);
				f1.fork();
				f2 = new CoinQueuedTaskCount(coins, index+1, accumulator + coins[index], depth+1, strategy);
				f2.fork();
				break;
		}

		int a = f1.join();
		int b = f2.join();
		return Math.max(a,  b);
	}

	@Override
	public void run() {
		int nCores = Runtime.getRuntime().availableProcessors();

		int[] coins = createRandomCoinSet(30);

		int repeats = 2;
		int[] strategies = {0, 1};
		for (int i=0; i<repeats; i++) {
			for (int s : strategies) {
				System.out.println("Strategy: " + s);


				long seqInitialTime = System.nanoTime();
				int rs = seq(coins, 0, 0);
				long seqEndTime = System.nanoTime() - seqInitialTime;
				System.out.println(nCores + ";Sequential;" + seqEndTime);
				
				long parInitialTime = System.nanoTime();
				//Strategies:
				// 0 -> surplus
				// 1 -> depth
				int rp = par(coins, 0, 0, 0, strategy);
				long parEndTime = System.nanoTime() - parInitialTime;
				System.out.println(nCores + ";Parallel;" + parEndTime);
				if (rp != rs) {
					System.out.println("Wrong Result!");
					System.exit(-1);
				}
			}
		}
	}
}
