package seminar_reactor.exercises.rc_4_parallel.rc_4_2_b_subscribeon_speedup.solution;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/*
Palllelität Performance
- Baue einen Stream mit zwei langsamen Subscribern
- Entkopple die Subscriber durch subscribeOn()
- Zeige, dass der Stream nun schneller läuft

 */
public class SubscribeOnSpeedup_Solution {
	
	static final int NR_OF_ITEMS = 100;

	public static void main(String[] args) {
		
		SubscribeOnSpeedup_Solution instance = new SubscribeOnSpeedup_Solution();
		
		instance.parallelStream();
		
		// Haelt das Programm lebendig
		sleep(30000);
		
	}
	
	
	void parallelStream(){
	
		long startTime = System.currentTimeMillis();
		
		
		Flux<Integer> parallelSource = Flux.range(1, NR_OF_ITEMS);
		
		// TODO: Parallelisierung mit subscribeOn()	
		parallelSource = parallelSource
			// subscribeOn() entkoppelt mehrere Clients voneinander,
			//	sie laufen parallel von der Source an
			.subscribeOn(Schedulers.parallel());
			
			
		// Subscriber 1:
		parallelSource
			.subscribe(i -> {
				System.out.println("Subscriber 1 gets: " + i + " in " + Thread.currentThread());
				
				// langsamer Subscriber
				sleep(100);
				if(i == NR_OF_ITEMS) {
					System.out.println("\n Client 1 finished after " + (System.currentTimeMillis() - startTime)/1000 + " Seconds \n");
				}
			});
		
		System.out.println("Subscriber 1 subscribe() returned");
		
		// Subscriber 2:
		parallelSource
			.subscribe(i -> {
				System.out.println("Subscriber 2: gets: " + i + " in " + Thread.currentThread());

				// langsamer Subscriber
				sleep(100);
				
				// Zeitmessung: 
				if(i == NR_OF_ITEMS) {
					System.out.println("\n Client 2 finished after " + (System.currentTimeMillis() - startTime) /1000 + " Seconds \n");
				}
			});
		
		System.out.println("Subscriber 2 subscribe() returned");

	}
	
////////////////////////////HELPER /////////////////////////////////////

	static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
