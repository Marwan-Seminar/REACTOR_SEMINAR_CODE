package seminar_reactor.exercises.rc_4_parallel.rc_4_1_b_subscribeon_behavior.base;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/*

Parellelitaet mit subscribeOn(): Funktionsweise

- 	Baue einen Stream mit zwei Subscribern
- 	Erkenne, dass erst der eine Subscriber bis zu Ende läuft und danach der andere
- 	Entkopple die Subscriber durch subscribeOn()
- 	Zeige durch Shell-Ausgaben, dass nun beide Subscriber gleichzeitig laufen, 
		in unterschiedlichen Threads
*/

public class SubscribeOnBehavior_BASE {

	
	static final int NR_OF_ITEMS = 100;

	public static void main(String[] args) {
		
		SubscribeOnBehavior_BASE instance = new SubscribeOnBehavior_BASE();
		
		instance.parallelStream();
		
		// Haelt das Programm lebendig
		sleep(5000);
		
	}
	
	
	void parallelStream(){
	
		
		Flux<Integer> parallelSource = Flux.range(1, NR_OF_ITEMS);
		
		// TODO: Parallelisierung mit subscribeOn()	
		
			
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
