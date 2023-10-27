import java.util.concurrent.RecursiveTask;

public class CoinDepth extends RecursiveTask<Integer> implements Runnable{

	private int[] coins;
	private int index;
	private int accumulator;
	private int depth;
	
	public static final int LIMIT = 999;

	public CoinDepth(int[] coins, int index, int accumulator, int depth) {

		this.coins = coins;
		this.index = index;
		this.accumulator = accumulator;
		this.depth = depth;
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
	
	private static int par(int[] coins, int index, int accumulator) {
		CoinQueuedTaskCount f2 = new CoinQueuedTaskCount(coins, index, accumulator);
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

		if (depth >= 20) return seq(coins, index, accumulator);

		CoinDepth f1 = new CoinDepth(coins, index+1, accumulator, depth+1);
		f1.fork();
		CoinDepth f2 = new CoinDepth(coins, index+1, accumulator + coins[index], depth+1);
		f2.fork();

		int a = f1.join();
		int b = f2.join();
		return Math.max(a,  b);
	}

	@Override
	public void run() {
		int nCores = Runtime.getRuntime().availableProcessors();

		int[] coins = createRandomCoinSet(30);

		int repeats = 2;
		for (int i=0; i<repeats; i++) {
			long seqInitialTime = System.nanoTime();
			int rs = seq(coins, 0, 0);
			long seqEndTime = System.nanoTime() - seqInitialTime;
			System.out.println(nCores + ";Sequential;" + seqEndTime);
			
			long parInitialTime = System.nanoTime();
			int rp = par(coins, 0, 0);
			long parEndTime = System.nanoTime() - parInitialTime;
			System.out.println(nCores + ";Parallel;" + parEndTime);
			if (rp != rs) {
				System.out.println("Wrong Result!");
				System.exit(-1);
			}
		}
	}
}
