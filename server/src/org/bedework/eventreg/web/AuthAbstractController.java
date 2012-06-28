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
package org.bedework.eventreg.web;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Ensure user is authenticated.
 *
 */
public abstract class AuthAbstractController extends AbstractController {
  @Override
  protected ModelAndView setup(final HttpServletRequest request) throws Throwable {
    ModelAndView mv = super.setup(request);

    if (mv != null) {
      return mv;
    }

    if (sessMan.getCurrentUser() == null) {
      return errorReturn("Not authenticated");
    }

    return null;
  }
}
