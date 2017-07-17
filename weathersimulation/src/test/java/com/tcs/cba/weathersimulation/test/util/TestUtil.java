/**
 * Copyright (c) 2017, Sruthi Sasikumar. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 
package com.tcs.cba.weathersimulation.test.util;

import com.tcs.cba.weathersimulation.utils.Util;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class TestUtil{
    List<Double> averageList = new ArrayList<Double>();

    @Before
    public void initialiseDoubleList(){
        averageList.add(10.0);
        averageList.add(23.14);
        averageList.add(26.0);
        averageList.add(13.2);
        averageList.add(7.8);
        averageList.add(-1.2);
    }

    @Test
    public void testAverage(){
        assertEquals(13.156666666666666, Util.getAverage(averageList));
    }

    @Test
    public void testRoundedValue(){
        assertEquals(13.2, Util.roundedValue(Util.getAverage(averageList)));
    }

    @Test
    public void testGetSimpleResultantIndex(){
        assertEquals(6, Util.getResultantIndex(6,10));
    }

    @Test
    public void testGetCircularResultantIndex(){
        assertEquals(2, Util.getResultantIndex(12,10));
    }

    @Test
    public void testGetSimpleIndexOfVariance(){
        assertEquals(2, Util.getIndexOfVariance(5,3,10));
    }

    @Test
    public void testGetCircularIndexOfVariance(){
        assertEquals(8, Util.getIndexOfVariance(1,3,10));
    }
}