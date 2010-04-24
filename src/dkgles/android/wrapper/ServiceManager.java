package dkgles.android.wrapper;

import java.util.List;

import lost.kapa.ContextHolder;
import lost.kapa.GameManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;

/**
 * 
 * @author doki
 * Remenber to turn on the user.permission
 */
public enum ServiceManager
{
	INSTANCE;
	
	ServiceManager()
	{
		_initialized = false;
	}
	
	public void release()
	{
		
	}
	
	/**
	 * 
	 * @param context
	 */
	public void initialize()
	{
		_vibrator 		= (Vibrator)ContextHolder.INSTANCE.get().getSystemService(Context.VIBRATOR_SERVICE);
		_sensorManager 	= (SensorManager)ContextHolder.INSTANCE.get().getSystemService(Context.SENSOR_SERVICE);
		_powerManager 	= (PowerManager)ContextHolder.INSTANCE.get().getSystemService(Context.POWER_SERVICE);
		_wakelock 		= _powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "FooTag");
		
		List<Sensor> sensors = _sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		
		for (Sensor s : sensors)
		{
			Log.d("SENSOR_INFO", s.getName());
		}
		
		if (sensors.size()>0)
		{
			_orientSensor = sensors.get(0);
		}
		
		_initialized 	= true;
	}
	
	public Vibrator vibrator()
	{
		if (_initialized)
		{
			return _vibrator;
		}
		else
		{
			return null;
		}
	}
	
	
	public PowerManager powerManager()
	{
		if (_initialized)
		{
			return _powerManager;
		}
		else
		{
			return null;
		}
	}
	
	public void pause()
	{
		_sensorManager.unregisterListener(
				GameManager.INSTANCE
				);
		
		releaseWakeLock();
	}
	
	public void resume()
	{
		_sensorManager.registerListener(
				GameManager.INSTANCE,
				_orientSensor,
				SensorManager.SENSOR_DELAY_FASTEST
		);
		
		acquireWakeLock();
		
	}
	
	public SensorManager sensorManager()
	{
		if (_initialized)
		{
			return _sensorManager;
		}
		else
		{
			return null;
		}
	}
	
	public void acquireWakeLock()
	{
		_wakelock.acquire();
	}
	
	public void releaseWakeLock()
	{
		_wakelock.release();
	}
	
	PowerManager	_powerManager;
	SensorManager 	_sensorManager;
	Vibrator 		_vibrator;
	Sensor					_orientSensor;
	PowerManager.WakeLock 	_wakelock;
	
	boolean _initialized;
	private static final String TAG = "ServiceManager";
}