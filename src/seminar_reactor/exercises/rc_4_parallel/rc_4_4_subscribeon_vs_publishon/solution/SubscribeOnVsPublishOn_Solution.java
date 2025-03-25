package seminar_reactor.exercises.rc_4_parallel.rc_4_4_subscribeon_vs_publishon.solution;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/*
 * 
 * Der Stream in diesem Beispiel ist bereits parallelisiert, aber er ist dennoch langsam.
 * 
 * Finde eine Moeglichkeit, ihn auf andere Weise zu parallelisieren, so dass er schneller wird. 
 * 
 * Ziel: die Laufzeit soll um 50% sinken.
 * 
 * Hinweis: subscribeOn()
 * 
 * Frage: Warum ist Deine Parallelisierung schneller als die urspruengliche?
 * 
 */
public class SubscribeOnVsPublishOn_Solution {

	public static void main(String[] args) {
		
		SubscribeOnVsPublishOn_Solution instance = new SubscribeOnVsPublishOn_Solution();
		
		instance.parallelStream();
		
		sleep(3000);
		
	}
	
	
	void parallelStream(){
	
		long startTime = System.currentTimeMillis();
		
		
		Flux<Integer> parallelSource = Flux.<Integer>create( emitter -> {
			for(int i = 1; i <= 10; ++i) {
				System.out.println("Source producing " + i + " in " +Thread.currentThread());				
				emitter.next(i);
				// Langsame Source
				sleep(200);
			}
		});
		
			
		parallelSource = parallelSource
			// subscribeOn() entkoppelt mehrere Clients voneinander, sie laufen parallel von der Source an
			.subscribeOn(Schedulers.parallel());
			// publishOn() ist hier nicht effektiv. Problem:
			// Die subscribe() Calls laufen im Main-Thread, dadurch laeuft auch die Source 
			// im Main-Thread und produziert zuerst alle Items fuer Subscriber 1 und erst danach
			// die Items fuer Subscriber 2.
			//.publishOn(Schedulers.parallel());	

			
			
		// Subscriber 1:
		parallelSource
			.subscribe(i -> {
				System.out.println("Subscriber 1 gets: " + i + " in " + Thread.currentThread());
				
				if(i == 10) {
					System.out.println("\n Client 1 finished after " + (System.currentTimeMillis() - startTime)/1000 + " Seconds \n");
				}
			});
		
		System.out.println("Subscriber 1 subscribe() returned");
		
		// Subscriber 2:
		parallelSource
			.subscribe(i -> {
				System.out.println("Subscriber 2: gets: " + i + " in " + Thread.currentThread());
				
				// Zeitmessung
				if(i == 10) {
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
