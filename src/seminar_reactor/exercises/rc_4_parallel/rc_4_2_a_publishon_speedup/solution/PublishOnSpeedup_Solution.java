package seminar_reactor.exercises.rc_4_parallel.rc_4_2_a_publishon_speedup.solution;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/*
 * Zeige, dass sich ein Stream durch den Einsatz unterschiedlicher 
 * Threads für die verschiednen Stream-Stufen beschleunigen laesst. 
 * 
 * Hinweis: publishOn() an geeigneter Stelle in den vorhanden Stream einbringen
 */
public class PublishOnSpeedup_Solution {

	public static void main(String[] args) {
		
		PublishOnSpeedup_Solution instance = new PublishOnSpeedup_Solution();
		
		instance.speedupStream();
		
		// Haelt das Programm lebendig
		sleep(15000);
				
			
	}

	private void speedupStream() {

		long start = System.currentTimeMillis();

		Flux<Integer> source = Flux.range(1, 50);

		source
				// Laeuft im Main-Thread
				.map(i -> {
					System.out.println("map: " + i + " " + Thread.currentThread().getName());
					// Dieser Aufruf macht die Map-Stufe langsam
					sleep(100);
					return i;
				})
				
				// Entkoppelt downstream Stufen in eigenen Thread 
				// und buffert Elemente zwischen den Stream-Stufen
				.publishOn(Schedulers.parallel())

				// Laeuft im Pool-Thread
				.subscribe(i -> {
					System.out.println("Subscriber: " + i + " " + Thread.currentThread().getName());
					// Dieser Aufruf macht den Subscriber langsam
					sleep(100);

					// Zeitmessung
					if (i == 50) {
						System.out.println("\n Stream Completed: Runtime: "
								+ (System.currentTimeMillis() - start) / 1000 + " Seconds");
					}
				});
			
		
	}
	
	
	//////////////////////// Helper ///////////////////////////////////
	
	static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
