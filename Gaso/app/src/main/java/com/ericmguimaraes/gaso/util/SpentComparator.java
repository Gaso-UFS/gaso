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

package com.ericmguimaraes.gaso.util;

import com.ericmguimaraes.gaso.model.Spent;

import java.util.Comparator;

/**
 * Created by ericm on 3/27/2016.
 */
public class SpentComparator implements Comparator<Spent> {

    @Override
    public int compare(Spent lhs, Spent rhs) {
        if(lhs.getDate().before(rhs.getDate()))
            return -1;
        else if (lhs.getDate().after(rhs.getDate()))
            return 1;
        else
            return 0;
    }

}
