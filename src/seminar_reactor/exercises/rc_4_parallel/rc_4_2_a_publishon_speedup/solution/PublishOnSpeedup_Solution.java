package seminar_reactor.exercises.rc_4_parallel.rc_4_2_a_publishon_speedup.solution;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/*
 * Zeige, dass sich ein Stream durch den Einsatz unterschiedlicher 
 * Threads für die verschiedenen Stream-Stufen beschleunigen laesst. 
 * 
 * Hinweis: publishOn() an geeigneter Stelle in den vorhanden Stream einbringen
 * 
 * Bemerkung: Unter welchen Bedingungen laufen die beiden Stufen in verschiedenen Threads? 
 * Es haengt davon ab, ob die Stufen CPU bound oder IO bound sind. Versuche dies durch die 
 * Aufrufe sleep() oder cpuIntensiveCall() herzustellen.
 * 
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
					System.out.println("map: " + i + "  " + Thread.currentThread());
					// Dieser Aufruf macht die Map-Stufe langsam
					sleep(100);
					//cpuIntensiveCall();
					return i;
				})
				
				// Entkoppelt downstream Stufen in eigenen Thread 
				// und buffert Elemente zwischen den Stream-Stufen
				.publishOn(Schedulers.parallel())

				// Laeuft im Pool-Thread
				.subscribe(i -> {
					System.out.println("Subscriber: " + i + "  " + Thread.currentThread());
					// Dieser Aufruf macht den Subscriber langsam
					sleep(100);
					//cpuIntensiveCall();
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
	
	/*
	 * Dieser Aufruf laueft ca. 100 Millisekunden auf (meiner) CPU 
	 */
	static void cpuIntensiveCall() {
		long start = System.currentTimeMillis();
		
		long dummy = 2;
		for(long counter = 0; counter <= Integer.MAX_VALUE / 5; counter ++) {
				dummy = (dummy + dummy);
				if (dummy > Integer.MAX_VALUE) {
					dummy = dummy % Integer.MAX_VALUE;
					
				}
				
		}
		
		long runtime = System.currentTimeMillis() - start;
		
		//System.out.println("cpuIntensiveCall: runtime " + runtime + " Thread: " + Thread.currentThread());
	}
}
