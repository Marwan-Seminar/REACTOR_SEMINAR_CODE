package seminar_reactor.exercises.rc_4_parallel.rc_4_5_para_seq_speedup.solution;

import java.util.concurrent.CountDownLatch;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;


/*
 * a) Beschleunige den Stream in dieser Aufgabe indem Du
 * ihn deklarativ parallelisierst, unter Verwendung der 
 * Operatoren parallel() sequential() und runOn().
 * 
 * b) Zeige durch Shell Ausgaben, dass die Map-Aufrufe parallel ablaufen
 */
public class ParallelSequentialSpeedup_SOLUTION {

	public static void main(String[] args) throws InterruptedException {
		ParallelSequentialSpeedup_SOLUTION instance = new ParallelSequentialSpeedup_SOLUTION();
		instance.parallelSequentialSpeedup();
	}
	

	private void parallelSequentialSpeedup() throws InterruptedException {
		
		System.out.println("ParallelSequentialSpeedup_SOLUTION");
		
		int NR_OF_ITEMS = 100;
		final CountDownLatch latch = new CountDownLatch(NR_OF_ITEMS);
		long startTime = System.currentTimeMillis();
		
		Flux.range(0, NR_OF_ITEMS)
			.parallel()
			.runOn(Schedulers.parallel())
			.map(i -> {
				System.out.println("map() entered " + i + " " + Thread.currentThread());
				// SLOW
				sleep(100);
				System.out.println("map() returning " + i + " " + Thread.currentThread());
				return i;
			})
			.sequential()
			.subscribe( i -> {
				System.out.println("Subscriber got " + i + " " +Thread.currentThread());
				latch.countDown();
			});
		
		latch.await();
		System.out.println("\n RUNTIME: parallel()-sequential() Stream : " + (System.currentTimeMillis() - startTime) );
		
	}
	
	//////////////////////////// HELPER /////////////////////////////////////
	
	static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
