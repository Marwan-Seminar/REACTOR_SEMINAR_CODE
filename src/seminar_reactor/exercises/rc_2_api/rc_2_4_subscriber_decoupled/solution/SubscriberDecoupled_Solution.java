package seminar_reactor.exercises.rc_2_api.rc_2_4_subscriber_decoupled.solution;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.reactivestreams.Subscription;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/*

Konsturiere einen Stream, bei dem die Aufgaben, die der Subscriber in seiner  onNext() erledigt, 
in einem anderen Thread ausgefuehrt werden als die Stream-Source. Dabei soll folgendes Verhalten
realisiert werden:

- Subscriber laeuft in einem anderen Thread als die Source
- Items werden in Batches bei der Source bestellt

a) 	Realisere dies, ohne dass Du einen eigenen Subscriber implementierst. 
	Hinweise:
	- publishOn() entkoppelt Publisher und Subscriber
	- Zeige das Verhalten Deines Streams mit den Log-Methoden doOnNext() und doOnRequest()

b) 	Baue einen Subscriber mithilfe der Klasse Base-Subscriber der folgendes leistet: 
	Wenn ein Item in onNext(T item) eintrifft, lagere die Verarbeitung dieses Items 
	in einen anderen Thread aus.
	Verwende dafuer z.B. einen Thread Pool (ExecutorService).

	Fordere neue Daten in einem Batch an, das heisst, rufe Subscribe.requst(BATCH_SIZE) 
	mit einer BATCH_SIZE z.B. von 32 auf.
	Führe Buch, wann diese 32 Items verarbeitet sind, und fordere erst dann neue Items an.	

 */
public class SubscriberDecoupled_Solution {
	
	public static void main(String[] args) {
	

		System.out.println("SubscriberDecoupled_Solution() ");	
		
		SubscriberDecoupled_Solution instance = new SubscriberDecoupled_Solution();
		
		instance.a_decoupledBatchSubscriberAutomatic();
		
		instance.b_decoupledBatchSubscriberHandmade();
		
		
	}

	
	
	private void a_decoupledBatchSubscriberAutomatic() {
		
		System.out.println("b_decoupledBatchSubscriberAutomatic() ");	

		Flux<Integer> source = Flux.range(0, 1000);
		
		source
			.doOnRequest((request) ->{
				System.out.println("\nAutomaticSubscriber: REQUEST() " + request + " " + Thread.currentThread() + "\n");

			})
			.doOnNext((nextItem) ->{
				if(nextItem % 100 == 0) {
				 System.out.println("AutomaticSubscriber: NEXT ITEM FROM SOURCE() " + nextItem + " " + Thread.currentThread()) ;
				}
			})
			// Dies ist die entscheidende Stelle
			// Entkoppelt Subscriber in einen anderne Thread
			// und bestellt Items in Batches bei der Source
			.publishOn(Schedulers.parallel())
			
			.subscribe(item -> {
				if(item % 100 == 0) {
					System.out.println("AutomaticSubscriber subscribe() " + item + " " + Thread.currentThread()) ;
				}
				
			});
		
		// keep program alive
		sleep(2000);
	}
	
	private void b_decoupledBatchSubscriberHandmade() {
		System.out.println("a_decoupledBatchSubscriber() ");	
		
		Flux<Integer> source = Flux.range(0, 1000);
		
		source.subscribe( new DecoupledBatchSubscriber());
		
	}
	
	////////////////////////HELPER ////////////////////

	static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

/*
/*
 * Zur Loesung von c) ein fortegschrittenen Subscriber auf Basis von BaseSubscriber
 * 
 * Dieser fordert immer 32 Items auf einmal an
 * führt diese in einem Hintergrund Thread aus
 * */
class DecoupledBatchSubscriber extends BaseSubscriber<Integer>{
	
	// Subscription
	Subscription subscription;
	
	// Batch Management
	static final int BATCH_SIZE = 32;
	final AtomicInteger remainingCount = new AtomicInteger();
	 
	// Thread Management
	ExecutorService threadPool = Executors.newFixedThreadPool(8);
	
	
	/*
	 * hookOnSubscribe() 
	 * Fordert den ersten Batch an
	 */
	@Override
	protected void hookOnSubscribe(Subscription subscription) {
		System.out.println("DecoupledBatchSubscriber.hookOnSubscribe() " +Thread.currentThread() );
		
		// muss VOR request stehen!! Sonst lauft onNext() schon los.
		remainingCount.set(BATCH_SIZE);
		//System.out.println("DecoupledBatchSubscriber.hookOnSubscribe() remaining set to " + remainingCount.get() + " " + Thread.currentThread()) ;

		// Subscription merken
		this.subscription = subscription;
		// Backpressure in Handarbeit: Ein Element anfordern
		subscription.request(BATCH_SIZE);
		
	}
	
	/*
	 * hookOnNext() 
	 * Verarbeitet Elemente in einem Thread-Pool
	 * Wenn der Batch abgearbeitet ist, wird ein neuer Batch angefordert
	 */
	@Override
	protected void hookOnNext(Integer value) {
		
		// Element value in Pool-Thread verarbeiten
		// und dabei das Batch-Managment realisiren
		threadPool.execute(()-> {
			
			if(value % 10 == 0) {
				System.out.println("DecoupledBatchSubscriber.hookOnNext() " + value + " " + Thread.currentThread()) ;
			}
			
			int currentRemaining = remainingCount.decrementAndGet();
			//System.out.println("remaining " +  currentRemaining) ;
			if( currentRemaining == 0) {
				System.out.println("\nNEW BATCH\n") ;
				
				// Buchfuehung fuer Batch aktualisieren
				// muss VOR request stehen!! Sonst lauft onNext() schon los.
				remainingCount.set(BATCH_SIZE);
				
				//Neuen Batch anfordern
				subscription.request(BATCH_SIZE);
				
			}
		});
			
	}
	
	@Override
	protected void hookOnComplete() {
		System.out.println("\nStream completed: Shutting down the Thread-Pool\n");
		this.threadPool.shutdown();
	}
	
}

