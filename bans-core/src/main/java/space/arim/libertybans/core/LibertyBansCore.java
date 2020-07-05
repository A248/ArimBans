/* 
 * LibertyBans-core
 * Copyright © 2020 Anand Beh <https://www.arim.space>
 * 
 * LibertyBans-core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * LibertyBans-core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with LibertyBans-core. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Affero General Public License.
 */
package space.arim.libertybans.core;

import java.io.File;

import space.arim.universal.events.EventPriority;
import space.arim.universal.events.Listener;
import space.arim.universal.registry.Registry;
import space.arim.universal.registry.RequireServices;
import space.arim.universal.registry.ServiceChangeEvent;
import space.arim.universal.util.concurrent.FactoryOfTheFuture;

import space.arim.api.env.DetectingPlatformHandle;
import space.arim.api.env.PlatformPluginInfo;

import space.arim.libertybans.api.LibertyBans;
import space.arim.libertybans.api.RunState;
import space.arim.libertybans.core.env.AbstractEnv;

public class LibertyBansCore implements LibertyBans, Part {

	private final File folder;
	private final Registry registry;
	private final AbstractEnv environment;
	
	private final Database database;
	private final Selector selector;
	private final Enactor enactor;
	private final Scoper scoper;
	
	private final UUIDMaster uuidMaster;
	private final Configs configs;
	private final Commands commands;
	
	private Listener unregistrationListener;
	
	public LibertyBansCore(@RequireServices(PlatformPluginInfo.class) Registry registry,
			File folder, AbstractEnv environment) {
		this.folder = folder;
		this.registry = registry;
		this.environment = environment;

		database = new Database(this);
		selector = new Selector(this);
		enactor = new Enactor(this);
		uuidMaster = new UUIDMaster(this);
		configs = new Configs(this);
		commands = new Commands(this);
		scoper = new Scoper();
	}
	
	@Override
	public void startup() {
		new DetectingPlatformHandle(registry).registerDefaultServiceIfAbsent(FactoryOfTheFuture.class);
		unregistrationListener = registry.getEvents().registerListener(ServiceChangeEvent.class, EventPriority.NORMAL, (evt) -> {
			if (evt.getService() == FactoryOfTheFuture.class && evt.getUpdated() == null) {
				environment.shutdown();
			}
		});
		database.startup();
		configs.startup();
		selector.startup();
		uuidMaster.startup();
	}
	
	File getFolder() {
		return folder;
	}
	
	AbstractEnv getEnvironment() {
		return environment;
	}
	
	@Override
	public RunState getRunState() {
		return environment.getRunState();
	}
	
	@Override
	public Registry getRegistry() {
		return registry;
	}
	
	@Override
	public FactoryOfTheFuture getFuturesFactory() {
		return registry.getRegistration(FactoryOfTheFuture.class).getProvider();
	}

	@Override
	public Database getDatabase() {
		return database;
	}
	
	@Override
	public Selector getSelector() {
		return selector;
	}

	@Override
	public Enactor getEnactor() {
		return enactor;
	}
	
	@Override
	public Scoper getScopeManager() {
		return scoper;
	}
	
	public UUIDMaster getUUIDMaster() {
		return uuidMaster;
	}
	
	public Configs getConfigs() {
		return configs;
	}
	
	public Commands getCommands() {
		return commands;
	}

	@Override
	public void shutdown() {
		registry.getEvents().unregisterListener(unregistrationListener);
		uuidMaster.shutdown();
		selector.shutdown();
		configs.shutdown();
		database.shutdown();
	}
	
}