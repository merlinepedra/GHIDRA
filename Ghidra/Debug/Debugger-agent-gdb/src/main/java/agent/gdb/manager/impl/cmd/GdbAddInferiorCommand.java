/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package agent.gdb.manager.impl.cmd;

import agent.gdb.manager.GdbInferior;
import agent.gdb.manager.GdbManager;
import agent.gdb.manager.evt.GdbCommandDoneEvent;
import agent.gdb.manager.impl.GdbManagerImpl;
import agent.gdb.manager.impl.GdbPendingCommand;

/**
 * Implementation of {@link GdbManager#addInferior()}
 */
public class GdbAddInferiorCommand extends AbstractGdbCommand<GdbInferior> {
	public GdbAddInferiorCommand(GdbManagerImpl manager) {
		super(manager);
	}

	@Override
	public String encode() {
		return "-add-inferior";
	}

	@Override
	public GdbInferior complete(GdbPendingCommand<?> pending) {
		GdbCommandDoneEvent done = pending.checkCompletion(GdbCommandDoneEvent.class);
		int iid = done.assumeInferior();
		return manager.getInferior(iid);
	}
}
