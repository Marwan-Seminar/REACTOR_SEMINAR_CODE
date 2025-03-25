package seminar_reactor.exercises.rc_4_parallel.rc_4_1_a_publishon_behavior.base;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/*
 * Concurrency mit publishjOn(): Funktionsweise 
 * 
 * 	- 	Baue einen Stream mit zwei Stufen 
 * 	- 	Entkoppele diese Stufen durch publishOn()
 * 	- 	Zeige durch Shell-Ausgaben, dass die Stufen nun nebenlaufig in 
 * 	  	unterschiedlichen Threads laufen
*/
public class PublishonBehavior_Base {

	public static void main(String[] args) {
		
		PublishonBehavior_Base instance = new PublishonBehavior_Base();
		
		instance.speedupStream();
		
		// Haelt das Programm lebendig
		sleep(15000);
				
			
	}

	private void speedupStream() {

		long start = System.currentTimeMillis();

		Flux<Integer> source = Flux.range(1, 50);

		source
				// Map Stufe
				.map(i -> {
					System.out.println("map: " + i + " " + Thread.currentThread().getName());
					// Dieser Aufruf macht die Map-Stufe langsam
					sleep(10);
					return i;
				})
				
				// TODO Entkoppeln der Stufen durch publishOn()
				
				// Subscriber Stufe
				.subscribe(i -> {
					System.out.println("Subscriber: " + i + " " + Thread.currentThread().getName());
					// Dieser Aufruf macht den Subscriber langsam
					sleep(30);

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
