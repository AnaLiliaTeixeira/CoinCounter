import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class Main {
    
	public static void main(String[] args) {

        List<Runnable> tasks = new ArrayList<>();
        tasks.add(new CoinQueuedTaskCount(null, 0, 0, 0, 0));
        //tasks.add(new CoinDepth(null, 0, 0, 0));

        for (Runnable task : tasks) {     
            System.out.println("\n" + task.getClass().getName() + "\n");
            task.run();
        }

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

}
