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

package org.bedework.eventreg.spring.db;

import org.bedework.eventreg.spring.bus.Event;

import java.util.List;

public interface EmpacregDao {
    public int registerUserInEvent(String email, Event eif, int numTickets, String comment, String regType, String userType);
    public boolean validateEmail(String activationCode);
    public List getUserRegistrations(String email);

    public List getRegistrations(String eventGUID);

    public int getRegistrantCount(String eventGUID);

    public int getTicketCount(String eventGUID);
    public int getUserTicketCount(String eventGUID, String email);
    public void removeTicketById(int id, String email, String userType) throws Throwable;
    public void updateTicketById(int id, String email, int numTickets, String comment, String type, String userType) throws Throwable;
}


