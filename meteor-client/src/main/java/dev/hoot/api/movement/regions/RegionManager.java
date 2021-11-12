package dev.hoot.api.movement.regions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.hoot.api.entities.Players;
import dev.hoot.api.game.Game;
import dev.hoot.api.movement.Reachable;
import dev.hoot.api.scene.Tiles;
import net.runelite.api.CollisionData;
import net.runelite.api.CollisionDataFlag;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.coords.Direction;
import okhttp3.*;
import org.sponge.util.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
public class RegionManager {
	private static final Set<Integer> SENT_REGIONS = new HashSet<>();
	private static final Logger logger = new Logger("RegionManager");
	private static final MediaType JSON_MEDIATYPE = MediaType.parse("application/json");
	private static final String API_URL = "http://20.90.16.60:8080/regions";

	@Inject
	private OkHttpClient okHttpClient;

	private final Gson gson = new GsonBuilder().create();

	public void sendRegion() {
		Player local = Players.getLocal();
		int region = local.getWorldLocation().getRegionID();
		if (SENT_REGIONS.contains(region)) {
			return;
		}

		CollisionData[] col = Game.getClient().getCollisionMaps();
		if (col == null) {
			return;
		}

		List<TileFlag> tileFlags = new ArrayList<>();
		int plane = Game.getClient().getPlane();
		int[][] flags = col[plane].getFlags();
		for (int x = 0; x < flags.length; x++) {
			for (int y = 0; y < flags[x].length; y++) {
				int tileX = x + Game.getClient().getBaseX();
				int tileY = y + Game.getClient().getBaseY();
				int flag = flags[x][y];

				Tile tile = Tiles.getAt(tileX, tileY, plane);
				if (tile == null) {
					continue;
				}

				TileFlag tileFlag = new TileFlag(tileX, tileY, plane, flag, region, x, y);
				for (Direction direction : Direction.values()) {
					switch (direction) {
						case NORTH -> {
							if (Reachable.hasDoor(tile, direction) || Reachable.hasDoor(tile.getWorldLocation().dy(1), Direction.SOUTH)) {
								tileFlag.setFlag(tileFlag.getFlag() - CollisionDataFlag.BLOCK_MOVEMENT_NORTH);
							}
						}

						case EAST -> {
							if (Reachable.hasDoor(tile, direction) || Reachable.hasDoor(tile.getWorldLocation().dx(1), Direction.WEST)) {
								tileFlag.setFlag(tileFlag.getFlag() - CollisionDataFlag.BLOCK_MOVEMENT_EAST);
							}
						}
						case WEST -> {
							if (Reachable.hasDoor(tile, direction) || Reachable.hasDoor(tile.getWorldLocation().dx(-1), Direction.EAST)) {
								tileFlag.setFlag(tileFlag.getFlag() - CollisionDataFlag.BLOCK_MOVEMENT_WEST);
							}
						}

						case SOUTH -> {
							if (Reachable.hasDoor(tile, direction) || Reachable.hasDoor(tile.getWorldLocation().dy(-1), Direction.NORTH)) {
								tileFlag.setFlag(tileFlag.getFlag() - CollisionDataFlag.BLOCK_MOVEMENT_SOUTH);
							}
						}
					}
				}

				tileFlags.add(tileFlag);
			}
		}

		try {
			String json = gson.toJson(tileFlags);
			RequestBody body = RequestBody.create(JSON_MEDIATYPE, json);
			Request request = new Request.Builder()
							.post(body)
							.url(API_URL)
							.build();
			Response response = okHttpClient.newCall(request)
							.execute();
			int code = response.code();
			if (code != 200) {
				logger.error("Request was unsuccessful: {}", code);
				return;
			}

			logger.debug("Region saved successfully");
			SENT_REGIONS.add(region);
		} catch (Exception e) {
			logger.error("Failed to POST: {}", e.getMessage());
			e.printStackTrace();
		}
	}
}
