/* ********************************************************************
Licensed to Jasig under one or more contributor license
agreements. See the NOTICE file distributed with this work
for additional information regarding copyright ownership.
Jasig licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a
copy of the License at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on
an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the
specific language governing permissions and limitations
under the License.
 */

package org.bedework.eventreg.bus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class URLReader {
  public static String read(final String url) throws Throwable {
    String theText = "";
    BufferedReader in = null;

    try {
      URL theURL = new URL(url);
      in = new BufferedReader(new InputStreamReader(theURL.openStream()));

      String inputLine;

      while ((inputLine = in.readLine()) != null) {
        if (inputLine.length() > 0) {
          theText = theText.concat(inputLine);
        }
      }
    } finally {
      in.close();
    }

    return theText;
  }

}