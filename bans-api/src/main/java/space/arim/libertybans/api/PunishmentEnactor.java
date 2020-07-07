/* 
 * LibertyBans-api
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * LibertyBans-api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * LibertyBans-api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with LibertyBans-api. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Affero General Public License.
 */
package space.arim.libertybans.api;

import space.arim.universal.util.concurrent.CentralisedFuture;

/**
 * Guardkeeper of adding punishments while enforcing constraints and reporting such enforcement.
 * For example, one victim cannot have more than 1 ban.
 * 
 * @author A248
 *
 */
public interface PunishmentEnactor {
	
	/**
	 * Enacts a punishment, adding it to the database. <br>
	 * If the punishment type is a ban or mute, and there is already a ban or mute for the user,
	 * the future will yield {@code null}. <br>
	 * <br>
	 * Assuming the caller wants the punishment to be enforced, {@link PunishmentEnforcer#enforce(Punishment)}
	 * should be called after enaction.
	 * 
	 * @param draftPunishment the draft punishment to enact
	 * @return a centralised future which yields the punishment or {@code null} if there was a conflict
	 */
	CentralisedFuture<Punishment> enactPunishment(DraftPunishment draftPunishment);
	
	/**
	 * Undoes an existing punishment in the database. <br>
	 * If the punishment existed and was removed, the future yields {@code true}, else {@code false}.
	 * 
	 * @param punishment the punishment to undo
	 * @return a centralised future which yields {@code true} if the punishment existing and was removed, {@code false} otherwise
	 */
	CentralisedFuture<Boolean> undoPunishment(Punishment punishment);
	
}
