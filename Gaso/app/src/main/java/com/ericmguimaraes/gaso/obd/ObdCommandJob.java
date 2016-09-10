/*
 *     Gaso
 *
 *     Copyright (C) 2016  Eric Guimarães
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ericmguimaraes.gaso.obd;

import com.github.pires.obd.commands.ObdCommand;

/**
 * This class represents a job that ObdGatewayService will have to execute and
 * maintain until the job is finished. It is, thereby, the application
 * representation of an ObdCommand instance plus a state that will be
 * interpreted and manipulated by ObdGatewayService.
 */
public class ObdCommandJob {

    private Long _id = -1L;
    private ObdCommand _command;
    private ObdCommandJobState _state;

    /**
     * Default ctor.
     *
     * @param command the ObCommand to encapsulate.
     */
    public ObdCommandJob(Long id, ObdCommand command) {
        _command = command;
        _state = ObdCommandJobState.NEW;
        _id = id;
    }

    /**
     * Default ctor.
     *
     * @param command the ObCommand to encapsulate.
     */
    public ObdCommandJob(ObdCommand command) {
        _command = command;
        _state = ObdCommandJobState.NEW;
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        _id = id;
    }

    public ObdCommand getCommand() {
        return _command;
    }

    /**
     * @return job current state.
     */
    public ObdCommandJobState getState() {
        return _state;
    }

    /**
     * Sets a new job state.
     *
     * @param state the new job state.
     */
    public void setState(ObdCommandJobState state) {
        _state = state;
    }

    /**
     * The state of the command.
     */
    public enum ObdCommandJobState {
        NEW,
        RUNNING,
        FINISHED,
        EXECUTION_ERROR,
        BROKEN_PIPE,
        QUEUE_ERROR,
        NOT_SUPPORTED
    }

    public void set_command(ObdCommand _command) {
        this._command = _command;
    }
}