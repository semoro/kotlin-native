/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package kotlin.text

/**
 * This class represent atomic group (?>X), once X matches, this match become unchangeable till the end of the match.
 *
 * @author Nikolay A. Kuznetsov
 */
open internal class AtomicJointSet(children: List<AbstractSet>, fSet: FSet) : NonCapturingJointSet(children, fSet) {

    /** Returns startIndex+shift, the next position to match */
    override fun matches(startIndex: Int, testString: CharSequence, matchResult: MatchResultImpl): Int {
        val start = matchResult.getConsumed(groupIndex)
        matchResult.setConsumed(groupIndex, startIndex)
        children.forEach {
            val shift = it.matches(startIndex, testString, matchResult)
            if (shift >= 0) {
                // AtomicFset always returns true, but saves the index to run this next.match() from;
                // TODO: Try to get rid of this explicit cast to AtomicFSet
                return next.matches((fSet as AtomicFSet).index, testString, matchResult) // TODO: We use next here.
            }
        }

        matchResult.setConsumed(groupIndex, start)
        return -1
    }

    override val name: String
        get() = "AtomicJointSet"

    // TODO: looks like we can replace it with just next property.
    override var next: AbstractSet = dummyNext
}