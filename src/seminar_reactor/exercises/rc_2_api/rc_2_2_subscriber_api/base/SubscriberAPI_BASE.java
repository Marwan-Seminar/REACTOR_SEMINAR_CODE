package seminar_reactor.exercises.rc_2_api.rc_2_2_subscriber_api.base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.reactivestreams.Subscription;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

/*


a) 	Registriere einen Subscriber mittels eines Lambdas. 
	Dieses schreibt das Item sowie den Thread in dem es ausgeführt wird auf die Shell.

b ) Programmiere einen einfachen Subscriber mithilfe der Klasse BaseSubscriber.
 	Überschreibe die hookOnNext(), und hookOnSubscribe() Methode.
 	Mache es Dir leicht, indem Du in jedem onNext() Call ein neues Element anforderst 
 	(Stop-And-Wait Protokoll)
 	
c) Ein Subscriber der mit Batches arbeitet (Advanced)

 */
public class SubscriberAPI_BASE {
	
	public static void main(String[] args) {
		SubscriberAPI_BASE instance = new SubscriberAPI_BASE();
		
		instance.a_lambdaSubscriber();
		
		instance.b_simpleBaseSubscriber();

	}

	/*
	 * a) 	Registriere einen Subscriber mittels eines Lambdas. 
	 * 		Dieses schreibt das Item sowie den Thread in dem es ausgeführt wird auf die Shell.
	 */
	private void a_lambdaSubscriber() {
		
		System.out.println("a) Lambda Subscriber ");
		
		Flux<Integer> source = Flux.range(0, 10);
		 
		// TODO: das Lambda bekommt als Argument einen Consumer: i-> {// do something with i}
		source.subscribe((Consumer<? super Integer>)null);
	}
	
	private void b_simpleBaseSubscriber() {
		
		System.out.println("b) Simple Base Subscriber ");
		
		Flux<Integer> source = Flux.range(0, 10);
		
		source.subscribe( new SimpleSubscriber());
		
	}

	
}

/*
 * Zur Loesung von b) ein einfacher Subscriber auf Basis von BaseSubscriber
 * 
 * Dieser folgt einem "naiven" Stop-And-Wait Protokoll, da er in jedem onNext()
 * ein neues Element anfordert, was funktioniert, aber nicht unbedingt effizient ist.
 */
class SimpleSubscriber extends BaseSubscriber<Integer>{
	
	Subscription subscription;
	
	/*
	 * hookOnSubscribe() 
	 * Merkt sich die Subscription 
	 * Fordert das erste Element an
	 */
	@Override
	protected void hookOnSubscribe(Subscription subscription) {
		System.out.println("SimpleSubscriber.hookOnSubscribe() " +Thread.currentThread() );
		
		// TODO: Subscription merken: this.subscription = subscription;
		
		// TODO  Backpressure in Handarbeit: Ein Element anfordern: subscription.request(1);
	}
	
	/*
	 * hookOnNext() 
	 * Verarbeitet ein Element, das von Upstream geschickt wurde
	 * Fordert ein neues Element an
	 */
	@Override
	protected void hookOnNext(Integer value) {
		
		// Element value verarbeiten
		System.out.println("SimpleSubscriber.hookOnNext() " + value + " " + Thread.currentThread()) ;
		
		//TODO: Neues Element anfordern subscription.request(1);
			
	}
	
}

/*
 * Zur Loesung von c) ein fortegschrittenen Subscriber auf Basis von BaseSubscriber
 * 
 * Dieser fordert immer 32 Items auf einmal an
 * führt diese in einem Hintergrund Thread aus
 * */
class AdvancedSubscriber extends BaseSubscriber<Integer>{
	
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
		System.out.println("AdvancedeSubscriber.hookOnSubscribe() " +Thread.currentThread() );
		
		// muss VOR request stehen!! Sonst lauft onNext() schon los.
		remainingCount.set(BATCH_SIZE);
		System.out.println("AdvancedeSubscriber.hookOnSubscribe() remaining set to " + remainingCount.get() + " " + Thread.currentThread()) ;

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
			System.out.println("AdvancedeSubscriber.hookOnNext() " + value + " " + Thread.currentThread()) ;
					
			int currentRemaining = remainingCount.decrementAndGet();
			System.out.println("remaining " +  currentRemaining) ;
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

