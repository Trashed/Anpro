package fi.tamk.anpro;

/**
 * Toteutus pelialueella vaeltelevalle teko‰lylle, joka pelaajan havaittuaan
 * vaihtaa teko‰ly‰‰n satunnaiseksi.
 * 
 * K‰ytet‰‰n ainoastaan vihollisille.
 */
public class SeekAndDestroyAi extends AbstractAi
{
	// Luodaan muuttujat
	private WeaponManager weaponManager;
	
	private long lastPositionUpdate = 0;
	private int  newAi 				= 0;
	private boolean aiIsChecked		= false;
	
	/**
	 * Alustaa luokan muuttujat.
	 * 
	 * @param _parentObject
	 * @param _userType
	 * @param _weaponManager
	 */
	public SeekAndDestroyAi(AiObject _parentObject, int _userType, WeaponManager _weaponManager)
	{
		super(_parentObject, _userType);
		
		/* Tallennetaan muuttujat */
		weaponManager = _weaponManager;
		newAi = Utility.getRandom(10, 40);
		newAi /= 10;
		
		
	}

	/* =======================================================
     * Perityt funktiot
     * ======================================================= */
	 /**
     * K‰sittelee teko‰lyn.
     */
	@Override
    public final void handleAi()
    {
        // Lasketaan pomovihollisen ja pelaajan v‰linen et‰isyys.
		double distance = Utility.getDistance(parentObject.x, parentObject.y, wrapper.player.x, wrapper.player.y);
		
		
		/* P‰‰tell‰‰n pomoaluksen ja pelaajan v‰lisest‰ et‰isyydest‰, 
		 * harhaillaanko pelialueella vai otetaanko satunnainen teko‰ly
		 * k‰yttˆˆn hyˆkk‰yst‰ varten. */
		if (distance > 50) {
			parentObject.movementSpeed = 2;
			
			if (lastPositionUpdate == 0) {
				lastPositionUpdate = android.os.SystemClock.uptimeMillis();
			}
			else {
				long currentTime = android.os.SystemClock.uptimeMillis();
				
				
				
				// Tarkistetaan, onko edellisest‰ tarkastuksesta kulunut yli asetettu aika.
				if (currentTime - lastPositionUpdate >= 2000) {
					
				}
				
				lastPositionUpdate = currentTime;
			}
			
			// M‰‰ritet‰‰n vihollisen ja pelaajan v‰linen kulma
	        double angle = Utility.getAngle((int) parentObject.x, (int) parentObject.y,(int) wrapper.player.x + 100,(int) wrapper.player.y + 100);
			
	        // M‰‰ritet‰‰n k‰‰ntymissuunta.
			parentObject.turningDirection = Utility.getTurningDirection(parentObject.direction, (int)angle);
		}
		else {
			parentObject.movementSpeed = 4;
			if (aiIsChecked != true) {
				if (newAi == AbstractAi.LINEAR_ENEMY_AI) {
					parentObject.ai = new LinearAi(parentObject, Wrapper.CLASS_TYPE_ENEMY);
				}
				else if (newAi == AbstractAi.ROTARY_ENEMY_AI) {
					parentObject.ai = new RotaryAi(parentObject, Wrapper.CLASS_TYPE_ENEMY, weaponManager);
				}
				else if (newAi == AbstractAi.SQUIGGLY_ENEMY_AI) {
					parentObject.ai = new SquigglyAi(parentObject, Wrapper.CLASS_TYPE_ENEMY, weaponManager);
				}
				else if (newAi == AbstractAi.APPROACHANDSTOP_ENEMY_AI) {
					parentObject.ai = new ApproachAndStopAi(parentObject, Wrapper.CLASS_TYPE_ENEMY, weaponManager);
				}
				
				aiIsChecked = true;
			}
		}
		
		
        
		/* Tarkistetaan tˆrm‰ykset pelaajan kanssa. */
        checkCollisionWithPlayer();
    }
}
