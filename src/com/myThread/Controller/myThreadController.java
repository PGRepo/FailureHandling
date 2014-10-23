package com.myThread.Controller;
import java.util.List;

public class myThreadController implements Runnable{

	    private final List<Runnable> runnables;
	 
	    public myThreadController(List<Runnable> runnables) {
			// TODO Auto-generated constructor stub
		
	        this.runnables = runnables;
	    }
	 
	    @Override
	    public void run() {
	        for (Runnable runnable : runnables) {
	             new Thread(runnable).start();
	        }
	    }
	
	
	
}
