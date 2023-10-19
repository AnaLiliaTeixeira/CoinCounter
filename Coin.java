import java.util.concurrent.RecursiveTask;

public class Coin extends RecursiveTask<Integer> {

	private int[] coins;
	private int index;
	private int accumulator;
	
	public static final int LIMIT = 999;
	private int depth;

	public Coin(int[] coins, int index, int accumulator, int depth) {

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

	public static void main(String[] args) {
		int nCores = Runtime.getRuntime().availableProcessors();

		int[] coins = createRandomCoinSet(30);

		int repeats = 40;
		for (int i=0; i<repeats; i++) {
			long seqInitialTime = System.nanoTime();
			int rs = seq(coins, 0, 0);
			long seqEndTime = System.nanoTime() - seqInitialTime;
			System.out.println(nCores + ";Sequential;" + seqEndTime);
			
			long parInitialTime = System.nanoTime();
			int rp = par(coins, 0, 0, 0);
			long parEndTime = System.nanoTime() - parInitialTime;
			System.out.println(nCores + ";Parallel;" + parEndTime);
			if (rp != rs) {
				System.out.println("Wrong Result!");
				System.exit(-1);
			}
		}

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
	
	private static int par(int[] coins, int index, int accumulator, int depth) {
		Coin f2 = new Coin(coins, index, accumulator, depth);
		return f2.compute();
	}

	@Override
	protected Integer compute() {
		//condição de paragem
		if (index >= coins.length) {
			if (accumulator < LIMIT) {
				return accumulator;
			}
			return -1;
		}

		if (accumulator + coins[index] > LIMIT) {
			return -1;
		}

		// working 
		if ( depth >= 20 ) return seq(coins, index, accumulator);

		//not working
		// Max tasks: if the total number of tasks >= T * #cores.
		// if ( Coin.getQueuedTasckCount() > 4 * Runtime.getRuntime().availableProcessors() ) return seq(coins, index, accumulator);
		
		//working and the sysout is returning a lot of 0's. is it good? does it means that here are no tasks waiting on the queue? 

		// Surplus: if the current queue has more than 2 tasks than the average
		// System.out.println(RecursiveTask.getSurplusQueuedTaskCount());
		// if (RecursiveTask.getSurplusQueuedTaskCount() > 2 ) return seq(coins, index, accumulator);

		Coin f1 = new Coin(coins, index+1, accumulator, depth+1);
		f1.fork();
		Coin f2 = new Coin(coins, index+1, accumulator + coins[index], depth+1);
		f2.fork();

		int a = f1.join();
		int b = f2.join();
		return Math.max(a,  b);
	}
}
