package org.quadrifolium.server;

public class Timer implements Runnable
{
	/** Rate at which timer is checked */
	protected int m_rate = 100;
	
	/** Length of timeout */
	private int m_length;

	/** Time elapsed */
	private int m_elapsed;
	
	private Thread _blinker = null ;

	/**
	  * Creates a timer of a specified length
	  * @param	length	Length of time before timeout occurs
	  */
	public Timer ( int length )
	{
		// Assign to member variable
		m_length = length;

		// Set time elapsed
		m_elapsed = 0;
	}

	public void start()
	{
    _blinker = new Thread(this) ;
    _blinker.start() ;
	}

	/** Resets the timer back to zero */
	public synchronized void reset()
	{
		m_elapsed = 0 ;
	}
	
	public void stop() 
	{
    _blinker = null ;
	}


	/** Performs timer specific code */
	@SuppressWarnings("static-access")
  public void run()
	{
		Thread thisThread = Thread.currentThread() ;
		
		// Keep looping
		while (_blinker == thisThread)
		{
			// Put the timer to sleep
			try
			{ 
				thisThread.sleep(m_rate);
			}
			catch (InterruptedException ioe) 
			{
				continue;
			}

			// Use 'synchronized' to prevent conflicts
			synchronized ( this )
			{
				// Increment time remaining
				m_elapsed += m_rate;

				// Check to see if the time has been exceeded
				if (m_elapsed > m_length)
				{
					// Trigger a timeout
					timeout();
				}
			}

		}
	}

	// Override this to provide custom functionality
	public void timeout()
	{
		System.err.println ("Network timeout occurred.... terminating");
		System.exit(1);
	}
}
