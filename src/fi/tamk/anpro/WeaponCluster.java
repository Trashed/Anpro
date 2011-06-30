package fi.tamk.anpro;

import java.util.ArrayList;

/**
 * Sis�lt�� #4 aseen, eli Cluster Weaponin toteutuksen.
 * 
 * @extends AbstractWeapon
 */
public class WeaponCluster extends AbstractWeapon
{
    /* Ammukset */
	private ArrayList<ProjectileBomb> projectiles;

	/**
     * Alustaa luokan muuttujat ja luo tarvittavan m��r�n ammuksia.
     */
    public WeaponCluster(Wrapper _wrapper, int _userType)
    {
        super(_wrapper, _userType);
        
        // Alustetaan ammukset
		projectiles = new ArrayList<ProjectileBomb>(16);
		
		// Luodaan tarvittava m��r� ammuksia
		for (int i = 15; i >= 0; --i) {
			projectiles.add(new ProjectileBomb(AbstractAi.LINEAR_PROJECTILE_AI, _userType));
		}

    }
    /**
     * Aktivoi ammukset. T�st� eteenp�in ammusten oma teko�ly hoitaa niiden
     * p�ivitt�misen.
     * 
     * @param int Kohteen X-koordinaatti
     * @param int Kohteen Y-koordinaatti
     */
    @Override
    public final void activate(int _targetX, int _targetY, float _startX, float _startY)
    {
        for (int i = 15; i >= 0; --i) {
			if (!projectiles.get(i).active) {
				
				// Aktivoidaan ammus ja asetetaan kohteen koordinaatit
				projectiles.get(i).activate( _targetX, _targetY, true, false, this, _startX, _startY);
				
				// Soitetaan ��ni
				SoundManager.playSound(3, 1);
				
				// Keskeytet��n silmukka
				break;
        	}
        }
    }

    /**
     * Aktivoi r�j�hdyksess� tarvittavat ammukset. 
     * 
     * @param int   Aktivoitavien ammusten m��r� 
     * @param float Aloituskohdan X-koordinaatti
     * @param float Aloituskohdan Y-koordinaatti
     */
    @Override
    public final void triggerCluster(int _amount, float _startX, float _startY)
    {
    	int loopAmount = 0;
    	
        for (int i = 15; i >= 0; --i) {
			if (!projectiles.get(i).active) {
				
				// Aktivoidaan ammus ja asetetaan ammuksen suunta
				projectiles.get(i).activate(loopAmount * 45, false, false, this, _startX, _startY);
				
    			// M��ritet��n silmukan kiertom��r� suurentamalla muuttujan arvoa
				++loopAmount;
				
				if (loopAmount == _amount) {
					break;
				}
				
			}
        }
    }
}