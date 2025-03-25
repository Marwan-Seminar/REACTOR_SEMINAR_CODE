package seminar_reactor.exercises.rc_4_parallel.rc_4_1_b_subscribeon_behavior.solution;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class SubscribeOnBehavior_SOLUTION {

	
	static final int NR_OF_ITEMS = 100;

	public static void main(String[] args) {
		
		SubscribeOnBehavior_SOLUTION instance = new SubscribeOnBehavior_SOLUTION();
		
		instance.parallelStream();
		
		// Haelt das Programm lebendig
		sleep(5000);
		
	}
	
	
	void parallelStream(){
	
		
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
				
			});
		
		System.out.println("\n Subscriber 1 subscribe() returned \n");
		
		// Subscriber 2:
		parallelSource
			.subscribe(i -> {
				System.out.println("Subscriber 2: gets: " + i + " in " + Thread.currentThread());

			
			});
		
		System.out.println("\n Subscriber 2 subscribe() returned \n");

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
