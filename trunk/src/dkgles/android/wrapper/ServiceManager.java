package dkgles.android.wrapper;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.os.Vibrator;

/**
 * 
 * @author doki
 * Remenber to turn on the user.permission
 */
public class ServiceManager
{
	public static ServiceManager instance()
	{
		if (_instance==null)
		{
			_instance = new ServiceManager();
		}
		
		return _instance;
	}
	
	/**
	 * 
	 * @param context
	 */
	public void initialize(Context context)
	{
		_vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		_sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		_powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		_initialized = true;
	}
	
	public Vibrator vibrator()
	{
		return _vibrator;
	}
	
	
	public PowerManager powerManager()
	{
		return _powerManager;
	}
	
	public SensorManager sensorManager()
	{
		return _sensorManager;
	}
	
	
	
	private ServiceManager()
	{
		_initialized = false;
	}
	
	private PowerManager	_powerManager;
	private SensorManager 	_sensorManager;
	private Vibrator 		_vibrator;
	
	private boolean _initialized;
	
	private static ServiceManager _instance;
}