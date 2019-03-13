package csgoskinmanager;

import java.util.Arrays;

/**
 * This class holds data about each item in the users inventory. It will have
 * data like weapon type (ex. AK-47) (String), skin name (String), float value
 * (float), rarity (int), stattrak (boolean), and link to screenshot (string).
 */
public class Weapon {

	private String weaponType;
	private String name;
	private float floatValue;
	private String rarity;
	private boolean stattrak;
	private String[] stickers;
	private String screenshot;

	public Weapon(String weaponType, String name, float floatValue, String rarity, boolean stattrak, String[] stickers, String screenshot) {
		this.weaponType = weaponType;
		this.name = name;
		this.floatValue = floatValue;
		this.rarity = rarity;
		this.stattrak = stattrak;
		this.stickers = stickers;
		this.screenshot = screenshot;
	}

	public String getWeaponType() {
		return weaponType;
	}

	public void setWeaponType(String weaponType) {
		this.weaponType = weaponType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getFloatValue() {
		return floatValue;
	}

	public void setFloatValue(float floatValue) {
		this.floatValue = floatValue;
	}

	public String getRarity() {
		return rarity;
	}

	public void setRarity(String rarity) {
		this.rarity = rarity;
	}

	public boolean isStattrak() {
		return stattrak;
	}

	public String[] getStickers() {
		return stickers;
	}

	public void setStickers(String[] stickers) {
		this.stickers = stickers;
	}

	public void setStattrak(boolean stattrak) {
		this.stattrak = stattrak;
	}

	public String getScreenshot() {
		return screenshot;
	}

	public void setScreenshot(String screenshot) {
		this.screenshot = screenshot;
	}

	@Override
	public String toString() {
		return "Weapon{" + "weaponType=" + weaponType + ", name=" + name + ", floatValue=" + floatValue + ", rarity=" + rarity + ", stattrak=" + stattrak + ", stickers=" + Arrays.toString(stickers) + " , screenshot=" + screenshot + '}';
	}

}
