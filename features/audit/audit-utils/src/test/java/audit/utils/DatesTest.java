/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package audit.utils;

import org.junit.Test;

import java.util.Calendar;

import static audit.util.Dates.format;
import static java.time.ZoneOffset.UTC;
import static java.util.Calendar.*;
import static java.util.TimeZone.getTimeZone;
import static org.assertj.core.api.Assertions.assertThat;

public class DatesTest {

    @Test
    public void shouldConvertToCalendarAndString() {
        String expected = "2016/04/08 15:02:05:123 +0200";

        Calendar converted = format(expected);

        assertThat(converted.get(YEAR))
                .isEqualTo(2016);
        assertThat(converted.get(MONTH))
                .isEqualTo(3);
        assertThat(converted.get(DAY_OF_MONTH))
                .isEqualTo(8);
        assertThat(converted.get(HOUR_OF_DAY))
                .isEqualTo(15);
        assertThat(converted.get(MINUTE))
                .isEqualTo(2);
        assertThat(converted.get(SECOND))
                .isEqualTo(5);
        assertThat(converted.get(MILLISECOND))
                .isEqualTo(123);
        assertThat(converted.get(ZONE_OFFSET))
                .isEqualTo(2 * 3600 * 1000);

        String convertedBack = format(converted);

        assertThat(convertedBack)
                .isEqualTo(expected);
    }

    @Test
    public void shouldConvertCalendar() {
        Calendar expected = Calendar.getInstance();

        String converted = format(expected);

        Calendar convertedBack = format(converted);

        assertThat(convertedBack.getTime())
                .isEqualTo(expected.getTime());
    }

    @Test
    public void shouldConvertCalendarAsUTC() {
        Calendar expected = Calendar.getInstance(getTimeZone(UTC));

        String converted = format(expected);

        Calendar convertedBack = format(converted);

        assertThat(convertedBack.get(YEAR))
                .isEqualTo(expected.get(YEAR));
        assertThat(convertedBack.get(MONTH))
                .isEqualTo(expected.get(MONTH));
        assertThat(convertedBack.get(DAY_OF_MONTH))
                .isEqualTo(expected.get(DAY_OF_MONTH));
        assertThat(convertedBack.get(HOUR_OF_DAY))
                .isEqualTo(expected.get(HOUR_OF_DAY));
        assertThat(convertedBack.get(MINUTE))
                .isEqualTo(expected.get(MINUTE));
        assertThat(convertedBack.get(SECOND))
                .isEqualTo(expected.get(SECOND));
        assertThat(convertedBack.get(MILLISECOND))
                .isEqualTo(expected.get(MILLISECOND));
        assertThat(convertedBack.get(ZONE_OFFSET))
                .isEqualTo(expected.get(ZONE_OFFSET));

        assertThat(convertedBack.getTime())
                .isEqualTo(expected.getTime());
    }
}
