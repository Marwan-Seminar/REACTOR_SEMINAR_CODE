package seminar_reactor.exercises.rc_3_data.rc_3_2_generator_api.solution;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import reactor.core.publisher.Flux;


/*
 * 
 * Diese Uebung demonstirert die Generator API von Reactor
 * 
 * a) Baue einen Genarator mit generate() der Zufallszahlen emittiert
 * 
 * b) Baue einen Generator mit generate() der ein State Objekt benutzt und die Folge 1..10 emittiert
 * 
 * c) Baue einen Generator mit create() der die Folge 1..10 emittiert
 * 
 * d) Baue einen Generator mit create() asynchone Events (Button-Clicks )in den Stream einspeist 
 * 
 */
public class GeneratorAPI_Solution {
	
	public static void main(String[] args) {
		
		GeneratorAPI_Solution instance = new GeneratorAPI_Solution();
		
		instance.a_generateStateless();
		
		instance.b_generateStateObject();
		
		instance.c_createSynchronous();
		
		instance.d_createAsynchronous();
	}

	private void a_generateStateless() {
		
		System.out.println("GeneratorAPI.a_genarateStateless()");
		
		/*
		 *  Hinweise:
		 *
		 *  Zufallszahl:  
		 *  	Random rand = new Random();
		 *  	rand.nextInt();
		 *  
		 *  Signatur Flux.generate():
		 *  
		 *  	generate(Consumer<SynchronousSink<T>> generator)
		 *  
		 *  	interface SynchronousSink{
		 *  		void next(T t);
		 *  		void complete();
		 * 		}
		 *  
		 */
		
		Random rand = new Random();
		
		// Abbruch-Bedingung 
		AtomicInteger exitCriteria = new AtomicInteger();
		
		Flux<Integer> source = Flux.generate( emitter -> {
			
			int randomNumber = rand.nextInt();
			emitter.next(randomNumber);
			
			// Beendet den Stream nach 10 Items
			if(exitCriteria.incrementAndGet() >= 10) {
				emitter.complete();
			}
		});
		
		source.subscribe(item -> {
			System.out.println("Subscriber received: " + item);
			
		});
		
		
		
	}

	/*
	 * Flux.generate() hat eine Variante mit State Objekt.
	 * Dieses wird von Aufruf zu Aufruf des Generator-Lambdas weitergegeben
	 * um einen Zustand zu modellieren.
	 * 
	 * Hinweis: generate Signatur mit State Objekt
	 * 
	 * generate(Callable<S> stateSupplier, BiFunction<S, SynchronousSink<T>, S> generator)
	 * 
	 * 
	 */
	private void b_generateStateObject() {
		

		System.out.println("GeneratorAPI.b_genarateStateObject()");
		
		Flux<Integer> source = Flux.generate(
				()-> new AtomicInteger(),
				(state, emitter) -> {

					int nextNumber = state.incrementAndGet();
					emitter.next(nextNumber);

					if(nextNumber == 10) {
						emitter.complete();
					}

					return state;
				});
		
		// Subscriber
		source.subscribe(item -> {
			System.out.println("Subscriber received: " + item);
			
		});
		
	}

	/*
	 * Flux.create() kann Daten synchron in den Stream emittieren,
	 * indem diese Daten innerhalb des Lambda-Aufrufes emittiert werden.
	 * 
	 * Hinweis:
	 * 	Flux.create() API
	 * 	
	 * 	create(Consumer<? super FluxSink<T>> emitter)
	 * 	
	 * 	interface FluxSink{
	 * 		next(T t);
	 * 		
	 * 		// ...
	 * 	}
	 * 
	 */
	private void c_createSynchronous() {
		
		System.out.println("GeneratorAPI.c_createSynchronous()");
		
		Flux<Integer> source = Flux.create( emitter -> {
			for(int counter = 1; counter <= 10; counter++)
			emitter.next(counter);
		});
		
		source.subscribe(item -> {
			System.out.println("Subscriber received: " + item);
			
		});
		
		
	}

	/*
	 * Flux.create() kann Daten asynchron in einen Stream senden,
	 * indem diese Daten ausserhalb des Lambda Aufrufes asynchron 
	 * erzeugt werden.
	 * 
	 * Innerhlab des Lamdas wird die Datenquelle an den Stream gekoppelt.
	 * Im hier vorliegenden Beispiel ist die Datenquelle ein JButton
	 * der ueber einen ActionListener mit dem Stream verbunden wird.
	 * 
	 */
	private void d_createAsynchronous() {
		
		
		System.out.println("GeneratorAPI.d_createAsynchronous()");
		
		JButton button = ButtonFrame.startFrame();
		
		
		Flux<String> source = Flux.create( emitter -> {
			
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
				
					System.out.println("Emitting Button Click Data " + Thread.currentThread());
					
					// Daten in den Stream senden
					emitter.next(e.getActionCommand());
		
					
				}
			});
		});

		
		source.subscribe(item -> {
			System.out.println("Subscriber received: " + item);
			
		});
		
		
	}

}

////////////////// HELPER CLASS FOR Swing BUTTON ///////////////////

class ButtonFrame
{
	static int state = 1;
	
	
    public static JButton startFrame()
    {
        JFrame frame = new JFrame();
        frame.setTitle("Button Stream Connection");
        JPanel panel = new JPanel();
 
        // JButton: An diesen wir ein Action-Handler angefuegt, der Daten in den Stream speist
        JButton button = new JButton("Send Data Into Stream");
 
        panel.add(button);        
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
       
        return button;
    }
}
