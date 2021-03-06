package fi.tamk.anpro;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class Collectable extends GameObject
{
	// Collectablen tyypit
	public static final byte COLLECTABLE_TYPE_SCORE  = 0;
	public static final byte COLLECTABLE_TYPE_WEAPON = 1;
	
	public int collectableType;
	public int weaponType;
	
	// Collectablen "rank" (t�ll� m��ritett��n ker�yksest� ansaitut pisteet)
	protected int COLLECTABLE_RANK = 5;
	
	// Wrapper ja Hud
	private Wrapper wrapper;
	private Hud     hud;
	
	/**
	 * Alustaa luokan muuttujat.
	 * 
	 * @param _x         X-koordinaatti
	 * @param _y         Y-koordinaatti
	 */
	public Collectable(int _x, int _y, int _type, Hud _hud)
	{
		super(0);
		
		/* Tallennetaan muuttujat */
		x               = _x;
		y               = _y;
		collectableType = _type;
		hud             = _hud;
		
		/* Otetaan tarvittavat luokat k�ytt��n */
		wrapper = Wrapper.getInstance();
		
		/* Alustetaan muuttujat */
		z = 7;
		
		// M��ritet��n t�rm�ystunnistus
		// TODO: SCALING (Options.scale)
		collisionRadius = (int) (25 * Options.scale);
		
		// M��ritet��n k�ytett�v� tekstuuri
		usedTexture = collectableType;
    
        // Haetaan animaatioiden pituudet
        animationLength = new int[GLRenderer.AMOUNT_OF_COLLECTABLE_ANIMATIONS];
        
        for (int i = 0; i < GLRenderer.AMOUNT_OF_COLLECTABLE_ANIMATIONS; ++i) {
            if (GLRenderer.collectableAnimations[i] != null) {
                animationLength[i] = GLRenderer.collectableAnimations[i].length;
            }
        }

        /* M��ritet��n objektin tila (piirtolista) */
		wrapper.addToDrawables(this);
	}
    
    /* =======================================================
     * Perityt funktiot
     * ======================================================= */
    /**
     * Piirt�� objektin k�yt�ss� olevan tekstuurin tai animaation ruudulle.
     * 
     * @param _gl OpenGL-konteksti
     */
	@Override
	public void draw(GL10 _gl)
	{
        // Tarkistaa onko animaatio p��ll� ja kutsuu oikeaa animaatiota tai tekstuuria
        if (usedAnimation >= 0) {
            GLRenderer.collectableAnimations[collectableType].draw(_gl, x, y, 0, currentFrame);
        }
        else {
            GLRenderer.collectableTextures[collectableType].draw(_gl, x, y, direction, currentFrame);
        }
	}
    
    /**
     * M��ritt�� objektin aktiiviseksi.
     */
	@Override
	public void setActive()
	{
		state = Wrapper.FULL_ACTIVITY;
		
		boolean isPlaced = false;
		
		while (!isPlaced) {
			x = Utility.getRandom(-GameMode.mapWidth, GameMode.mapWidth);
	        y = Utility.getRandom(-GameMode.mapHeight, GameMode.mapHeight);

	        for (int i = wrapper.obstacles.size()-1; i >= 0; --i) {
		        if (Math.abs(x - wrapper.obstacles.get(i).x) > (Wrapper.gridSize + 300 * Options.scaleX) && Math.abs(x - wrapper.player.x) > 250 * Options.scaleX &&
		        	Math.abs(y - wrapper.obstacles.get(i).y) > (Wrapper.gridSize + 300 * Options.scaleY) && Math.abs(y - wrapper.player.y) > 500 * Options.scaleY) {

	        		isPlaced = true;
	        		break;
				}
	        }
        }

		if (collectableType == COLLECTABLE_TYPE_WEAPON) {
			int tempType = weaponType;
			while (weaponType == tempType) {
				weaponType = Utility.getRandom(1, 6);
			}
		}
	}

    /**
     * M��ritt�� objektin ep�aktiiviseksi. Sammuttaa my�s teko�lyn jos se on tarpeen.
     */
	@Override
	public void setUnactive()
	{
		state = Wrapper.INACTIVE;
	}

    /**
     * K�sittelee t�rm�ykset.
     * 
     * @param _damage Osuman aiheuttama vahinko
     * @param _armorPiercing Osuman kyky l�p�ist� suojat (k�ytet��n, kun t�rm�ttiin ammukseen)
     */
    @Override
    public final void triggerCollision(int _eventType, int _damage, int _armorPiercing)
    {
    	if (collectableType == COLLECTABLE_TYPE_SCORE) {
	    	GameMode.updateScore(COLLECTABLE_RANK, x, y);
	    	
	    	state = Wrapper.ONLY_ANIMATION;
	    	setAction(collectableType, 1, 1, GfxObject.ACTION_DESTROYED, 0, 0);
    	}
    	else if (collectableType == COLLECTABLE_TYPE_WEAPON) {
    		hud.setCollectedWeapon(weaponType);
    		Log.e("COLLECTEDWEAPON", String.valueOf(Hud.collectedWeapon));
	    	
	    	state = Wrapper.ONLY_ANIMATION;
	    	setAction(collectableType, 1, 1, GfxObject.ACTION_DESTROYED, 0, 0);
    	}
    }

    /**
     * K�sittelee jonkin toiminnon p��ttymisen. Kutsutaan animaation loputtua, mik�li
     * <i>actionActivated</i> on TRUE.<br /><br />
     * 
     * K�ytet��n seuraavasti:<br />
     * <ul>
     *   <li>1. Objekti kutsuu funktiota <b>setAction</b>, jolle annetaan parametreina haluttu animaatio,
     *     animaation toistokerrat, animaation nopeus, toiminnon tunnus (vakiot <b>GfxObject</b>issa).
     *     Toiminnon tunnus tallennetaan <i>actionId</i>-muuttujaan.
     *     		<ul><li>-> Lis�ksi voi antaa my�s jonkin animaation ruudun j�rjestysnumeron (alkaen 0:sta)
     *     		   ja ajan, joka siin� ruudussa on tarkoitus odottaa.</li></ul></li>
     *  <li>2. <b>GfxObject</b>in <b>setAction</b>-funktio kutsuu startAnimation-funktiota (sis�lt�� my�s
     *     <b>GfxObject</b>issa), joka k�ynnist�� animaation asettamalla <i>usedAnimation</i>-muuttujan arvoksi
     *     kohdassa 1 annetun animaation tunnuksen.</li>
     *  <li>3. <b>GLRenderer</b> p�ivitt�� animaatiota kutsumalla <b>GfxObject</b>in <b>update</b>-funktiota.</li>
     *  <li>4. Kun animaatio on loppunut, kutsuu <b>update</b>-funktio koko ketjun aloittaneen objektin
     *     <b>triggerEndOfAction</b>-funktiota (funktio on abstrakti, joten alaluokat luovat siit� aina
     *     oman toteutuksensa).</li>
     *  <li>5. <b>triggerEndOfAction</b>-funktio tulkitsee <i>actionId</i>-muuttujan arvoa, johon toiminnon tunnus
     *     tallennettiin, ja toimii sen mukaisesti.</li>
     * </ul>
     * 
     * Funktiota k�ytet��n esimerkiksi objektin tuhoutuessa, jolloin se voi asettaa itsens�
     * "puoliaktiiviseen" tilaan (esimerkiksi 2, eli ONLY_ANIMATION) ja k�ynnist�� yll� esitetyn
     * tapahtumaketjun. Objekti tuhoutuu asettumalla tilaan 0 (INACTIVE) vasta ketjun p��tytty�.
     * Tuhoutuminen toteutettaisiin triggerEndOfAction-funktion sis�ll�.
     * 
     * Toimintojen vakiot l�ytyv�t GfxObject-luokan alusta.
     */
	@Override
	protected void triggerEndOfAction()
	{
		if (actionId == GfxObject.ACTION_DESTROYED) {
			setActive();
		}
	}
}
