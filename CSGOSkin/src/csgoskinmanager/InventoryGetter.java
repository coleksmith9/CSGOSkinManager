package csgoskinmanager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class will be responsible for taking the Steam URL and getting all of
 * the data about the user's inventory. This includes parsing the JSON file,
 * formatting the data, and creating a Weapon object for each item in the
 * inventory.
 */
public class InventoryGetter {

	private String finalLink;
	private JSONObject data;

	public ArrayList<Weapon> getInventory(String url) {
		JSONParser parser = new JSONParser();
		ArrayList<Weapon> list = new ArrayList();
		try {
			/**
			 * Steam IDs. Cole: 76561198070824838 Ezana: 76561198139274163 Ryan:
			 * 76561198054859030 Jackson: 76561198084885575
			 */
			// all of the data that Steam returns
			JSONObject inventoryData = (JSONObject) (parser.parse(IOUtils.toString(new URL("https://steamcommunity.com/inventory/" + url + "/730/2"), "UTF-8")));
			// Uncomment to use test files
			// JSONObject inventoryData = (JSONObject) (parser.parse(new FileReader(new File("src/csgoskinmanager/TestJSONData_wbcw.json"))));

			// used to get inspect link for each item
			JSONArray descriptions = (JSONArray) inventoryData.get("descriptions");

			// used to fill assetid in for inspect links
			JSONArray assets = (JSONArray) inventoryData.get("assets");

			// used to loop through each item in the inventory
			int totalCount = descriptions.size();

			// Prints out "working" to show progress
			System.out.print("Working");
			// loops through whole inventory
			for (int i = 0; i < totalCount; i++) {
				// gets data for each item
				JSONObject item = (JSONObject) descriptions.get(i);

				// "A" parameter for API (asset ID)
				String aParameter = ((JSONObject) assets.get(i)).get("assetid").toString();
				String type = item.get("type").toString();

				// filters out graffiti and collectibles such as service medals
				if (type.contains("Pistol") || type.contains("SMG") || type.contains("Rifle") || type.contains("Sniper") || type.contains("Knife") || type.contains("Machinegun") || type.contains("Shotgun")) {

					// gets the link from the descriptions section of the JSON data
					String inspectLink = ((JSONObject) ((JSONArray) item.get("actions")).get(0)).get("link").toString();

					// uses the inspect URL to get the "D" parameter for the API
					Scanner choppa = new Scanner(inspectLink);
					choppa.useDelimiter("D");
					choppa.next();
					String dParameter = choppa.next();

					// formats the inspect link for the getScreenshot() method
					// "steam://rungame/730/76561202255233023/+csgo_econ_action_preview%20S%owner_steamid%A%assetid%D6971624731879957313"
					Scanner chopper = new Scanner(inspectLink);
					chopper.useDelimiter("%");
					finalLink = chopper.next() + "%20S" + url;
					chopper.next(); // removes owner_steamid%
					finalLink += "A" + aParameter;
					chopper.next(); // removes assetid%
					finalLink += "D" + dParameter;

					// attempts to get data from the CSGOFloat API
					try {
						data = ((JSONObject) ((JSONObject) parser.parse(IOUtils.toString(new URL("https://api.csgofloat.com/?s=" + url + "&a=" + aParameter + "&d=" + dParameter), "UTF-8"))).get("iteminfo"));

						// if the API responds correctly, the data will be put into a Weapon object
						String weaponType = getWeaponType();
						String name = getName();
						float floatValue = getFloatValue();
						String rarity = getRarity();
						boolean stattrak = getStattrak();
						String[] stickers = getStickers();
						String screenshot = getScreenshot(finalLink);

						// each weapon is added to the list
						list.add(new Weapon(weaponType, name, floatValue, rarity, stattrak, stickers, screenshot));

						// prints dots to display progress
						System.out.print(".");
					} catch (IOException ex) {
						System.out.println("IOException: " + ex);
					}
				}
			}

		} catch (IOException | ParseException | IndexOutOfBoundsException ex) {
			System.out.println("IO, Parse, or IndexOutOfBounds exception: " + ex);
		}
		System.out.println("\n\n\n\n");
		return list;
	}

	private String getWeaponType() {
		return data.get("weapon_type").toString();
	}

	private String getName() {
		return data.get("item_name").toString();
	}

	private float getFloatValue() {
		return Float.parseFloat(data.get("floatvalue").toString());
	}

	private String getRarity() {
		return data.get("rarity").toString();
	}

	private boolean getStattrak() {
		return data.get("full_item_name").toString().contains("StatTrak");
	}

	private String[] getStickers() {
		String[] stickers = new String[4];
		JSONArray rawData = (JSONArray) data.get("stickers");

		for (Object slot : rawData) {
			int slotNum = Integer.parseInt(((JSONObject) slot).get("slot").toString());
			stickers[slotNum] = ((JSONObject) slot).get("name").toString();
		}

		// removes null from each array, puts "" in its place
		for (int i = 0; i < 4; i++) {
			if (stickers[i] == null) {
				stickers[i] = "None";
			}
		}

		if (Arrays.toString(stickers).equals("[None, None, None, None]")) {
			return new String[]{"None"};
		}
		return stickers;
	}

	public String getScreenshot(String url) {
		URL urlTmp = null;
		String redUrl = null;
		HttpURLConnection connection = null;

		try {
			urlTmp = new URL("http://csgo.gallery/" + url);
		} catch (MalformedURLException ex) {
			Logger.getLogger(InventoryGetter.class.getName()).log(Level.SEVERE, null, ex);
		}

		try {
			connection = (HttpURLConnection) urlTmp.openConnection();
		} catch (IOException ex) {
			Logger.getLogger(InventoryGetter.class.getName()).log(Level.SEVERE, null, ex);
		}

		try {
			connection.getResponseCode();
		} catch (IOException ex) {
			Logger.getLogger(InventoryGetter.class.getName()).log(Level.SEVERE, null, ex);
		}

		redUrl = connection.getURL().toString();
		connection.disconnect();

		return redUrl;
	}
}
